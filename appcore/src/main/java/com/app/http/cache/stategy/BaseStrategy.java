
package com.app.http.cache.stategy;


import com.app.http.cache.RxCache;
import com.app.http.cache.model.CacheResult;
import com.app.http.utils.HttpLog;

import java.lang.reflect.Type;

import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * <p>描述：实现缓存策略的基类</p>
 */
public abstract class BaseStrategy implements IStrategy {
    <T> Observable<CacheResult<T>> loadCache(final RxCache rxCache, Type type, final String key, final long time) {
        return rxCache
                .<T>load(type,key,time)
                .map(new Func1<T, CacheResult<T>>() {
                    @Override
                    public CacheResult<T> call(T o) {
                        HttpLog.i("loadCache result=" + o);
                        return new CacheResult<T>(true, (T) o);
                    }
                });
    }

     <T> Observable<CacheResult<T>> loadRemote(final RxCache rxCache, final String key, Observable<T> source) {
        return source
                .map(new Func1<T, CacheResult<T>>() {
                    @Override
                    public CacheResult<T> call(T t) {
                        HttpLog.i("loadRemote result=" + t);
                        rxCache.save(key, t).subscribeOn(Schedulers.io())
                                .subscribe(new Action1<Boolean>() {
                                    @Override
                                    public void call(Boolean status) {
                                        HttpLog.i("save status => " + status);
                                    }
                                });
                        return new CacheResult<T>(false, (T) t);
                    }
                });
    }
}
