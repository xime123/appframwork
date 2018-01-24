package com.yap.webapp.was.webruntime;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.os.Process;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.CookieSyncManager;

import com.yap.webapp.plugin.Plugin;
import com.yap.webapp.plugin.PluginManager;

import java.util.HashMap;
import java.util.Map;

public class WasEngine implements WasWebview.LoadObserver {
	private static String TAG = "WasEngine";

	private WasWebview mWebview;
	private WasWebviewContainer mWebviewContainer;
	private Context mContext;// Application上下文
	private Activity mActivity;// 活动的上下文，注意沙箱界面关闭之后就不能使用了
	private int netType = 0;// 网络状态。1在线；0离线；
	private static WasEngine instance = new WasEngine();
	private Object startAppLockObj = new Object();
	private String appID;
	private StatusMonitor mStatusMonitor;

	private WasEngine() {
	}

	public static WasEngine getInstance() {
		return instance;
	}

	public void init(Context context, StatusMonitor status) {
		CookieSyncManager.createInstance(context);

		mContext = context;
		mStatusMonitor = status;

		PluginManager.getInstance().loadPlugins();

		// 这个handler必须在UI线程中创建出来
		mHandler = new MyHandler();
	}
	
	public int getNetState() {
		return this.netType;
	}

	public Context getApplicationContext() {
		return this.mContext;
	}

	public Activity getActivityContext() {
		return this.mActivity;
	}

	public void setActivityContext(Activity act) {
		Log.d(TAG, "setActivityContext " + (act == null));
		this.mActivity = act;
	}

	public String getAppID() {
		return this.appID;
	}

	private Handler mHandler;
	public static final int CMD_CALL_APP = 1;
	public static final int CMD_EXIT_APP = 2;
	public static final int CMD_KILL_APP = 3;
	public static final int CMD_PAUSE_APP = 4;
	public static final int CMD_NET_STATE_CHANGE = 9;
	public static final int CMD_PHONE_CALL = 10;
	public static final int CMD_BACK_KEY_PRESSED = 99;

	private class MyHandler extends Handler {
		public void handleMessage(Message msg) {
			if (msg.what == CMD_CALL_APP) {
				// 调用应用
				Log.e(TAG, "WasEngine.this.callApp end...");
			} else if (msg.what == CMD_EXIT_APP) {
				// 退出应用
				WasEngine.this.exitApp();
			} else if (msg.what == CMD_KILL_APP) {
				// 杀死应用
				WasEngine.this.killApp();
			} else if (msg.what == CMD_PAUSE_APP) {
				// 退出应用
				WasEngine.this.pauseApp();
			} else if (msg.what == CMD_BACK_KEY_PRESSED) {
				// 按了返回键
				if (mWebview != null) {
					mWebview.fireEvent("onBackKeyPressed", null);
				}
			} else if (msg.what == CMD_NET_STATE_CHANGE) {
				// 网络状态变化了
				HashMap<String, String> eventData = new HashMap<String, String>();
				eventData.put("netState", String.valueOf(netType));
				mWebview.execJS("Was.netState=" + netType + ";");
				mWebview.fireEvent("onNetStateChange", eventData);
			} else if (msg.what == CMD_PHONE_CALL) {
				// 电话状态变化
				Object[] args = (Object[]) msg.obj;
				int type = (Integer) args[0];
				String incomeNumber = "";
				if (args.length > 1) {
					incomeNumber = (String) args[1];
				}
				HashMap<String, String> eventData = new HashMap<String, String>();
				eventData.put("phoneState", String.valueOf(type));
				eventData.put("param", incomeNumber);
				mWebview.fireEvent("onPhoneStateChange", eventData);
			}
		}
	}

	/**
	 * 需要UI线程来处理的事请，都需要通过这个方法来调用 目前提供给应用插件/启动服务使用
	 */
	public void postCommand(int command, Object[] args) {
		Log.d(TAG, "WasEngine.postCommand(" + command + ")");
		Message msg = mHandler.obtainMessage(command, args);
		mHandler.sendMessage(msg);
	}

	public void loadApp(String appID, String indexPath, WasWebviewContainer container, Map<String, String> startArgs) {
		Log.d(TAG, "loadApp(" + appID + ")");
		synchronized (startAppLockObj) {
			loadAppInternal(appID, indexPath, container, startArgs);
		}
	}

	private void loadAppInternal(String appID, String indexPath, WasWebviewContainer container,
			Map<String, String> callArgs) {
		Log.d(TAG, "loadAppInternal(): " + appID + ", " + indexPath);

		this.appID = appID;
		this.mWebviewContainer = container;

//		if (mWebview != null) {
//			this.resumeAppInternal(appID, container, callArgs);
//			return;
//		}

		// 开始装载
		container.onLoading();

		Was.getInstance().notifyAppStart(appID, String.valueOf(Process.myPid()));

		if(mStatusMonitor != null) {
			netType = mStatusMonitor.getNetType();
		} else {
			netType = 1;
		}
		// ville
		// 初始化一个WasWebview
		WasWebview view = new WasWebview(appID, mStatusMonitor, mActivity);
		view.setLoadObserver(this);
		mWebview = view;

		// 把view放置到container中。
		container.putWebview(view.getWebview());
		container.setScreenOrientation(view.getContext().getScreenOrientation());

		// 装载index页
		String loadUrl = createLoadUrl(indexPath);
		Log.d(TAG, "AAAA loadURL " + loadUrl);
		view.loadUrl(loadUrl);
		// 为在onCreate事件中传递callArgs数据，把数据放到WebviewContext里面
		// WasWebviewClient.onPageFinished中读取数据
		if (callArgs == null) {
			callArgs = new HashMap<String, String>();
		}
		callArgs.put("__netState", String.valueOf(netType));
		view.getContext().setParam("__onCreateEventData", callArgs);
		Was.getInstance().notifyAppResume(appID);
	}

	private String createLoadUrl(String path) {
		String loadUrl = path;
		if(path != null && path.length() > 0){
			if(path.startsWith("http://") || path.startsWith("https://")) {
				loadUrl = path;
			}
		}
		return loadUrl;
	}
	
	private void resumeAppInternal(String appID, WasWebviewContainer container,	Map<String, String> callArgs) {
		Log.d(TAG, "resumeAppInternal()");

		container.setScreenOrientation(mWebview.getContext().getScreenOrientation());

		// 发布应用继续事件到应用
		if (callArgs == null) {
			callArgs = new HashMap<String, String>();
		}
		callArgs.put("__netState", String.valueOf(netType));
		mWebview.fireEvent("onResume", callArgs);

		Was.getInstance().notifyAppResume(appID);
	}

	public void pauseApp() {
		Log.d(TAG, "pauseApp(" + appID + ")");

		// 发布应用暂停事件到应用
		mWebview.fireEvent("onPause", null);
		mWebviewContainer.onHome();

		Was.getInstance().notifyAppPaused(appID);
	}

	public boolean exitApp() {
		Log.d(TAG, "exitApp(" + appID + ")");

		if (mWebview == null) {
			return false;
		}
		
		hideSoftInput(mActivity);
		
		// 发布应用暂停/退出事件到应用
		mWebview.fireEvent("onPause", null);
		mWebview.fireEvent("onExit", null);

		// 把view从container中拆除
		mWebviewContainer.removeWebview(mWebview.getWebview());

		// 释放资源
		mWebview.destroy();
		mWebview = null;

		Was.getInstance().notifyAppExited(appID, mWebviewContainer, false);

		mWebviewContainer.onClose();
		mWebviewContainer = null;

		return true;
	}

	public WasWebview getWasWebView(){
		return mWebview;
	}
	private boolean killApp() {
		Log.d(TAG, "killApp(" + appID + ")");

		if (mWebview == null) {
			return false;
		}

		// 发布应用退出事件到应用
		mWebview.fireEvent("onPause", null);
		mWebview.fireEvent("onExit", null);

		// 把view从container中拆除
		mWebviewContainer.removeWebview(mWebview.getWebview());

		// 释放资源
		mWebview.destroy();
		mWebview = null;

		Was.getInstance().notifyAppExited(appID, mWebviewContainer, true);

		mWebviewContainer.onClose();
		mWebviewContainer = null;

		return true;
	}

	public void sendPushMsg2App(Map<String, String> msg) {
		Log.d(TAG, "sendPushMsg2App(" + msg + ")");
		mWebview.fireEvent("onPushMessage", msg);
	}

	// 销毁沙盒
	public void destroy() {
		Log.d(TAG, "destroy ActivityContext ");
		mContext = null;
		mActivity = null;
	}

	@Override
	public void onLoadFinish() {
		this.mWebviewContainer.onLoadfinish();
	}

	public Plugin getPlugin(String pluginName) {
		return PluginManager.getInstance().getPlugin(pluginName);
	}
	
	public WasWebviewContext getWebviewContext() {
		if(mWebview != null) {
			return mWebview.getContext();
		}
		return null;
	}
	
	// 隐藏软键盘
	public void hideSoftInput(Activity activity) {
		InputMethodManager inputMethodManager = (InputMethodManager) activity.
				getSystemService(Context.INPUT_METHOD_SERVICE);
		View decorView = activity.getWindow().getDecorView();
		if (inputMethodManager != null && decorView != null) {
			inputMethodManager.hideSoftInputFromWindow(decorView.getWindowToken(), 0);
		}
	}
	
	public boolean hasRegisterBackKey() {
		if(mWebview != null) {
			return mWebview.hasRegisterBackKey();
		}
		return false;
	}

}
