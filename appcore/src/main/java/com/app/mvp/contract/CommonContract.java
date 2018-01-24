package com.app.mvp.contract;

import com.app.mvp.AView;
import com.app.mvp.present.IPresenter;

import java.util.Map;

import rx.Subscription;

/**
 * Created by 徐敏 on 2017/7/19.
 *
 */

public interface CommonContract {
    interface View <T> extends AView {
        void onGetDataSuccess(T data);

        void ongetDataFailed(String msg);
    }

    interface Presenter extends IPresenter {
        Subscription getData(Map<String, String> params);
    }
}
