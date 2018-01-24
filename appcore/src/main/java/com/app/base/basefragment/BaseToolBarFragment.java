package com.app.base.basefragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.app.R;
import com.app.base.baseui.CommonActionBar;
import com.app.base.baseui.loadingdialog.LoadingDialog;


/**
 * Created by 徐敏 on 2017/8/14.
 */

public abstract class BaseToolBarFragment extends BaseFragment {
    protected View baseRootView;
    protected ViewGroup containerView;
    protected ViewGroup contentView;
    protected CommonActionBar toolbar;


    protected abstract int getLayoutRes();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        baseRootView = View.inflate(getActivity(), R.layout.activity_base, null);
        containerView = (ViewGroup) baseRootView.findViewById(R.id.container);
        contentView = (ViewGroup) View.inflate(getActivity(), getLayoutRes(), null);
        containerView.addView(contentView);
        initView(contentView);
        initTooBar();
        return baseRootView;
    }


    protected void initView(ViewGroup contentView) {
    }


    protected void initTooBar() {
        toolbar = (CommonActionBar)baseRootView.findViewById(R.id.action_bar);
        toolbar.setBackResource(R.color.cyan_click);
        setTitle("矩阵元");
        toolbar.setBackIconVisible(false);
    }


    public void setTitle(String text) {
        toolbar.setTitle(text);
    }

    public void showLoading() {
        LoadingDialog.loadingDialog(getActivity());
    }

    public void dismissLoading() {
        LoadingDialog.cancleDialog();
    }


}
