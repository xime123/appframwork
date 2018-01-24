package com.app.base.basefragment;

import android.support.annotation.NonNull;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.app.R;
import com.app.base.baseAdapter.CommonAdapter;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.SimpleClickListener;
import com.example.xrecyclerview.XRecyclerView;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



/**
 * Created by 徐敏 on 2017/8/14.
 */

public abstract class BaseRefreshListFragment<T> extends BaseToolBarFragment implements XRecyclerView.LoadingListener{
    protected XRecyclerView rvList;
    protected View errorView;
    protected View emptyView;
    private boolean mIsFirst = true;
    protected CommonAdapter mAdapter;
    protected int pagenum = 0;
    protected int pagesize = 10;
    protected List<T> datas = new ArrayList<>();
    private String mType = "综合";

    protected abstract void fetchListItems(@NonNull Map<String, String> params);

    protected abstract void fetchMoreListItems(@NonNull Map<String, String> params);

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_pull_base_refresh_recycleview_layout;
    }

    @NonNull
    protected Map<String, String> getFetchListItemsParams() {
        Map<String, String> params = new HashMap<>();
        params.put("tag", mType);
        params.put("start", pagenum+"");
        params.put("count", pagesize+"");
        return params;
    }

    @NonNull
    protected Map<String, String> getFetchMoreListItemsParams() {
        Map<String, String> params = new HashMap<>();
        params.put("tag", mType);
        params.put("start", pagenum+"");
        params.put("count", pagesize+"");
        return params;
    }

    /**
     * RecyclerView 的Adapter , 用于展示Item
     */
    protected abstract CommonAdapter<T> createAdapter();

    @Override
    protected void initView(ViewGroup contentView) {
        super.initView(contentView);
        rvList = (XRecyclerView)baseRootView.findViewById(R.id.xrv_base);
        mAdapter = createAdapter();
        rvList.setAdapter(mAdapter);
        rvList.setLayoutManager(buildLayoutManager());
        rvList.setItemAnimator(new DefaultItemAnimator());
        for (RecyclerView.ItemDecoration itemDecoration : buildItemDecorations()) {
            rvList.addItemDecoration(itemDecoration);
        }
        showLoading();
        fetchListItems(getFetchListItemsParams());
        rvList.setItemAnimator(null);
        rvList.setLoadingListener(this);
    }

    public CommonAdapter<T> getAdapter() {
        return mAdapter;
    }


    protected List<? extends RecyclerView.ItemDecoration> buildItemDecorations() {
        return Collections.singletonList(new HorizontalDividerItemDecoration.Builder(getActivity())
            .colorResId(R.color.colorPrimary)
            .sizeResId(R.dimen.divide)
            .build());
    }


    protected RecyclerView.LayoutManager buildLayoutManager() {
        return new LinearLayoutManager(getActivity());
    }


    /**
     * 刷新数据
     */
    @Override
    public void onRefresh() {
        pagenum = 0;
        fetchListItems(getFetchListItemsParams());
    }

    @Override
    public void onLoadMore() {
        pagenum++;
        fetchMoreListItems(getFetchMoreListItemsParams());
    }

    public void loadSuccess(List<T> items) {
        dismissLoading();
        removeErrorView();
        if (pagenum == 0) {
            setAdapter(items);
        } else {
            if (items != null && items.size() > 0) {
                rvList.refreshComplete();
                datas.addAll(items);
                getAdapter().notifyDataSetChanged();
            } else {
                rvList.noMoreLoading();
            }
        }

    }

    public void loadFailed(String msg) {
        dismissLoading();
        rvList.refreshComplete();
        // 注意：这里不能写成 mPage == 1，否则会一直显示错误页面
        if (getAdapter().getItemCount() == 0) {
            showError();
        }
        if (pagenum > 1) {
            pagenum--;
        }
    }

    private void removeErrorView() {
        containerView.removeView(errorView);
    }

    protected void showError() {
        errorView = View.inflate(getActivity(), R.layout.error_layout, null);
        containerView.addView(errorView);
        errorView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLoading();
                onRefresh();
            }
        });

    }

    /**
     * 设置adapter
     */
    private void setAdapter(List<T> items) {
        datas.clear();
        datas.addAll(items);
        getAdapter().notifyDataSetChanged();
        rvList.refreshComplete();

        mIsFirst = false;
    }

    private class ItemClickListener extends SimpleClickListener {

        @Override
        public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
            this.onItemClick(adapter, view, position);
        }

        @Override
        public void onItemLongClick(BaseQuickAdapter adapter, View view, int position) {
            this.onItemLongClick(adapter, view, position);
        }

        @Override
        public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
            this.onItemChildClick(adapter, view, position);
        }

        @Override
        public void onItemChildLongClick(BaseQuickAdapter adapter, View view, int position) {
            this.onItemChildLongClick(adapter, view, position);
        }
    }
}
