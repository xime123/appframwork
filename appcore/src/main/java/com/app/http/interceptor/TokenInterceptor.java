package com.app.http.interceptor;

import okhttp3.Response;

/**
 * Created by 徐敏 on 2017/8/8.
 */

public class TokenInterceptor extends BaseExpiredInterceptor {

    @Override
    public boolean isResponseExpired(Response response, String bodyString) {
        if (response.code() == 404) {
            return true;
        }
        return false;
    }

    @Override
    public Response responseExpired(Chain chain, String bodyString) {
        // 通过一个特定的接口获取新的token，此处要用到同步的retrofit请求

        return null;
    }
}
