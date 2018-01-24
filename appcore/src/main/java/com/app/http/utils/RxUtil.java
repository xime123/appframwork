
package com.app.http.utils;


import com.app.http.func.HandleFuc;
import com.app.http.func.HttpResponseFunc;
import com.app.http.model.ApiResult;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * <p>描述：线程调度工具</p>
 */
public class RxUtil {

    public static <T> Observable.Transformer<T, T> io_main() {
        return (Observable.Transformer) new Observable.Transformer() {
            @Override
            public Object call(Object observable) {
                return ((Observable) observable)
                        .subscribeOn(Schedulers.io())
                        .unsubscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());
            }
        };
    }

    public static <T> Observable.Transformer<ApiResult<T>, T> _io_main() {
        return (Observable.Transformer) new Observable.Transformer() {
            @Override
            public Object call(Object observable) {
                return ((Observable) observable)
                        .subscribeOn(Schedulers.io())
                        .unsubscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .map(new HandleFuc<T>())
                        .onErrorResumeNext(new HttpResponseFunc<T>());
            }
        };
    }


    public static <T> Observable.Transformer<ApiResult<T>, T> _main() {
        return (Observable.Transformer) new Observable.Transformer() {
            @Override
            public Object call(Object observable) {
                return ((Observable) observable)
                        .observeOn(AndroidSchedulers.mainThread())
                        .map(new HandleFuc<T>())
                        .onErrorResumeNext(new HttpResponseFunc<T>());
            }
        };
    }
}
