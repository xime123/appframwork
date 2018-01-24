package com.yap.webapp.plugin;


import com.yap.webapp.was.webruntime.StringUtil;

public class PluginResult
{
    private StringBuffer sb = new StringBuffer();
    private boolean isEmpty = true;
    
    
    public PluginResult()
    {
        
    }
    
    public void addParam(String name,String value)
    {
        this.addParam(name, value, false);
    }

    public void addParam(String name,String value,boolean needFitJS)
    {
        if(isEmpty)
        {
            isEmpty = false;
            sb.append('{');
        }
        else
        {
            sb.append(',');
        }
        sb.append(name);
        sb.append(':');
        sb.append('\'');
        if(needFitJS)
        {
            sb.append(StringUtil.fitJS(value==null?"":value));
        }
        else
        {
            sb.append(value==null?"":value);
        }
        sb.append('\'');
    }
    public void addObject(String name,String value,boolean needFitJS)
    {
        if(isEmpty)
        {
            isEmpty = false;
            sb.append('{');
        }
        else
        {
            sb.append(',');
        }
        sb.append(name);
        sb.append(':');
        sb.append('\'');
        if(needFitJS)
        {
            sb.append(StringUtil.fitJS(value==null?"":value));
        }
        else
        {
            sb.append(value==null?"":value);
        }
        sb.append('\'');
    }
    public String toString()
    {
        return sb.toString()+'}';
    }

}
