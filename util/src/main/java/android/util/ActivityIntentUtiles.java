package android.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

/**
 * Activity跳转管理类
 *
 * @author Robert
 *
 */
public class ActivityIntentUtiles {
	/**
	 * Activity Finish Jump
	 */
	public static void gotoActivityFinish(Context context, Class<?> actClass) {
		Intent intent = new Intent(context, actClass);
		context.startActivity(intent);
		((Activity) context).finish();
	}

	/**
	 * Activity Not Finish Jump
	 */
	public static void gotoActivityNotFinish(Context context, Class<?> actClass) {
		Intent intent = new Intent(context, actClass);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent);
	}

	/**
	 * Activity Finish Jump with Bundle
	 */
	public static void gotoActivityWithBundle(Context context, Class<?> actClass, Bundle bundle) {
		Intent intent = new Intent(context, actClass);
		if (bundle != null) {
			intent.putExtras(bundle);
		}
		context.startActivity(intent);
		((Activity) context).finish();
	}


	/**
	 * Activity Not Finish Jump with Action
	 */
	public static void gotoActivityNotFinishWithAction(Context context, Class<?> actClass, String action) {
		Intent intent = new Intent(context, actClass);
		intent.setAction(action);
		context.startActivity(intent);
	}

	/**
	 * Activity  Finish Jump with Action
	 */
	public static void gotoActivityFinishWithAction(Context context, Class<?> actClass, String action) {
		Intent intent = new Intent(context, actClass);
		intent.setAction(action);
		context.startActivity(intent);
		((Activity) context).finish();
	}

	/**
	 * Activity Finish Jump for Result
	 */
	public static void gotoActivityForResult(Context context, Class<?> actClass,
                                             int requestCode) {
		Intent intent = new Intent(context, actClass);
		((Activity)context).startActivityForResult(intent, requestCode);
	}

	/**
	 * Activity Not Finish Jump with Bundle
	 */
	public static void gotoActivityNotFinishWithBundle(Context context, Class<?> actClass,
                                                       Bundle bundle) {
		Intent intent = new Intent(context, actClass);
		intent.putExtras(bundle);
		context.startActivity(intent);
	}

	/**
	 * Activity Not Finish Jump With Bundle for Result
	 */
	public static void gotoActivityForResultWithBundle(Context context, Class<?> actClass,
                                                       Bundle bundle, int key) {
		Intent intent = new Intent(context, actClass);
		intent.putExtras(bundle);
		((Activity)context).startActivityForResult(intent, key);
	}

	/**
	 * New task activity not finish jump
	 * @param context
	 * @param actClass
     */
	public static void gotoNewTaskActivityNotFinish(Context context, Class<?> actClass) {
		Intent intent = new Intent(context,actClass);
		intent.addCategory(Intent.CATEGORY_DEFAULT);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent);
	}

	/**
	 *  Activity not finish jump put string value
	 * @param context
	 * @param actClass
	 */
	public static void gotoActivityNotFinishPutStringExtra(Context context, Class<?> actClass, String key , String value) {
		Intent intent = new Intent(context,actClass);
		intent.putExtra(key,value);
		context.startActivity(intent);
	}

}
