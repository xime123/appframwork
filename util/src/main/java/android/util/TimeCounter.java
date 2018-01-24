package android.util;

import android.os.CountDownTimer;
import android.os.Handler;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

/**
 * 倒计时计数器 , 通过getTimeCounter(tag)来获取实例，一个tag对应唯一的一个实例对象
 *
 */
public class TimeCounter {
	private static final Map<String, TimeCounter> sCouters = new HashMap<String, TimeCounter>();

	protected static final String TAG = "Counter";
	private long startTime;
	private int mMaxSec;
	private TimeCountListener mListener;

	// tag 是TimeCounter实例的唯一标识，同一个tag只能对应同一个TimeCounter实例
	public static TimeCounter getTimeCounter(String tag) {
		TimeCounter tc = sCouters.get(tag);
		if (tc == null) {
			tc = new TimeCounter();
			sCouters.put(tag, tc);
		}
		return tc;
	}
	
	private TimeCounter() {
	}

	public boolean isRunning() {
		int left = getLeftTime(System.currentTimeMillis());
		return left >= 0 && left <= mMaxSec;
	}

	private int getLeftTime(long current) {
		int count = mMaxSec - (int) ((current - startTime) / 1000.0F);
		return count;
	}

	/**
	 * 设置倒计时
	 * @param maxSec 倒计总时
	 * @param listener 
	 */
	public void setup(int maxSec, TimeCountListener listener) {
		mMaxSec = maxSec;
		mListener = listener;
	}

	private Handler mHandler = new Handler() ;

	// 开始倒计时
	// FIXME: 倒计时启动后，即使当前activity退出，倒计时仍然会运行，后续需要解决,
	// 目前在销毁Activity时，主动调用quit()方法实现
	public void startTick() {
		long current = System.currentTimeMillis();
		if (!isRunning()) {
			startTime = current;
		}
		mHandler.removeCallbacksAndMessages(null);
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				long current = System.currentTimeMillis();
				int count = getLeftTime(current);
				Log.d(TAG, "handleMessage " + count + ", current=" + current + ", startTime=" + startTime);
				if (count >= 0 && count <= mMaxSec) {
					mHandler.postDelayed(this, 1000);
					if (mListener != null) {
						mListener.onTicked(count);
					}
				}
			}
		});
	}
	
	// 不再进行tick回调，但是计数器时间仍会维持
	public void quit(){
		mHandler.removeCallbacksAndMessages(null);
	}

	public interface TimeCountListener {
		void onTicked(int count);
	}


	public static void timerBack(final TextView textView, long totalTime, long intervalTime) {
		CountDownTimer timer = new CountDownTimer(totalTime, intervalTime) {

			@Override
			public void onTick(long millisUntilFinished) {
				textView.setEnabled(false);
				textView.setText(millisUntilFinished / 1000 + "s");
			}

			@Override
			public void onFinish() {
				textView.setEnabled(true);
				textView.setText("重新获取");

			}
		};
		timer.start();
	}
}
