
package com.app.http.cache.stategy;


import com.app.http.cache.RxCache;
import com.app.http.cache.model.CacheResult;

import java.lang.reflect.Type;

import rx.Observable;
import rx.functions.Func1;


/**
 * <p>描述：先显示缓存，缓存不存在，再请求网络</p>
 *<-------此类加载用的是反射 所以类名是灰色的 没有直接引用  不要误删----------------><br>
 */
final public class FirstCacheStategy extends BaseStrategy{
    @Override
    public <T> Observable<CacheResult<T>> execute(RxCache rxCache, String key, long time, Observable<T> source, Type type) {
        Observable<CacheResult<T>> cache = loadCache(rxCache,type,key,time);
        cache.onErrorReturn(new Func1<Throwable, CacheResult<T>>() {
            @Override
            public CacheResult<T> call(Throwable throwable) {
                return null;
            }
        });
        Observable<CacheResult<T>> remote = loadRemote(rxCache,key, source);
        return Observable.concat(cache, remote)
                .firstOrDefault(null, new Func1<CacheResult<T>, Boolean>() {
                    @Override
                    public Boolean call(CacheResult<T> tResultData) {
                        return tResultData != null && tResultData.data != null;
                    }
                });
    }
}
