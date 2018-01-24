package com.yap.webapp.was.webruntime;


import android.util.Log;

public class StringUtil
{
    private static final String TAG = "StringUtil";
    //让字符串符合javascript的要求。
    public static String fitJS(String source)
    {
        //把单引号/回车/换行都转义了
        String r = source.replace("\\", "\\\\");
        r = r.replace("\"", "\\\"");
        r = r.replace("'", "\\'");
        r = r.replace("\r", "\\r");
        r = r.replace("\n", "\\n");
        return r;
    }

    public static String fitJSComment(String line){
        String ret = line;
        if(ret != null && ret.trim().startsWith("//")) {
            //ret = "/*" + line + "*/";
            Log.d(TAG, "remove line : " + line);
            ret = "";
        }
        return ret;
    }


    public static boolean isEmpty(Object str) {
        return str == null || str.toString().length() == 0;
    }

    public static boolean isNotEmpty(Object str) {
        return !isEmpty(str);
    }
}
