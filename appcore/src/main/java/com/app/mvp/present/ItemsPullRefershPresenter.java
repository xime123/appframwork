package com.app.mvp.present;


import com.app.mvp.contract.PullToRefrshItemsContract;

import java.util.Map;




/**
 * Created by 徐敏 on 2017/5/26.
 */

public abstract class ItemsPullRefershPresenter<T,V extends PullToRefrshItemsContract.View> extends BasePresenter implements PullToRefrshItemsContract.Presenter {
    public ItemsPullRefershPresenter(V view) {
        super(view);
        this.view=view;
    }
    protected  V view;
    @Override
    public V getView() {
        return view;
    }

    protected abstract void getListItemsObservable(Map<String, String> params);

    protected abstract void getMoreListItemsObservable(Map<String, String> params);

    @Override
    public void fetchListItems(Map<String, String> params) {
        getListItemsObservable(params);
    }

    @Override
    public void fetchMoreListItems(Map<String, String> params) {
         getMoreListItemsObservable(params);
    }
}
