

package com.app.http.model;

/**
 * <p>描述：提供的默认的标注返回api</p>
 */
public class ApiResult<T> {
    private int ret;
    private String message;
    private T data;
    public int getCode() {
        return ret;
    }

    public void setCode(int code) {
        this.ret = code;
    }

    public String getMsg() {
        return message;
    }

    public void setMsg(String msg) {
        this.message = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public boolean isOk() {
        return ret == 0 ? true : false;
    }

    @Override
    public String toString() {
        return "ApiResult{" +
                "code='" + ret + '\'' +
                ", msg='" + message + '\'' +
                ", data=" + data +
                '}';
    }
}
