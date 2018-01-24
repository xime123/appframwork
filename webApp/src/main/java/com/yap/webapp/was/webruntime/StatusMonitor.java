package com.yap.webapp.was.webruntime;

import java.util.Map;

/**
 * Created by Administrator on 2016/7/15.
 */
public interface StatusMonitor {
    String getUserID();

    String getUserToken();

    Map<String, String> getStatusParam();

    // 网络状态。1在线；0离线；
    int getNetType();
}
