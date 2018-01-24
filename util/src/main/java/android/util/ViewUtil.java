package android.util;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.Html;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;



import static android.view.View.GONE;
import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;


public class ViewUtil {
    private ViewUtil() {
    }

    @SuppressWarnings("unchecked")
    public static <T extends View> T findView(Activity a, Integer id) {
        if (a == null) {

            return null;
        }
        return (T) a.findViewById(id);
    }

    /**
     * 查找指定的View并设置点击事件
     *
     * @param a        Activity
     * @param id       View的Id
     * @param listener 点击事件
     */
    public static void findViewAndClick(Activity a, Integer id, OnClickListener listener) {
        setOnClick(findView(a, id), listener);
    }

    /**
     * 在View中查找指定的View .
     *
     * @param view 包含指定Id的View
     * @param id   View的Id
     * @return 查找到的View
     */
    @SuppressWarnings("unchecked")
    public static <T extends View> T findView(View view, Integer id) {
        if (view == null) {

            return null;
        }
        return (T) view.findViewById(id);
    }

    /**
     * 查找指定的View并设置点击事件
     *
     * @param view     包含指定Id的View
     * @param id       View的Id
     * @param listener 点击事件
     */
    public static void findViewAndClick(View view, Integer id, OnClickListener listener) {
        setOnClick(findView(view, id), listener);
    }

    /**
     * 查找指定的View并设置点击事件
     *
     * @param view 包含指定Id的View
     * @param id   View的Id
     */
    public static void findTextViewAndSetText(View view, Integer id, CharSequence text) {
        View target = findView(view, id);
        if (!(target instanceof TextView)) {

            return;
        }
        ((TextView) target).setText(text);
    }

    public static void findTextViewAndSetText(Activity activity, Integer id, CharSequence text) {
        View target = findView(activity, id);
        if (!(target instanceof TextView)) {

            return;
        }
        ((TextView) target).setText(text);
    }

    /**
     * 查找Fragment
     *
     * @param fm FragmentManager 对象
     * @param id Fragment的Id
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T extends Fragment> T findFragment(FragmentManager fm, Integer id) {
        if (fm == null) {

            return null;
        }
        return (T) fm.findFragmentById(id);
    }

    /**
     * 查找Fragment
     *
     * @param fm  FragmentManager 对象
     * @param tag Fragment的Tag
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T extends Fragment> T findFragment(FragmentManager fm, String tag) {
        if (fm == null) {

            return null;
        }
        return (T) fm.findFragmentByTag(tag);
    }

    /**
     * 设置View的Visibility为GONE
     *
     * @param view 需要设置为View.GONE的View
     * @param gone 是否这是为View.GONE
     */
    public static void setGone(final View view, final boolean gone) {
        if (view == null) {

            return;
        }
        final int current = view.getVisibility();
        if (gone && current != GONE)
            view.setVisibility(GONE);
        else if (!gone && current != VISIBLE)
            view.setVisibility(VISIBLE);
    }

    /**
     * 设置View的Visibility为INVISIBLE
     *
     * @param view      需要设置为View.INVISIBLE的View
     * @param invisible 是否这是为View.INVISIBLE
     */
    public static void setInvisible(final View view, final boolean invisible) {
        if (view == null) {

            return;
        }
        final int current = view.getVisibility();
        if (invisible && current != INVISIBLE)
            view.setVisibility(INVISIBLE);
        else if (!invisible && current != VISIBLE)
            view.setVisibility(VISIBLE);
    }

    /**
     * 设置View的点击事件
     *
     * @param view     需要设置的View
     * @param listener 点击事件
     */
    public static void setOnClick(View view, OnClickListener listener) {
        if (view != null) {
            view.setOnClickListener(listener);
        }
    }

    /**
     * 设置View是否可用
     *
     * @param view   需要设置的View
     * @param enable 是否可用
     */
    public static void setEnable(View view, boolean enable) {
        if (view != null) {
            view.setEnabled(enable);
        }
    }

    /**
     * 设置TextView的Text
     *
     * @param view TextView对象
     * @param text 字符串
     */
    public static void setText(TextView view, String text) {
        if (view != null) {
            view.setText(text);
            if (view instanceof EditText && !TextUtils.isEmpty(text)) {
                ((EditText) view).setSelection(text.length());
            }
        }
    }

    /**
     * 设置TextView的Text
     *
     * @param view  TextView对象
     * @param resId 字符串的资源ID
     */
    public static void setText(TextView view, Integer resId) {
        if (view != null && resId != null) {
            view.setText(resId);
        }
    }

    /**
     * 设置TextView的Text
     *
     * @param view TextView对象
     * @param text 可能包含html的字符串
     */
    public static void setTextMaybeHtml(TextView view, String text) {
        if (TextUtils.isEmpty(text)) {
            view.setText("");
            return;
        }
        if (text.contains("<") && text.contains(">")) {
            view.setText(Html.fromHtml(text));
            view.setMovementMethod(LinkMovementMethod.getInstance());
        } else {
            view.setText(text);
        }
    }
}
