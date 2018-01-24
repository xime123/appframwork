package com.yap.webapp.was.webruntime;

import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;

import java.util.Map;
import java.util.Set;



public class WasWebview
{
    public enum Mode{Application, Service, Widget};
    
    private static final String TAG = "WebRuntime";
    private static final boolean DEBUG_WEBVIEW = false;

    private WebView webview;
    private WasWebviewContext context;
    private EventProcessor eventProcessor;
    private LoadObserver observer;
    private boolean appEventProcessRegister = true;//app有没有注册事件处理器。现在只要包含了WAS JS就有了
    private boolean backKeyRegister = false;
    
    private boolean isAlreadNotifyLoadObserver = false;//只能通知一次，因此搞了个标志位。艹
    
    private Mode mode;
    
    public WasWebview(String appID, StatusMonitor statusMonitor, Context androidContext)
    {
        this.webview = new WebView(androidContext);
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT){
        	WebView.setWebContentsDebuggingEnabled(DEBUG_WEBVIEW);
        }
        this.webview.setWebViewClient(new WasWebviewClient(this));
        this.webview.setWebChromeClient(new WebChromeClient());
        this.webview.addJavascriptInterface(new JS2JavaProxy(this), "__js2java_proxy");

        this.mHandler = new MyHandler();

        int version = Integer.valueOf(Build.VERSION.SDK_INT);
        if(version >= 11)
        {
            int isHardwareAccelerated = 0;

            if(isHardwareAccelerated == 0)
            {
                Log.i(TAG, "isHardwareAccelerated>>"+isHardwareAccelerated);
                this.webview.setLayerType(View.LAYER_TYPE_SOFTWARE , null);
            }
            else if(isHardwareAccelerated == 1)
            {
                Log.i(TAG, "isHardwareAccelerated>>"+isHardwareAccelerated);
                this.webview.setLayerType(View.LAYER_TYPE_HARDWARE , null);
            }
        }
        if(version<11)
        {
            this.webview.setVerticalScrollBarEnabled(false);
        }

        WebSettings settings = this.webview.getSettings();

        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setMinimumFontSize(settings.getMinimumFontSize() + 8);
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);

        this.context = new WasWebviewContext(
                appID,
                statusMonitor.getStatusParam()
                );

        this.eventProcessor = new EventProcessor(this);

    }

    private Handler mHandler;

    private class MyHandler extends Handler
    {
        public void handleMessage(Message msg)
        {
            String script = (String)msg.obj;
            WasWebview.this.execJS(script);
        }
    }

    void registerEventProcess()
    {
        this.appEventProcessRegister = true;
    }

    public boolean hasRegisterAppEventProcess() {
    	return appEventProcessRegister;
    }

    void registerBackKey(){
    	backKeyRegister = true;
    }

    public boolean hasRegisterBackKey() {
    	return backKeyRegister;
    }

    public void setLoadObserver(LoadObserver observer)
    {
        this.observer = observer;
    }

    public Mode getMode()
    {
        return mode;
    }
    public boolean isAlreadNotifyLoadObserver()
    {
        return isAlreadNotifyLoadObserver;
    }

    public WebView getWebview()
    {
        return this.webview;
    }

    public WasWebviewContext getContext()
    {
        return context;
    }

    public void loadUrl(String url)
    {
        this.webview.loadUrl(url);
    }

    private void execJSRealLogic(String script)
    {
        Log.d(TAG, "execJS("+script+")");
        try
        {
        	if(webview != null) {
        		this.webview.loadUrl("javascript:"+script);
        	}
        }
        catch(Throwable t)
        {
            Log.e(TAG, "execJS failed!",t);
        }
    }

    public void execJS(String script) {
		// Log.debug(TAG, "execJS("+script+")");
		if (Looper.myLooper() == Looper.getMainLooper()) {
			// 如果是主线程，则直接执行了
			execJSRealLogic(script);
		} else {
			// 不是主线程，需要通过handler转一道。
			if (mHandler != null) {
				Message msg = mHandler.obtainMessage(1, script);
				mHandler.sendMessage(msg);
			}
		}
	}

    /**
     * 提供给外部使用的发送事件的方法
     */
    public void fireEvent(String eventName,Map<String,String> eventData)
    {
        //应用没有注册事件处理器，我们就不给他们发送事件！
        if(this.appEventProcessRegister)
        {
            this.eventProcessor.fireEvent(eventName,eventData);
        }
    }

    public void notifyEventResult(int eventID)
    {
        this.eventProcessor.notifyEventResult(eventID);
    }

    //提供给WebviewClient用于通知页面加载完毕的方法
    void notifyLoadPageFinish()
    {
        if(!isAlreadNotifyLoadObserver && observer!=null)
        {
            //由于给外部的通知是应用加载完毕（相当于首页加载完毕），只通知一次才是合理。
            isAlreadNotifyLoadObserver = true;
            observer.onLoadFinish();
        }
    }

    //提供给EventProcessor用的发送事件方法
    void sendEvent(String eventName,int eventID,Map<String,String> eventData)
    {
        String eventDataStr = "";
        if(eventData!=null)
        {
            boolean isFirstElement = true;
            Set<String> keys = eventData.keySet();
            for(String key:keys)
            {
                if(!isFirstElement)
                {
                    eventDataStr += ',';
                }
                isFirstElement = false;
                eventDataStr += key+':'+'\''+eventData.get(key)+'\'';
            }
        }
        String script = "Was.fireEvent('"+eventName+"',"+eventID+",{"+eventDataStr+"});";
        this.execJS(script);
    }

    public void destroy()
    {
        mHandler = null;
        webview.clearCache(true);
        this.webview.addJavascriptInterface(new Object(), "__js2java_proxy");
        int version = Integer.valueOf(Build.VERSION.SDK_INT);
        if(version >= 11)
        {
            webview.removeJavascriptInterface("__js2java_proxy");
        }
        webview.freeMemory();
        webview.destroy();
        try
        {
            this.webview.setWebViewClient(null);
            this.webview.setWebChromeClient(null);
        }
        catch(Exception e)
        {
            Log.e(TAG, "clear webview client failed.",e);
        }
        this.eventProcessor= null;
        this.webview = null;
        System.gc();
    }
    
    public interface LoadObserver
    {
        void onLoadFinish();
    }

}
