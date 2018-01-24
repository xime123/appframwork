package com.app.mvp.contract;


import android.app.Activity;

import com.app.mvp.IView;
import com.app.mvp.present.IPresenter;

import java.util.List;
import java.util.Map;




/**
 * 描述说明  <br/>
 * 封裝了列表回调
 */
public interface PullToRefrshItemsContract {

    interface View<T> extends IView {
        void loadSuccess(List<T> items);

        void loadFailed(String msg);

        Activity getActivity();

    }

    interface Presenter extends IPresenter {
        void fetchListItems(Map<String, String> params);

        void fetchMoreListItems(Map<String, String> params);
    }

}
