package com.app.rx;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by 徐敏 on 2018/1/5.
 * RxJava异步调用封装类
 */

public class RxLogicHandler {
    public static <T> void doWork(final Excutor excutor,BaseCallBack<T> callBack){
        Observable.create(new ObservableOnSubscribe<T>() {
            @Override
            public void subscribe(ObservableEmitter<T> e) throws Exception {
                T t=(T)excutor.excute();
                e.onNext(t);
                e.onComplete();
            }
        }).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new BaseOberver<T>(callBack));
    }

    public interface Excutor<Result>{
        Result excute()throws Exception;
    }
}
