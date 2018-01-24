package com.yap.webapp.plugin;


import com.yap.webapp.was.webruntime.StringUtil;
import com.yap.webapp.was.webruntime.WasWebview;

import java.util.Map;

public abstract class Plugin
{
    protected static final String TAG = "Plugin";
    protected String name;
    
    public abstract void doMethod(String appID,String method,Map<String,String> args,String callbackID
                                  ,WasWebview webview);
    
    public String getName()
    {
        return this.name;
    }
    
    public void sendResult(String callbackID,PluginResult r,WasWebview webview)
    {
        this.sendResult(callbackID, r, webview, false);
    }

    public void sendResult(String callbackID,PluginResult r,WasWebview webview,boolean keepCallback)
    {
        String script = null;
        String resultStr = r==null?"{}":r.toString();
        if(keepCallback){
            script = "Was.callbackResult('"+callbackID+"',"+resultStr+",1)";
        }
        else{
            script = "Was.callbackResult('"+callbackID+"',"+resultStr+")";
        }
        webview.execJS(script);
    }
    
    public void sendError(String callbackID,int retCode,String errMsg,WasWebview webview)
    {
        String script = "Was.callbackError('"+callbackID+"',"+retCode+",'"+errMsg+"')";
        webview.execJS(script);
    }

    public void sendError(String callbackID,int retCode,Throwable t,WasWebview webview)
    {
        String errMsg = t.getMessage();
        errMsg = errMsg==null?"": StringUtil.fitJS(errMsg);
        String script = "Was.callbackError('"+callbackID+"',"+retCode+",'"+errMsg+"')";
        webview.execJS(script);
    }
    
    public void sendPluginOperBean(PluginOperBean b,String callbackID,WasWebview webview)
    {
        if(b.retCode==0)
        {
            this.sendResult(callbackID, b.r, webview);
        }
        else
        {
            this.sendError(callbackID, b.retCode, b.errMsg, webview);
        }
    }
    
    public void init()
    {
        
    }

    public void destroy()
    {
        
    }
    
    public static class PluginOperBean
    {
        public PluginResult r;
        public int retCode;
        public String errMsg;
    }
}
