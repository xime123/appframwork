package com.app.mvp.contract;


import com.app.base.baseui.loadingdialog.IRefreshView;
import com.app.mvp.present.IPresenter;

import java.util.List;
import java.util.Map;




/**
 * 描述说明  <br/>
 * 封裝了列表hui回掉
 */
public interface ItemsContract {

    interface View<T> extends IRefreshView {
        void updateListItems(List<T> items);

        void appendListItems(List<T> items);

        void loadMoreComplete();

    }

    interface Presenter extends IPresenter {
        void fetchListItems(Map<String, String> params);

        void fetchMoreListItems(Map<String, String> params);
    }

}
