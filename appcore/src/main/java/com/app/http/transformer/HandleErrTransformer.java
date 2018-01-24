

package com.app.http.transformer;


import com.app.http.func.HttpResponseFunc;

import rx.Observable;

/**
 * <p>描述：错误转换Transformer</p>
 */
public class HandleErrTransformer<T> implements Observable.Transformer<T, T> {
    @Override
    public Observable<T> call(Observable<T> tObservable) {
        //return ((Observable) observable).map(new HandleFuc<T>()).onErrorResumeNext(new HttpResponseFunc<T>());
        return ((Observable) tObservable).onErrorResumeNext(new HttpResponseFunc<T>());
    }
}
