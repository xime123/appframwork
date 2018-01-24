package com.app.mvp;

import android.support.v4.app.Fragment;

import com.trello.rxlifecycle.LifecycleProvider;
import com.trello.rxlifecycle.android.FragmentEvent;

/**
 * Fragment 对应的 IView  <br/>
 */
public interface FView extends IView, LifecycleProvider<FragmentEvent> {
    Fragment getFragment();
}
