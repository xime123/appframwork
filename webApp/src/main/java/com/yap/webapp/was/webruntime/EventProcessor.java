package com.yap.webapp.was.webruntime;


import android.util.Log;

import java.util.HashMap;
import java.util.Map;


class EventProcessor
{
    static final String EVENT_ONCREATE = "onCreate";
    static final String EVENT_ONPAUSE = "onPause";
    static final String EVENT_ONRESUME = "onResume";
    static final String EVENT_ONEXIT = "onExit";
    
    private static final String TAG = "WebRuntime";

    private WasWebview webview;
    private Object eventIDLockObj = new Object();
    private int eventIDIDX=60000;
    
    private HashMap<Integer,Object> lockMap = new HashMap<Integer,Object>();
    
    public EventProcessor(WasWebview webview)
    {
        this.webview = webview;
    }
    
    public void fireEvent(String eventName,Map<String,String> eventData)
    {
        Log.d(TAG, "fireEvent("+eventName+")");
        boolean needBlock = false;
        if(needBlock && (EVENT_ONPAUSE.equals(eventName) || EVENT_ONEXIT.equals(eventName)))
        {
            //应用暂停和应用退出的事件，是需要阻塞的
            int eventID = genEventID();
            Object lockObj = new Object();
            synchronized(lockMap)
            {
                lockMap.put(eventID, lockObj);
            }
            Log.d(TAG, "event:"+eventID+" begin to wait...");
            synchronized(lockObj)
            {
                this.webview.sendEvent(eventName, eventID,eventData);
                try
                {
                    lockObj.wait(5000);
                }
                catch(Exception e)
                {}
            }
            Log.d(TAG, "event:"+eventID+" wait finished...");
            synchronized(lockMap)
            {
                lockMap.remove(eventID);
            }
        }
        else
        {
            this.webview.sendEvent(eventName, -1,eventData);
        }
    }
    
    public void notifyEventResult(int eventID)
    {
        Object lockObj = null;
        synchronized(lockMap)
        {
            lockObj = lockMap.get(eventID);
        }
        if(lockObj!=null)
        {
            Log.d(TAG, "event:"+eventID+" notify recieve...");
            synchronized(lockObj)
            {
                lockObj.notifyAll();
            }
        }
    }
    
    private int genEventID()
    {
        synchronized(eventIDLockObj)
        {
            if(eventIDIDX>=70000) eventIDIDX = 60000;
            return eventIDIDX++;
        }
    }

}
