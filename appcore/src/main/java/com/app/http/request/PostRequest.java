
package com.app.http.request;


import com.app.http.cache.model.CacheResult;
import com.app.http.callback.CallBack;
import com.app.http.callback.CallBackProxy;
import com.app.http.callback.CallClazzProxy;
import com.app.http.func.ApiResultFunc;
import com.app.http.func.CacheResultFunc;
import com.app.http.func.RetryExceptionFunc;
import com.app.http.model.ApiResult;
import com.app.http.subsciber.CallBackSubsciber;
import com.app.http.utils.RxUtil;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

import okhttp3.ResponseBody;
import rx.Observable;
import rx.Subscription;

/**
 * <p>描述：post请求</p>
 */
public class PostRequest extends BaseBodyRequest<PostRequest> {
    public PostRequest(String url) {
        super(url);
    }


    public <T> Observable<T> execute(Class<T> clazz) {
        return execute(new CallClazzProxy<ApiResult<T>, T>(clazz) {
        });
    }

    public <T> Observable<T> execute(Type type) {
        return execute(new CallClazzProxy<ApiResult<T>, T>(type) {
        });
    }
    
    public <T> Observable<T> execute(CallClazzProxy<? extends ApiResult<T>, T> proxy) {
        return build().generateBody()
                .map(new ApiResultFunc(proxy.getType()))
                .compose(isSyncRequest ? RxUtil._main() : RxUtil._io_main())
                .compose(rxCache.transformer(cacheMode, proxy.getCallType()))
                .retryWhen(new RetryExceptionFunc(retryCount, retryDelay, retryIncreaseDelay))
                .compose(new Observable.Transformer<CacheResult<T>, T>() {
                    @Override
                    public Observable<T> call(Observable<CacheResult<T>> cacheResultObservable) {
                        return cacheResultObservable.map(new CacheResultFunc<T>());
                    }
                });
    }

    public <T> Subscription execute(CallBack<T> callBack) {
        return execute(new CallBackProxy<ApiResult<T>, T>(callBack) {
        });
    }

    public <T> Subscription execute(CallBackProxy<? extends ApiResult<T>, T> proxy) {
        Observable<CacheResult<T>> observable = build().toObservable(generateBody(), proxy);
        if (CacheResult.class != proxy.getCallBack().getRawType()) {
            return observable.compose(new Observable.Transformer<CacheResult<T>, T>() {
                @Override
                public Observable<T> call(Observable<CacheResult<T>> observable) {
                    return observable.map(new CacheResultFunc<T>());
                }
            }).subscribe(new CallBackSubsciber(context, proxy.getCallBack()));
        }
        return observable.subscribe(new CallBackSubsciber<CacheResult<T>>(context, proxy.getCallBack()));
    }

    private <T> Observable<CacheResult<T>> toObservable(Observable observable, CallBackProxy<? extends ApiResult<T>, T> proxy) {
        return observable.map(new ApiResultFunc(proxy != null ? proxy.getType() : new TypeToken<ResponseBody>() {}.getType()))
                .compose(isSyncRequest ? RxUtil._main() : RxUtil._io_main())
                .compose(rxCache.transformer(cacheMode, proxy.getCallBack().getType()))
                .retryWhen(new RetryExceptionFunc(retryCount, retryDelay, retryIncreaseDelay));
    }
}
