package com.yap.webapp.was.webruntime;

import android.content.Context;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.Toast;

import com.app.util.FileUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class Was {

	private static String TAG = "WebRuntime";
	private static final int MAXRUNNINGAPP = 5;// 注意不能超过10！

	private static Was instance = new Was();

	private Context mContext;// Android Application Context

	private HashMap<String, WasRunningApp> mRunningAppMap;
	private WasRunningApp mActiveApp;
	private HashMap<String, Object> mAppLockMap;
	private String mCoreJSString;

	private Was() {
		mAppLockMap = new HashMap<String, Object>();
		mRunningAppMap = new HashMap<String, WasRunningApp>();
	}

	public static Was getInstance() {
		return instance;
	}

	public void init(Context context) {
		CookieSyncManager.createInstance(context);
		mContext = context;
		// easymicore.js 已经转到web上动态加载，本地不再处理该js文件
		initCoreJS(context);
	}

	private void initCoreJS(Context context) {
		InputStream input = null;
		BufferedReader reader = null;
		try {
			input = context.getAssets().open("juzix.js");
			reader = new BufferedReader(new InputStreamReader(input));
			StringBuilder sb = new StringBuilder();

			String line = null;
			while ((line = reader.readLine()) != null) {
				line = StringUtil.fitJSComment(line);
				sb.append(line + "\n");
			}
			mCoreJSString = sb.toString();
			Log.i(TAG, "mCoreJSString="+mCoreJSString);
		} catch (Exception e) {
			Log.e(TAG, "read WAS JS failed!", e);
		} finally {
			FileUtil.closeInputStream(input);
			if(reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
				}
			}
		}
	}




	public Object getAppLock(String appID) {
		synchronized (mAppLockMap) {
			Object lock = mAppLockMap.get(appID);
			if (lock == null) {
				lock = new Object();
				mAppLockMap.put(appID, lock);
			}
			return lock;
		}
	}

	// 用户签退之后的释放
	public void stopAfterUserLogout() {
		Log.d(TAG, "stopAfterUserLogout()");

		// 把现在还启动的应用，都关闭了
		String[] runningApps = this.getRunningApps();
		for (String appID : runningApps) {
			this.killApp(appID);
		}

		// 清除所有session cookie
		Log.d(TAG, "clear session cookie...");
		CookieManager.getInstance().removeSessionCookie();
		CookieSyncManager.getInstance().sync();

	}

	// 销毁沙盒
	public void destroy() {
		mContext = null;

		mAppLockMap.clear();
		mRunningAppMap.clear();
	}

	// 释放app资源，维护相关状态
	private void releaseAppResource(WasRunningApp app) {
		this.mRunningAppMap.remove(app.appID);
		if (mActiveApp != null && mActiveApp.appID.endsWith(app.appID)) {
			// 检查的这个应用，是正在运行的那个，已经死了，需要设置正在运行为null
			mActiveApp = null;
		}
	}

	public String[] getRunningApps() {
		String[] appIDs = {};
		// 把清理过的集合返回回去
		return mRunningAppMap.keySet().toArray(appIDs);
	}

	public String getCurrAppID() {
		if (mActiveApp == null) {
			return null;
		}
		return mActiveApp.appID;
	}

	public boolean isCurrApp(String appID) {
		String currAppID = this.getCurrAppID();
		if (currAppID == null) {
			return false;
		}
		return currAppID.equals(appID);
	}

	public boolean isAppRunning(String appID) {
		return mRunningAppMap.containsKey(appID);
	}

	private boolean isExceedMaxRunning() {
		return mRunningAppMap.keySet().size() >= MAXRUNNINGAPP;
	}

	public boolean killApp(String appID) {
		Log.d(TAG, "killApp(" + appID + ")");
		WasRunningApp runningApp = this.mRunningAppMap.get(appID);
		if (runningApp == null) {
			// 没有对应的应用，说明不在运行中。。
			return false;
		}
		try {
			WasEngine.getInstance().postCommand(WasEngine.CMD_KILL_APP, null);
			return true;
		} catch (Exception e) {
			Log.e(TAG, "", e);
			return false;
		}
	}

	public boolean loadApp(String appID, String indexPath, WasWebviewContainer container, Map<String, String> startArgs) {
		Log.i(TAG, "Was.loadApp(" + appID + ") indexPath=" + indexPath);
//		if (AppManager.getInstance().isAppAtInstallingState(appID)) {
//			Log.info("ZZZ", "appID>" + appID + " is installing just return..");
//			return false;
//		}
		return loadAppInternal(appID, indexPath, container, startArgs);
	}

	private boolean loadAppInternal(String appID, String indexPath, WasWebviewContainer container, Map<String, String> startArgs) {

		if (this.isExceedMaxRunning() && mRunningAppMap.get(appID) == null) {
			Toast.makeText(this.mContext, "最多只能有" + MAXRUNNINGAPP + "个应用同时运行，你必须先关闭一个才能打开新的应用。",
					Toast.LENGTH_LONG).show();
			return false;
		}

		// 判断应用是否已经启动了
		boolean isAppRunning = this.isAppRunning(appID);
		boolean isCurrApp = this.isCurrApp(appID);
		if (isAppRunning) {
			// 已运行
			WasRunningApp runningApp = this.mRunningAppMap.get(appID);

			if (isCurrApp) {

			} else {

			}

		} else {
			// 未运行
			WasRunningApp runningApp = new WasRunningApp();
			runningApp.appID = appID;
			mRunningAppMap.put(appID, runningApp);

		}

		WasEngine.getInstance().loadApp(appID, indexPath, container, startArgs);
		return true;
	}

	public void notifyAppStart(String appID, String processID) {
		Log.d(TAG, "notifyAppStart(" + appID + "," + processID + ")");

		WasRunningApp app = mRunningAppMap.get(appID);
		if (app == null) {
			Log.e(TAG, "appID " + appID + " has No specificRegarding belong");
			return;
		}
		// 找到app，把相关数据都关联进去
		app.appID = appID;

		Log.i(TAG, "appID>" + app.appID + ",processID>");
		mActiveApp = app; // 将这个应用设置为正在运行的应用
	}

	// 应用进程通知Was某个应用已经退出了
	public void notifyAppExited(String appID, WasWebviewContainer container, boolean isKill) {
		Log.d(TAG, "notifyAppExited(" + appID + "," + isKill + ")");

		WasRunningApp app = mRunningAppMap.get(appID);
		if (app == null) {
			Log.e(TAG, "appID " + appID + " has No specificRegarding belong");
			return;
		}

		// 释放资源，维护app状态
		this.releaseAppResource(app);
	}

	// 应用进程通知Was某个应用已经暂停了
	public void notifyAppPaused(String appID) {
		Log.d(TAG, "notifyAppPaused(" + appID + ")");

		WasRunningApp app = mRunningAppMap.get(appID);
		if (app == null) {
			Log.e(TAG, "appID " + appID + " has No specificRegarding belong");
			return;
		}
		// 只需要将正在运行的app置空
		mActiveApp = null;
		Log.d(TAG, "currentApp is null");
	}

	// 应用进程通知Was某个应用已经复活了
	public void notifyAppResume(String appID) {
		Log.d(TAG, "notifyAppResume(" + appID + ")");

		WasRunningApp app = mRunningAppMap.get(appID);
		if (app == null) {
			Log.e(TAG, "appID " + appID + " has No specificRegarding belong");
			return;
		}
		mActiveApp = app;
		Log.d(TAG, "currentApp >" + mActiveApp.appID);
	}

	public String getCoreJSString(){
		return mCoreJSString;
	}

}
