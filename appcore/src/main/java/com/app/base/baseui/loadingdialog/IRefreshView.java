package com.app.base.baseui.loadingdialog;


import com.app.mvp.IView;

/**
 * Created by 徐敏 on 2017/5/25.
 */

public interface IRefreshView extends IView {
    void onStartLoading();
    void onFinishLoading();
    void onErrorLoading();
    void onDataEmpty();
}
