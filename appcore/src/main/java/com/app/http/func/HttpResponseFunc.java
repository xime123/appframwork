

package com.app.http.func;


import com.app.http.exception.ApiException;

import rx.Observable;
import rx.functions.Func1;

/**
 * <p>描述：异常转换处理</p>
 */
public class HttpResponseFunc<T> implements Func1<Throwable, Observable<T>> {
    @Override
    public Observable<T> call(Throwable t) {
        return Observable.error(ApiException.handleException(t));
    }
}
