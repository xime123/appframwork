package com.app.base.baseactivity;

import android.support.annotation.NonNull;

import com.app.mvp.contract.ItemsContract;

import java.util.Map;




/**
 * Created by 徐敏 on 2017/5/26.
 * MVP模式下的列表页面
 */

public abstract class BasePresenterListActivity<T, PRESENTER extends ItemsContract.Presenter> extends BaseSwipRefreshListActivity<T> {

    private PRESENTER mPresenter = initPresenter();

    protected abstract PRESENTER initPresenter();

    @Override
    protected void fetchListItems(@NonNull Map<String, String> params) {
        mPresenter.fetchListItems(params);
    }

    @Override
    protected void fetchMoreListItems(@NonNull Map<String, String> params) {
        mPresenter.fetchMoreListItems(params);
    }
}
