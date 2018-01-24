package com.app.util;


import com.app.CoreApp;

/**
 * 统一获取资源字符串，避免各类频繁引用Application
 */
public class TDString {

    public static String getStr(int resId) {
        return CoreApp.getAppInstance().getResources().getString(resId);
    }

}
