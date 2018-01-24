package com.app.activity;

import android.os.Bundle;
import android.util.Log;

import com.trello.rxlifecycle.components.support.RxAppCompatActivity;

import rx.Subscription;
import rx.subscriptions.CompositeSubscription;


public class CoreActivity extends RxAppCompatActivity {

	private static final String TAG = "CoreActivity";
	protected boolean mResumed;
	protected boolean mCreated;
	protected boolean mStopped;
	protected boolean mIsDestroyed;
	private CompositeSubscription mCompositeSubscription;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG, getClass().getName() + ":: onCreate");
	//	ActivityManager.getInstance().setActivityAttribute(this);
		ActivityManager.getInstance().addManagedActivity(this);
		mCreated = true;
		mIsDestroyed = false;
	}

	@Override
	protected void onStart() {
		super.onStart();
		Log.d(TAG, getClass().getName() + ":: onStart");
		mStopped = false;
	}

	@Override
	protected void onResume() {
		super.onResume();
		Log.d(TAG, getClass().getName() + ":: onResume");
		mResumed = true;
		ActivityManager.getInstance().setIsForeGround(true);
		ActivityManager.getInstance().setCurrActivity(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		Log.d(TAG, getClass().getName() + ":: onPause");
		mResumed = false;
		ActivityManager.getInstance().setIsForeGround(false);
		// 及时置空，避免内存溢出
		ActivityManager.getInstance().setCurrActivity(null);
	}

	@Override
	protected void onStop() {
		super.onStop();
		Log.d(TAG, getClass().getName() + ":: onStop");
		mStopped = true;
	}

	protected void onDestroy() {
		Log.d(TAG, getClass().getName() + ":: onDestroy");
		ActivityManager.getInstance().removeManagedActivity(this);
		removeSubscription();
		super.onDestroy();
		mIsDestroyed = true;
	}

	public void addSubscription(Subscription s) {
		if (this.mCompositeSubscription == null) {
			this.mCompositeSubscription = new CompositeSubscription();
		}
		this.mCompositeSubscription.add(s);
	}

	public void removeSubscription() {
		if (this.mCompositeSubscription != null && mCompositeSubscription.hasSubscriptions()) {
			this.mCompositeSubscription.unsubscribe();
		}
	}

}
