

package com.app.http.func;


import com.app.http.cache.model.CacheResult;

import rx.functions.Func1;

/**
 * <p>描述：缓存结果转换</p>
 */
public class CacheResultFunc<T> implements Func1<CacheResult<T>, T> {
    @Override
    public T call(CacheResult<T> tCacheResult) {
        return (T) tCacheResult.data;
    }
}
