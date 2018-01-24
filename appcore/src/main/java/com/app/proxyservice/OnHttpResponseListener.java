package com.app.proxyservice;


import com.app.http.exception.ApiException;

public interface OnHttpResponseListener<T> {
    void onSuccess(T data);
    void onFail(ApiException e);
}
