package com.app.base.basefragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.util.ToastUtil;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import com.app.util.AndroidUtil;


public class BaseFragment extends Fragment {

    private static final String TAG = "BaseFragment";
    protected boolean mResumed;
    protected boolean mCreate;
    protected boolean mStopped;
    protected boolean mDestroyed;
	protected boolean mHidden;

	private boolean mResumedForFirstTime;

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mDestroyed = false;
		mCreate = true;
		Log.d(TAG, "[" + getClass().getSimpleName() + "]: onCreate, Hidden = " + mHidden);
	}

    @Override
    public void onStart() {
        super.onStart();
        mStopped = false;
		Log.d(TAG, "[" + getClass().getSimpleName() + "]: onStart, Hidden = " + mHidden);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
		mHidden = hidden;
        Log.d(TAG, "[" + getClass().getSimpleName() + "]: onHiddenChanged " + hidden);
    }

	@Override
	public void onResume() {
		super.onResume();
		mResumed = true;
		if(!mResumedForFirstTime) {
			mResumedForFirstTime = true;
		}
		Log.d(TAG, "[" + getClass().getSimpleName() + "]: onResume, Hidden = " + mHidden);
	}

	@Override
	public void onPause() {
		super.onPause();
		mResumed = false;
		Log.d(TAG, "[" + getClass().getSimpleName() + "]: onPause, Hidden = " + mHidden);
	}

	@Override
	public void onStop() {
		super.onStop();
		mStopped = true;
		Log.d(TAG, "[" + getClass().getSimpleName() + "]: onStop, Hidden = " + mHidden);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		mDestroyed = true;
		mResumedForFirstTime = false;
		Log.d(TAG, "[" + getClass().getSimpleName() + "]: onDestroy, Hidden = " + mHidden);
	}

	@Override
	public void onDetach() {
		super.onDetach();
		Log.d(TAG, "[" + getClass().getSimpleName() + "]: onDetach");
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		Log.d(TAG, "[" + getClass().getSimpleName() + "]: onAttach");
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		Log.d(TAG, "[" + getClass().getSimpleName() + "]: onCreateView");
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		Log.d(TAG, "[" + getClass().getSimpleName() + "]: onViewCreated");
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		Log.d(TAG, "[" + getClass().getSimpleName() + "]: onActivityCreated");
	}

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);
		Log.d(TAG, "[" + getClass().getSimpleName() + "]: setUserVisibleHint " + isVisibleToUser);
	}



	// 在UI线程弹出toast
	public void safetyToast(final String msg) {
		final FragmentActivity activity = getActivity();
		if(activity != null) {
			if(Looper.myLooper() == Looper.getMainLooper()) {
				ToastUtil.toast(activity, msg);
			} else {
				activity.runOnUiThread(new Runnable() {

					@Override
					public void run() {
						ToastUtil.toast(activity, msg);
					}
				});
			}
		}
	}

	public void safetyDismissDialog(Dialog dialog) {
		if (AndroidUtil.isValidContext(getActivity()) && dialog != null && dialog.isShowing()) {
			try {
				dialog.dismiss();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	// 隐藏软键盘
	public void hideSoftInput() {
		Activity context = getActivity();
		if(context != null) {
			InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
			View decorView = context.getWindow().getDecorView();
			imm.hideSoftInputFromWindow(decorView.getWindowToken(), 0);
		}
	}

	// 当前Fragment是否显示，或者需要被显示
	// 当执行onResume方法后，就使用mHidden变量来替代
	public boolean isShow(){
		if(mResumedForFirstTime) {
			return !mHidden;
		} else {
			return mCreate;
		}
	}
}
