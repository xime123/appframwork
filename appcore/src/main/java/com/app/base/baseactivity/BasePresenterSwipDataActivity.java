package com.app.base.baseactivity;


import com.app.mvp.present.BasePresenter;

/**
 * Created by 徐敏 on 2017/7/19.
 * MVP模式下的刷新页面
 */

public abstract class BasePresenterSwipDataActivity<PRESENTER extends BasePresenter> extends DataSwipRefreshActivity{
    protected PRESENTER mPresenter = initPresenter();

    protected abstract PRESENTER initPresenter();

}
