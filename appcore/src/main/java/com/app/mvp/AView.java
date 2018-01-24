package com.app.mvp;

import android.app.Activity;

import com.trello.rxlifecycle.LifecycleProvider;
import com.trello.rxlifecycle.android.ActivityEvent;

/**
 * Activity 对应的 IView  <br/>
 */
public interface AView extends IView, LifecycleProvider<ActivityEvent> {
    Activity getActivity();
}
