package com.app.base.baseactivity;

import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.app.R;
import com.app.base.baseui.loadingdialog.IRefreshView;
import com.app.util.TDString;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.listener.SimpleClickListener;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;


/**
 * Created by 徐敏 on 2017/5/25.
 * 谷歌推荐的 列表刷新页面
 */

public abstract class BaseSwipRefreshListActivity<T> extends DataSwipRefreshActivity implements BaseQuickAdapter.RequestLoadMoreListener, IRefreshView {
    protected RecyclerView rvList;
    private ItemClickListener mItemClickListener;
    protected BaseQuickAdapter<T, ? extends BaseViewHolder> mAdapter;
    protected int pagenum = 0;
    protected int pagesize = 10;

    protected abstract void fetchListItems(@NonNull Map<String, String> params);

    protected abstract void fetchMoreListItems(@NonNull Map<String, String> params);


    @Override
    protected int getLayoutRes() {
        return R.layout.activity_base_swiprefresh_list_layout;
    }


    @Override
    public void onDataEmpty() {

    }


    @NonNull
    protected Map<String, String> getFetchListItemsParams() {
        return new HashMap<>(0);
    }

    @NonNull
    protected Map<String, String> getFetchMoreListItemsParams() {
        Map<String, String> params = new HashMap<>();
        params.put("pagenum", pagenum+"");
        return params;
    }

    /**
     * RecyclerView 的Adapter , 用于展示Item
     */
    protected abstract BaseQuickAdapter<T, BaseViewHolder> createAdapter();

    @Override
    protected void initView(ViewGroup contentView) {
        super.initView(contentView);
        rvList =(RecyclerView)findViewById(R.id.recycler_view);

        mAdapter = createAdapter();

        rvList.setAdapter(mAdapter);
        setLoadMoreEnable(true);
        rvList.setLayoutManager(buildLayoutManager());
     //   rvList.setItemAnimator(new DefaultItemAnimator());
            //目前不需要分割线
//        for (RecyclerView.ItemDecoration itemDecoration : buildItemDecorations()) {
//            rvList.addItemDecoration(itemDecoration);
//        }
        mItemClickListener = new ItemClickListener();
        rvList.addOnItemTouchListener(mItemClickListener);
        showLoading(TDString.getStr(R.string.loading),"",false);
        fetchListItems(getFetchListItemsParams());

        mAdapter.openLoadAnimation(BaseQuickAdapter.ALPHAIN);
        rvList.setItemAnimator(null);
    }

    public BaseQuickAdapter<T, ? extends BaseViewHolder> getAdapter() {
        return mAdapter;
    }

    public void updateListItems(List<T> items) {
        getAdapter().setNewData(items);
    }

    public void appendListItems(List<T> items) {
        getAdapter().addData(items);
    }


    public void loadMoreComplete() {
        Observable.timer(50, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new Action1<Long>() {
                @Override
                public void call(Long aLong) {
                    getAdapter().loadMoreComplete();
                }
            });
        pagenum++;
    }

    public void loadMoreFail() {
        Observable.timer(50, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new Action1<Long>() {
                @Override
                public void call(Long aLong) {
                    getAdapter().loadMoreFail();
                }
            });
    }

    public void loadMoreEnd() {
        Observable.timer(50, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new Action1<Long>() {
                @Override
                public void call(Long aLong) {
                    getAdapter().loadMoreEnd(isHideLoadMoreEnd());
                }
            });
    }

    protected List<? extends RecyclerView.ItemDecoration> buildItemDecorations() {
        return Collections.singletonList(new HorizontalDividerItemDecoration.Builder(this)
            .colorResId(R.color.colorPrimary)
            .sizeResId(R.dimen.divide)
            .build());
    }


    protected RecyclerView.LayoutManager buildLayoutManager() {
        return new LinearLayoutManager(this);
    }

    protected void setLoadMoreEnable(boolean enable) {
        if (mAdapter == null) return;
        if (enable) {
            mAdapter.setOnLoadMoreListener(this, rvList);
        } else {
            mAdapter.setOnLoadMoreListener(null, rvList);
        }
    }

    @Override
    public void onLoadMoreRequested() {

        swipeRefreshLayout.setEnabled(false);
        if (mAdapter.getData().size() < pagesize) {
            mAdapter.loadMoreEnd(true);
        } else {
            fetchMoreListItems(getFetchMoreListItemsParams());
            swipeRefreshLayout.setEnabled(true);
        }

        if (getAdapter().getData().size() >= pagesize) {
            fetchMoreListItems(getFetchMoreListItemsParams());
        } else {
            loadMoreEnd();
        }
    }
    @Override
    public void onDestroy() {
        rvList.removeOnItemTouchListener(mItemClickListener);
        rvList.clearOnScrollListeners();
        super.onDestroy();
    }
    private class ItemClickListener extends SimpleClickListener {

        @Override
        public void onItemClick(BaseQuickAdapter adapter, android.view.View view, int position) {
            BaseSwipRefreshListActivity.this.onItemClick(((T) adapter.getItem(position)), adapter, view, position);
        }

        @Override
        public void onItemLongClick(BaseQuickAdapter adapter, android.view.View view, int position) {
            BaseSwipRefreshListActivity.this.onItemLongClick(((T) adapter.getItem(position)), adapter, view, position);
        }

        @Override
        public void onItemChildClick(BaseQuickAdapter adapter, android.view.View view, int position) {
            BaseSwipRefreshListActivity.this.onItemChildClick(((T) adapter.getItem(position)), adapter, view, position);
        }

        @Override
        public void onItemChildLongClick(BaseQuickAdapter adapter, android.view.View view, int position) {
            BaseSwipRefreshListActivity.this.onItemChildLongClick(((T) adapter.getItem(position)), adapter, view, position);
        }
    }

    public void onItemClick(T item, BaseQuickAdapter adapter, android.view.View view, int position) {
    }

    public void onItemLongClick(T item, BaseQuickAdapter adapter, android.view.View view, int position) {
    }

    public void onItemChildClick(T item, BaseQuickAdapter adapter, android.view.View view, int position) {
    }

    public void onItemChildLongClick(T item, BaseQuickAdapter adapter, android.view.View view, int position) {
    }


    @Override
    public void onFinishLoading() {
        super.onFinishLoading();
        dismissLoading();
        swipeRefreshLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                setRefreshing(false);
            }
        }, 2000);
    }

    protected boolean isHideLoadMoreEnd() {
        return false;
    }


    /**
     * 刷新数据
     */
    public void refresh() {
        pagenum = 0;
        setRefreshing(true);
        fetchListItems(getFetchListItemsParams());
    }

}
