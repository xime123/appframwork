package com.app.base.baseactivity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import com.app.R;
import com.app.base.baseui.CommonActionBar;
import com.app.base.baseui.StatusBarUtil;


/**
 * Created by 徐敏 on 2017/5/25.
 * ToolBar页面
 */

public abstract class BaseToolBarActivity extends BaseActivity {

    protected View baseRootView;
    protected ViewGroup containerView;
    protected ViewGroup contentView;
    protected CommonActionBar toolbar;


    protected abstract int getLayoutRes();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        baseRootView = View.inflate(this, R.layout.activity_base, null);
        containerView = (ViewGroup) baseRootView.findViewById(R.id.container);
        contentView = (ViewGroup) View.inflate(this, getLayoutRes(), null);

        containerView.addView(contentView);
        setContentView(baseRootView);
        initView(contentView);
        setListener();
        initData();
//        setStatusBarColor(R.color.color_5d9deb);
    }

    protected void initView(ViewGroup contentView) {
        initTooBar();
    }

    protected void setListener(){

    }

    protected  void initData(){

    }

    protected void initTooBar() {
        toolbar = (CommonActionBar) findViewById(R.id.action_bar);
        toolbar.setBackgroundResource(R.color.main_color_normal);
        setTitle("矩阵元");
        toolbar.setOnBackClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    protected void setStatusBarColor(int colorRes) {
        // 设置透明状态栏
        StatusBarUtil.setColor(this, getResources().getColor(colorRes), 0);
    }

    public void setTitle(String text) {
        toolbar.setTitle(text);
    }



    public Activity getActivity(){
        return this;
    }
}
