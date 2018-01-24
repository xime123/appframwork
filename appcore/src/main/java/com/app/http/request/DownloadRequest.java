
package com.app.http.request;


import com.app.http.callback.CallBack;
import com.app.http.func.RetryExceptionFunc;
import com.app.http.subsciber.DownloadSubscriber;
import com.app.http.transformer.HandleErrTransformer;

import okhttp3.ResponseBody;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * <p>描述：下载请求</p>
 */
public class DownloadRequest extends BaseRequest<DownloadRequest> {
    public DownloadRequest(String url) {
        super(url);
    }

    private String savePath;
    private String saveName;

    /**
     * 下载文件路径<br>
     * 默认在：/storage/emulated/0/Android/data/包名/files/1494647767055<br>
     */
    public DownloadRequest savePath(String savePath) {
        this.savePath = savePath;
        return this;
    }

    /**
     * 下载文件名称<br>
     * 默认名字是时间戳生成的<br>
     */
    public DownloadRequest saveName(String saveName) {
        this.saveName = saveName;
        return this;
    }

    public <T> Subscription execute(CallBack<T> callBack) {
        return build().apiManager.downloadFile(url).compose(new Observable.Transformer<ResponseBody, Object>() {
            @Override
            public Observable<Object> call(Observable<ResponseBody> responseBodyObservable) {
                if (isSyncRequest)
                    return ((Observable) responseBodyObservable)
                            .observeOn(AndroidSchedulers.mainThread());
                else
                    return ((Observable) responseBodyObservable).subscribeOn(Schedulers.io())
                            .unsubscribeOn(Schedulers.io())
                            .observeOn(Schedulers.io());
            }
        }).compose(new HandleErrTransformer()).retryWhen(new RetryExceptionFunc(retryCount, retryDelay, retryIncreaseDelay))
                .subscribe(new DownloadSubscriber(savePath, saveName, callBack, context));
    }
}
