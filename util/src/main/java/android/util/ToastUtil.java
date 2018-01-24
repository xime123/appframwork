package android.util;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Villey on 2016/7/9.
 */
public class ToastUtil {
    private static Toast mToast;
    //双重锁定，使用同一个Toast实例,就不会连续弹出多个toast
    private  static Toast getInstance(Context context){
        if(mToast == null){
            synchronized (ToastUtil.class){
                if(mToast == null) {
                    mToast = new Toast(context);
                }
            }
        }
        return mToast;
    }

    private static void toast(Context context, String msg, int duration) {
        Toast toast = getInstance(context);
        TextView tv = new TextView(context);
        int padding = DensityUtils.dp2px(context, 12);  // 12dp
        tv.setText(msg);
        tv.setPadding(padding, padding, padding, padding);
        tv.setBackgroundResource(R.drawable.toast_background);
        tv.setTextSize(14);  // 12sp
        tv.setTextColor(Color.WHITE);
        toast.setView(tv);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.setDuration(duration);
        toast.show();
    }


    public static void toast(Context context, String msg){
        toast(context, msg, Toast.LENGTH_SHORT);
    }

    public static void toast(Context context, int msgResId){
        toast(context, context.getResources().getString(msgResId), Toast.LENGTH_SHORT);
    }

    public static void toastLong(Context context, String msg){
        toast(context, msg, Toast.LENGTH_LONG);
    }

    public static void toastLong(Context context, int msgResId){
        toast(context, context.getResources().getString(msgResId), Toast.LENGTH_LONG);
    }
}
