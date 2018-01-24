package com.app.http.model;

/**
 * Created by 徐敏 on 2017/7/20.
 */

public class WalletApiResult<T> extends ApiResult<T> {
    String reason;
    int error_code;
    int count;

    @Override
    public int getCode() {
        return error_code;
    }
    @Override
    public void setCode(int code) {
        error_code = code;
    }
    @Override
    public String getMsg() {
        return reason;
    }
    @Override
    public void setMsg(String msg) {
        reason = msg;
    }


    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    @Override
    public boolean isOk() {
        return true;//如果不是0表示成功，请重写isOk()方法。
    }
}
