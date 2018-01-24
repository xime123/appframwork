package com.yap.webapp.was.webruntime;

//import android.webkit.ConsoleMessage;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.util.Log;
import android.webkit.ConsoleMessage;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebStorage;
import android.webkit.WebView;


public class WasWebchromeClient extends WebChromeClient
{
    private static String JSTAG = "JSLog";
    
    private WasWebview webview;

    public WasWebchromeClient(WasWebview webview)
    {
        this.webview = webview;
    }
    
    //@Override
	// 这个是2.3以前的
	public void onConsoleMessage(String message, int lineNumber, String sourceID) {
		Log.d(JSTAG, "line:" + lineNumber + ",sourceID:" + sourceID + "," + message);
	}

    //@Override
    //这个是2.3以及2.3以后的
    public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
        onConsoleMessage(consoleMessage.message(),consoleMessage.lineNumber(),consoleMessage.sourceId());
        return true;
    }    

    @Override
    public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
        WasWebview.Mode mode = webview.getMode();
        if (mode == WasWebview.Mode.Application && webview.isAlreadNotifyLoadObserver())
        {
            AlertDialog.Builder b2 = new AlertDialog.Builder(WasEngine.getInstance().getActivityContext()).setTitle("Alert").setMessage(message)
                    .setPositiveButton("ok", new AlertDialog.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            result.confirm();
                        }
                    });

            b2.setCancelable(false);
            b2.create();
            b2.show();
            return true;

        }
        else {
            result.cancel();
            return true;
        }
  
    }     

    @Override
    public void onExceededDatabaseQuota(String url, String databaseIdentifier, long currentQuota, long estimatedSize, long totalUsedQuota, WebStorage.QuotaUpdater quotaUpdater)
    {
        quotaUpdater.updateQuota(estimatedSize * 2);
    }    
}
