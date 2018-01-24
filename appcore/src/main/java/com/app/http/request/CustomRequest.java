

package com.app.http.request;


import com.app.http.cache.model.CacheResult;
import com.app.http.callback.CallBack;
import com.app.http.callback.CallBackProxy;
import com.app.http.func.ApiResultFunc;
import com.app.http.func.CacheResultFunc;
import com.app.http.func.HandleFuc;
import com.app.http.func.RetryExceptionFunc;
import com.app.http.model.ApiResult;
import com.app.http.subsciber.CallBackSubsciber;
import com.app.http.transformer.HandleErrTransformer;
import com.app.http.utils.RxUtil;
import com.app.http.utils.Utils;
import com.google.gson.reflect.TypeToken;

import okhttp3.ResponseBody;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;

/**
 * <p>描述：自定义请求，例如你有自己的ApiService</p>
 */
public class CustomRequest extends BaseRequest<CustomRequest> {
    public CustomRequest() {
        super("");
    }

    @Override
    public CustomRequest build() {
        return super.build();
    }

    /**
     * 创建api服务  可以支持自定义的api，默认使用BaseApiService,上层不用关心
     *
     * @param service 自定义的apiservice class
     */
    public <T> T create(final Class<T> service) {
        checkvalidate();
        return retrofit.create(service);
    }

    private void checkvalidate() {
        Utils.checkNotNull(retrofit, "请先在调用build()才能使用");
    }

    /**
     * 调用call返回一个Observable<T>
     * 举例：如果你给的是一个Observable<ApiResult<AuthModel>> 那么返回的<T>是一个ApiResult<AuthModel>
     */
    public <T> Observable<T> call(Observable<T> observable) {
        checkvalidate();
        return observable.compose(RxUtil.io_main())
                .compose(new HandleErrTransformer())
                .retryWhen(new RetryExceptionFunc(retryCount, retryDelay, retryIncreaseDelay));
    }

    public <T> Subscriber call(Observable<T> observable, CallBack<T> callBack) {
        return call(observable, new CallBackSubsciber(context, callBack));
    }

    public <R> Subscriber call(Observable observable, Subscriber<R> subscriber) {
        observable.compose(RxUtil.io_main())
                .subscribe(subscriber);
        return subscriber;
    }


    /**
     * 调用call返回一个Observable,针对het的业务<T>
     * 举例：如果你给的是一个Observable<ApiResult<AuthModel>> 那么返回的<T>是AuthModel
     */
    public <T> Observable<T> apiCall(Observable<ApiResult<T>> observable) {
        checkvalidate();
        return observable
                .map(new HandleFuc<T>())
                .compose(RxUtil.<T>io_main())
                .compose(new HandleErrTransformer<T>())
                .retryWhen(new RetryExceptionFunc(retryCount, retryDelay, retryIncreaseDelay));
    }

    public <T> Subscription apiCall(Observable<T> observable, CallBack<T> callBack) {
        return call(observable, new CallBackProxy<ApiResult<T>, T>(callBack) {
        });
    }

    public <T> Subscription call(Observable<T> observable, CallBackProxy<? extends ApiResult<T>, T> proxy) {
        Observable<CacheResult<T>> cacheobservable = build().toObservable(observable, proxy);
        if (CacheResult.class != proxy.getCallBack().getRawType()) {
            return cacheobservable.compose(new Observable.Transformer<CacheResult<T>, T>() {
                @Override
                public Observable<T> call(Observable<CacheResult<T>> observable) {
                    return observable.map(new CacheResultFunc<T>());
                }
            }).subscribe(new CallBackSubsciber(context, proxy.getCallBack()));
        }
        return cacheobservable.subscribe(new CallBackSubsciber<CacheResult<T>>(context, proxy.getCallBack()));
    }

    private <T> Observable<CacheResult<T>> toObservable(Observable observable, CallBackProxy<? extends ApiResult<T>, T> proxy) {
        return observable.map(new ApiResultFunc(proxy != null ? proxy.getType() : new TypeToken<ResponseBody>() {
        }.getType()))
                .compose(isSyncRequest ? RxUtil._main() : RxUtil._io_main())
                .compose(rxCache.transformer(cacheMode, proxy.getCallBack().getType()))
                .retryWhen(new RetryExceptionFunc(retryCount, retryDelay, retryIncreaseDelay));
    }

}
