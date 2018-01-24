package com.app.mvp.present;


import com.app.mvp.IView;

/**
 * 描述说明  <br/>
 */
public class BasePresenter<V extends IView> implements IPresenter {
    private V view;

    public BasePresenter(V view) {
        this.view = view;
    }

    public V getView() {
        return view;
    }
}
