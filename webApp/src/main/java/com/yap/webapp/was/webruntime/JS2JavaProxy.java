package com.yap.webapp.was.webruntime;

import android.util.Log;
import android.webkit.JavascriptInterface;

import com.yap.webapp.plugin.PluginManager;

import java.util.HashMap;

public class JS2JavaProxy
{
    private static final String TAG = "WebRuntime";

    private WasWebview mossWebview;
    private static final String SPLIT1 = ""+(char)1;
    private static final String SPLIT2 = ""+(char)2;

    public JS2JavaProxy(WasWebview mossWebview)
    {
        this.mossWebview = mossWebview;
    }

    @JavascriptInterface
    public void execPlugin(String pluginName,String method,String argsStr,String callbackID,String abilityCode)
    {
        Log.d(TAG, "execPlugin("+pluginName+","+method+","+argsStr+","+callbackID+","+abilityCode+")");
        //解析argsStr
        HashMap<String,String> args = new HashMap<String,String>();
        if(argsStr!=null && argsStr.length()>0)
        {
            argsStr = argsStr.substring(1);
            String[] params = argsStr.split(SPLIT1);
            String[] tmps = null;
            for(String param:params)
            {
                tmps = param.split(SPLIT2);
                if(tmps==null || tmps.length!=2)
                {
                    Log.d(TAG, "can't parse param:"+param);
                    continue;
                }
                Log.d(TAG, "parse a param:["+tmps[0]+"],["+tmps[1]+"]");
                args.put(tmps[0], tmps[1]);
            }
        }
        PluginManager.getInstance().exec(pluginName, method, args, callbackID, mossWebview,abilityCode);
    }

    @JavascriptInterface
    public void registerEventProcess()
    {
        Log.d(TAG, "registerEventProcess("+mossWebview.getContext().getAppID()+")");
        this.mossWebview.registerEventProcess();
    }

    @JavascriptInterface
    public void registerBackKey(){
    	 Log.d(TAG, "registerBackKey("+mossWebview.getContext().getAppID()+")");
         this.mossWebview.registerBackKey();
    }

    @JavascriptInterface
    public void notifyEventResult(int eventID)
    {
        Log.d(TAG, "notifyEventResult("+eventID+")");
        this.mossWebview.notifyEventResult(eventID);
    }
        
 
}
