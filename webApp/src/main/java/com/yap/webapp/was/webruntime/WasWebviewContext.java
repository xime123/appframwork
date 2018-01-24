package com.yap.webapp.was.webruntime;

import java.util.HashMap;
import java.util.Map;

public class WasWebviewContext
{
    private String appID;
    private String userID;
    private String userToken;
    private HashMap<String, Object> map;

    private Map<String, String> mPreParams;
    
    //应用的横竖屏状态 1为竖屏 2为横屏     默认状态从配置文件读取
    private int screenOrientation;

    public WasWebviewContext(String appID, Map<String, String> preParams)
    {
        //从配置文件中读取
        //screenOrientation= Config.getInstance().getMoudle("Engine").getIntItem("WasViewDefaultOrient", 1);
        this.appID = appID;
        this.map = new HashMap<String,Object>();
        this.userID = userID;
        this.userToken = userToken;
        if(preParams == null) {
            preParams = new HashMap<>();
        }
        mPreParams = preParams;
    }

    public Map<String, String> getPreParams(){
        return mPreParams;
    }
      
    public String getAppID()
    {
        return appID;
    }
    public String getUserID()
    {
        return userID;
    }
    public String getUserToken()
    {
        return userToken;
    }

    public void setParam(String name,Object value)
    {
        this.map.put(name, value);
    }
    
    public Object getParam(String name)
    {
        return this.map.get(name);
    }
    
    public Object removeParam(String name)
    {
        return this.map.remove(name);
    }
    public int getScreenOrientation()
    {
        return screenOrientation;
    }
    public void setScreenOrientation(int screenOrientation)
    {
         this.screenOrientation = screenOrientation;
    }

}
