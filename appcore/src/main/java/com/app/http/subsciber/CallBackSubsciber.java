
package com.app.http.subsciber;

import android.content.Context;

import com.app.http.callback.CallBack;
import com.app.http.callback.ProgressDialogCallBack;
import com.app.http.exception.ApiException;


/**
 * <p>描述：带有callBack的回调</p>
 * 主要作用是不需要用户订阅，只要实现callback回调<br>
 */
public class CallBackSubsciber<T> extends BaseSubscriber<T> {
    private CallBack<T> callBack;

    public CallBackSubsciber(Context context, CallBack<T> callBack) {
        super(context);
        this.callBack = callBack;
        if (callBack instanceof ProgressDialogCallBack) {
            ((ProgressDialogCallBack) callBack).subscription(this);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (callBack != null) {
            callBack.onStart();
        }
    }

    @Override
    public void onError(ApiException e) {
        if (callBack != null) {
            callBack.onError(e);
        }
    }

    @Override
    public void onNext(T t) {
        //Utils.checkNotNull(t, "CallBackSubsciber onNext t==null");
        if (callBack != null) {
            callBack.onSuccess(t);
        }
    }

    @Override
    public void onCompleted() {
        super.onCompleted();
        if (callBack != null) {
            callBack.onCompleted();
        }
    }
}
