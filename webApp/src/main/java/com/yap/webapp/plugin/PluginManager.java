package com.yap.webapp.plugin;


import android.util.Log;

import com.yap.webapp.was.webruntime.WasWebview;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PluginManager
{
    private static final String TAG = "Plugin";

    private static PluginManager instance = new PluginManager();

    private HashMap<String, Plugin> pluginMap;

    private PluginManager()
    {
        pluginMap = new HashMap<String, Plugin>();
    }

    public static PluginManager getInstance()
    {
        return instance;
    }

    public void loadPlugins()
    {
        Log.d(TAG, "loadPlugins,, load null (deprecated)" );
        for(Plugin p:pluginMap.values())
        {
            p.init();
        }
    }
    
    public void unloadPlugins()
    {
        for(Plugin p:pluginMap.values())
        {
            p.destroy();
        }
        pluginMap.clear();
    }

    // 注入插件
    public void injectionPlugin(Plugin plugin) {
        if(plugin != null && !pluginMap.containsKey(plugin.getName())) {
            pluginMap.put(plugin.getName(), plugin);
        }
    }

    // 注入插件列表
    public void injectionPlugins(List<Plugin> plugins) {
        if(plugins != null) {
            for(Plugin plugin : plugins) {
                injectionPlugin(plugin);
            }
        }
    }

    
    public Plugin getPlugin(String pluginName)
    {
        return pluginMap.get(pluginName);
    }
    

    public void exec(String pluginName, String method, Map<String, String> args, String callbackID, WasWebview webview, String abilityCode)
    {
        Plugin plugin = pluginMap.get(pluginName);
        Log.d(TAG, "exec(" + pluginName + "," + method+")");
        if (plugin == null)
        {
            // 没有找到插件，发送错误给js层
            Log.d(TAG, "can't find plugin:" + pluginName);
            this.sendError(callbackID, 998, "plugin not found.", webview);
            return;
        }
        try
        {
            String appID = null;// 需要找到合适的appID
            if (abilityCode == null || abilityCode.equals(""))
            {
                // 没有指定能力代码，appID就是调用发起的应用的ID
                appID = webview.getContext().getAppID();
            }
            else
            {
//                // 如果指定了能力代码，appID就是能力对应的appID
//                String abilityID = AppManager.getInstance().getAbilityIDByCode(abilityCode);
//                if (abilityID == null)
//                {
//                    Log.debug(TAG, "can't find ability:" + abilityCode);
//                    // 发送错误给js层
//                    this.sendError(callbackID, 997, "ability not found.", webview);
//                    return;
//                }
//                appID = abilityID;
            }
            plugin.doMethod(appID, method, args, callbackID, webview);
        }
        catch (Exception e)
        {
            Log.e(TAG, "", e);
            // 发送错误给js层
            this.sendError(callbackID, 999, "plugin internal error:"+e.getMessage(), webview);
            return;
        }
    }

    private void sendError(String callbackID, int retCode, String errMsg, WasWebview webview)
    {
        String script = "Was.callbackError('" + callbackID + "'," + retCode + ",'" + errMsg + "')";
        webview.execJS(script);
    }
}
