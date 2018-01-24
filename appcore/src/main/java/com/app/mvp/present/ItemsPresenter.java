package com.app.mvp.present;


import com.app.mvp.contract.ItemsContract;

import java.util.Map;




/**
 * Created by 徐敏 on 2017/5/26.
 */

public abstract class ItemsPresenter<T,V extends ItemsContract.View> extends BasePresenter implements ItemsContract.Presenter {
    public ItemsPresenter(V view) {
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
