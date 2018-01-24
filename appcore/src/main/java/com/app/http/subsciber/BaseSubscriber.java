
package com.app.http.subsciber;

import android.content.Context;

import com.app.http.exception.ApiException;
import com.app.http.utils.HttpLog;

import java.lang.ref.WeakReference;

import rx.Subscriber;

import static com.app.http.utils.Utils.isNetworkAvailable;


/**
 * <p>描述：订阅的基类</p>
 * 1.可以防止内存泄露。<br>
 * 2.在onStart()没有网络时直接onCompleted();<br>
 * 3.统一处理了异常<br>
 */
public abstract class BaseSubscriber<T> extends Subscriber<T> {
    public WeakReference<Context> contextWeakReference;

    public BaseSubscriber() {
    }

    public BaseSubscriber(Context context) {
        if (context != null)
            contextWeakReference = new WeakReference<Context>(context);
    }


    @Override
    public final void onError(Throwable e) {
        HttpLog.e("-->http is err");
        if (e instanceof ApiException) {
            HttpLog.e("--> e instanceof ApiException err:" + e.getMessage());
            onError((ApiException) e);
        } else {
            HttpLog.e("--> e !instanceof ApiException err:" + e.getMessage());
            onError(ApiException.handleException(e));
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        HttpLog.e("-->http is start");
        if (contextWeakReference != null && !isNetworkAvailable(contextWeakReference.get())) {
            //Toast.makeText(context, "无网络，读取缓存数据", Toast.LENGTH_SHORT).show();
            onCompleted();
        }
    }

    @Override
    public void onCompleted() {
        HttpLog.e("-->http is Complete");
    }


    public abstract void onError(ApiException e);

}
