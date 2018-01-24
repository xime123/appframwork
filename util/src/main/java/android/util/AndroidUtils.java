package android.util;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.view.MotionEvent;
import android.view.View;

public class AndroidUtils {
	private final static String TAG = AndroidUtils.class.getSimpleName();

	/**
	 * 检查context对应的Activity的状态
	 *
	 * @return
	 */
	public static boolean isValidContext(Activity activity) {
		if(activity == null ){
			return false;
		}
		if (isDestroyed(activity) || activity.isFinishing()) {
			Log.i(TAG, "Activity is invalid." + "[isFinishing]:" + activity.isFinishing());
			return false;
		} else {
			return true;
		}
	}
	
	@TargetApi(17)
	private static boolean isDestroyed(Activity activity) {
		// TODO Auto-generated method stub
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
			return activity.isDestroyed();
		}
		return false;
	}

	/**
	 * 判断是否在v之外区域(目前适用EditText之外点击隐藏键盘)
	 * @param v
	 * @param event
     * @return
     */
	public static boolean isOutSizeView(View v, MotionEvent event) {
		if (v != null && event != null) {
			int[] leftTop = { 0, 0 };
			v.getLocationInWindow(leftTop);
			int left = leftTop[0];
			int top = leftTop[1];
			int bottom = top + v.getHeight();
			int right = left + v.getWidth();
			if (event.getX() > left && event.getX() < right
					&& event.getY() > top && event.getY() < bottom) {
				return false;
			} else {
				return true;
			}
		}
		return false;
	}
}
