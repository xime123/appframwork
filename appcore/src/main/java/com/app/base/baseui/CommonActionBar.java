package com.app.base.baseui;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.app.R;


/**
 * 标题统一类
 */
public class CommonActionBar extends RelativeLayout {

    private ImageView mBackIv;
    private ImageView mCloseIv;
    private ImageView mMenu1Iv;
    private ImageView mMenu2Iv;
    private TextView mTitleTv;
    private TextView mRightTv;

    private boolean showBack;
    private boolean showMenu1;
    private boolean showMenu2;
    private int backResId = -1;
    private int menu1ResId = -1;
    private int menu2ResId = -1;
    private String title;

    View mCommonDividerV;

    public CommonActionBar(Context context) {
        this(context, null);
    }

    public CommonActionBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CommonActionBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        View.inflate(context, R.layout.layout_actionbar_common_merge, this);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CommonActionBar);
        showBack = a.getBoolean(R.styleable.CommonActionBar_showBack, true);
        showMenu1 = a.getBoolean(R.styleable.CommonActionBar_showMenu1, false);
        showMenu2 = a.getBoolean(R.styleable.CommonActionBar_showMenu2, false);
        backResId = a.getResourceId(R.styleable.CommonActionBar_backSrc, -1);
        menu1ResId = a.getResourceId(R.styleable.CommonActionBar_menu1Src, -1);
        menu2ResId = a.getResourceId(R.styleable.CommonActionBar_menu2Src, -1);
        title = a.getString(R.styleable.CommonActionBar_titleText);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mBackIv = (ImageView) findViewById(R.id.iv_back);
        mCloseIv=(ImageView)findViewById(R.id.close_iv);
        mBackIv.setVisibility(showBack ? VISIBLE : INVISIBLE);
        if (backResId != -1)
            mBackIv.setImageResource(backResId);

        mMenu1Iv = (ImageView) findViewById(R.id.iv_menu1);
        mMenu1Iv.setVisibility(showMenu1 ? VISIBLE : INVISIBLE);
        if (menu1ResId != -1)
            mMenu1Iv.setImageResource(menu1ResId);

        mMenu2Iv = (ImageView) findViewById(R.id.iv_menu2);
        mMenu2Iv.setVisibility(showMenu2 ? VISIBLE : INVISIBLE);
        if (menu2ResId != -1)
            mMenu2Iv.setImageResource(menu2ResId);

        mTitleTv = (TextView) findViewById(R.id.tv_title);
        mTitleTv.setText(title);
        mRightTv=(TextView)findViewById(R.id.right_tv);
        mCommonDividerV = findViewById(R.id.action_bar_common_divider_v);
    }

    public CommonActionBar setColseIvVisiable(boolean isShow){
        mCloseIv.setVisibility(isShow?VISIBLE:GONE);
        return this;
    }

    public void setOnCloseIvClickListener(OnClickListener listener){
        mCloseIv.setOnClickListener(listener);
    }

    public CommonActionBar showBack(boolean isShow) {
        mBackIv.setVisibility(isShow ? VISIBLE : INVISIBLE);
        return this;
    }

    public CommonActionBar showMenu1(boolean isShow) {
        mMenu1Iv.setVisibility(isShow ? VISIBLE : INVISIBLE);
        return this;
    }

    public CommonActionBar showMenu2(boolean isShow) {
        mMenu2Iv.setVisibility(isShow ? VISIBLE : INVISIBLE);
        return this;
    }

    public CommonActionBar setTitle(String title) {
        mTitleTv.setText(title);
        return this;
    }

    public CommonActionBar setTitle(@StringRes int title) {
        mTitleTv.setText(title);
        return this;
    }

    public CommonActionBar setRightTv(String title){
        mRightTv.setVisibility(VISIBLE);
        mRightTv.setText(title);
        return this;
    }

    public CommonActionBar setRightTv(@StringRes int title){
        mRightTv.setVisibility(VISIBLE);
        mRightTv.setText(title);
        return this;
    }

    public CommonActionBar setOnBackClickListener(OnClickListener listener) {
        mBackIv.setOnClickListener(listener);
        return this;
    }
    public CommonActionBar setBackIconVisible(boolean visible) {
        mBackIv.setVisibility(visible?VISIBLE:GONE);
        return this;
    }
    public CommonActionBar setOnMenu1ClickListener(OnClickListener listener) {
        mMenu1Iv.setOnClickListener(listener);
        return this;
    }

    public CommonActionBar setOnMenu2ClickListener(OnClickListener listener) {
        mMenu2Iv.setOnClickListener(listener);
        return this;
    }

    public CommonActionBar setBackResource(@DrawableRes int resId) {
        mBackIv.setImageResource(resId);
        return this;
    }

    public CommonActionBar setMenu1Resource(@DrawableRes int resId) {
        mMenu1Iv.setImageResource(resId);
        mMenu1Iv.setVisibility(VISIBLE);
        return this;
    }

    public CommonActionBar setMenu2Resource(@DrawableRes int resId) {
        mMenu2Iv.setImageResource(resId);
        mMenu2Iv.setVisibility(VISIBLE);
        return this;
    }

    public CommonActionBar setActionbarDividerVisiable(boolean visiable) {
        if (mCommonDividerV != null) {
            mCommonDividerV.setVisibility(visiable ? VISIBLE : GONE);
        }
        return this;
    }

    public void setActionbarDividerVisiable(int visiable) {
        if (mCommonDividerV != null) {
            mCommonDividerV.setVisibility(visiable);
        }
    }

    public void setOnRightTvClickListener(OnClickListener listener){
        if(mRightTv!=null){
            mRightTv.setOnClickListener(listener);
        }
    }
}
