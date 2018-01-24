package android.util;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.TextView;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommonUtil {
    private static final double EARTH_RADIUS = 6378.137; //地图半径（单位：KM）

    public static void makeACall(String phonenUmber, Context context) {
        Intent callIntent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phonenUmber));
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            context.startActivity(callIntent);
            return;
        }

    }

    public static boolean isValidContext(Context c) {

        Activity a = (Activity) c;

        if (a.isFinishing()) {
            return false;
        } else {
            return true;
        }
    }

    public static boolean isMainProcess(Context context) {
        ActivityManager am = ((ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE));
        List<ActivityManager.RunningAppProcessInfo> processInfos = am.getRunningAppProcesses();
        String mainProcessName = context.getPackageName();
        int myPid = android.os.Process.myPid();
        for (ActivityManager.RunningAppProcessInfo info : processInfos) {
            if (info.pid == myPid && mainProcessName.equals(info.processName)) {
                return true;
            }
        }
        return false;
    }

    public static void hideSoftKeyBoard(View v) {
         /* 隐藏软键盘 */
        InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(
                Context.INPUT_METHOD_SERVICE);
        if (imm.isActive()) {
            imm.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);
        }
    }

    public static int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }


    // 角度转弧度
    private static double rad(double d) {
        return d * Math.PI / 180.0D;
    }

    /**
     * 计算两个经纬度点之间的距离（单位：M）
     */
    public static double getDistance(double lat1, double lng1, double lat2, double lng2) {
        double radLat1 = rad(lat1);
        double radLat2 = rad(lat2);
        double a = radLat1 - radLat2;
        double b = rad(lng1) - rad(lng2);
        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2) +
                Math.cos(radLat1) * Math.cos(radLat2) * Math.pow(Math.sin(b / 2), 2)));
        s = s * EARTH_RADIUS * 1000;
        //s = Math.round(s * 10000) / 10000;
        return s;
    }

    /**
     * 设置文本，改变textView部分文字颜色,实现点击指定文字
     *
     * @param tv     textView控件
     * @param str    原文本
     * @param regExp 正则表达式
     * @returnType void
     */
    public static void richText(TextView tv, String str, String regExp, ClickableSpan span) {
        SpannableStringBuilder style = new SpannableStringBuilder(str);
        Pattern p = Pattern.compile(regExp, Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(str);
        while (m.find()) {
            int start = m.start(0);
            int end = m.end(0);
            //Android4.0以上默认是淡绿色，低版本的是黄色。解决方法就是通过重新设置文字背景为透明色
            tv.setHighlightColor(tv.getResources().getColor(android.R.color.transparent));
            style.setSpan(span, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        tv.setText(style);
        tv.setMovementMethod(LinkMovementMethod.getInstance());
    }

    public static int parseColor(String colorStr, String defaultColor) {
        int color;
        try {
            color = Color.parseColor(colorStr);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            color = Color.parseColor(defaultColor);
        }
        return color;
    }

    /**
     * 获取listView滑动的距离
     *
     * @return
     */
    public static int listViewScrollY(AbsListView listView) {
        if (listView == null) {
            return 0;
        }
        View view = listView.getChildAt(0);
        if (view == null) {
            return 0;
        }
        int firstVisiblePosition = listView.getFirstVisiblePosition();
        int top = view.getTop();
        return -top + firstVisiblePosition * view.getHeight();
    }


    /**
     * 获取屏幕的高度
     *
     * @param context
     * @return
     */
    public static int getScreenHeight(Context context) {
        WindowManager manager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();
        return display.getHeight();
    }
}
