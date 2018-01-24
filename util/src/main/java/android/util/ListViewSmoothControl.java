package android.util;

import android.widget.AbsListView;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * 利用反射原理停止listView滚动
 *
 * @author Robert
 */
public class ListViewSmoothControl {
    private static Field mFlingEndField = null;
    private static Method mFlingEndMethod = null;

    public static ListViewSmoothControl getInstance() {
        ListViewSmoothControl control = ListViewSmoothControlHolder.SMOOTH_CONTROL;
        control.init();
        return control;
    }

    private static class ListViewSmoothControlHolder {
        private static final ListViewSmoothControl SMOOTH_CONTROL = new ListViewSmoothControl();
    }

    public void init() {
        try {
            mFlingEndField = AbsListView.class.getDeclaredField("mFlingRunnable");
            mFlingEndField.setAccessible(true);
            mFlingEndMethod = mFlingEndField.getType().getDeclaredMethod("endFling");
            mFlingEndMethod.setAccessible(true);
        } catch (Exception e) {
            mFlingEndMethod = null;
        }
    }

    public static void listScrollOff(AbsListView list) {
        if (list == null) {
            return;
        }
        if (mFlingEndMethod != null) {
            try {
                mFlingEndMethod.invoke(mFlingEndField.get(list));
            } catch (Exception e) {
            }
        }
    }
}
