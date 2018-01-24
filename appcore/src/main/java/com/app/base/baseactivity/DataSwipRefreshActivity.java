package com.app.base.baseactivity;

import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.view.ViewGroup;

import com.app.R;
import com.app.base.baseui.loadingdialog.IRefreshView;
import com.app.util.TDString;




/**
 * Created by 徐敏 on 2017/5/26.
 * 普通模式下的数据刷新页面
 */

public abstract class DataSwipRefreshActivity extends StatusBarColorActivity implements SwipeRefreshLayout.OnRefreshListener, IRefreshView {
    protected SwipeRefreshLayout swipeRefreshLayout;
    protected View errorView;
    protected View emptyView;

    @Override
    protected void initView(ViewGroup contentView) {
        super.initView(contentView);

        swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.refresh_srl);
        if (swipeRefreshLayout == null) {
            swipeRefreshLayout = new SwipeRefreshLayout(this);
            swipeRefreshLayout.setId(R.id.refresh_srl);
            ViewGroup parentViewGroup = (ViewGroup) contentView.getParent();
            parentViewGroup.removeView(contentView);
            swipeRefreshLayout.addView(contentView);
            parentViewGroup.addView(swipeRefreshLayout, contentView.getLayoutParams());
        }

        swipeRefreshLayout.setOnRefreshListener(this);

    }


    @Override
    public void onStartLoading() {
        setRefreshing(true);
    }


    @Override
    public void onFinishLoading() {
        dismissLoading();
        containerView.removeView(errorView);
        setRefreshing(false);
    }

    @Override
    public void onErrorLoading() {
        onFinishLoading();
        errorView = View.inflate(this, R.layout.error_layout, null);
        containerView.addView(errorView);
        errorView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refresh();
            }
        });
        setRefreshing(false);
    }

    @Override
    public void onDataEmpty() {
        emptyView = View.inflate(this, R.layout.empty_layout, null);
        containerView.addView(emptyView);
        emptyView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onRefresh();
            }
        });
        setRefreshing(false);
    }
    private boolean first=true;
    @Override
    public void onRefresh() {
        if(first) {
            first=false;
            showLoading(TDString.getStr(R.string.loading),"",false);
        }
        refresh();
    }

    public abstract void refresh();

    protected void setRefreshing(boolean refreshing) {
        swipeRefreshLayout.setRefreshing(refreshing);
    }
}
