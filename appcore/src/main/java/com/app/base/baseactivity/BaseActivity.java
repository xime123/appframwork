package com.app.base.baseactivity;


import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.SystemBarTintManager;
import android.view.Window;
import android.view.WindowManager;

import com.app.R;
import com.app.activity.CoreActivity;
import com.app.base.baseui.ToastDialog;
import com.app.base.baseui.loadingdialog.LoadingDialog;


/**
 * Created by 徐敏 on 2017/5/25.
 */

public abstract class BaseActivity extends CoreActivity {
    //handler code
    public static final int DISMISS_DIALOG = 1;
    public static final int DIALOG_SHOW_TIME = 2 * 1000;

    private ToastDialog customerDialog;
    protected   Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
                dismissDialog();
            }
    };


    private SystemBarTintManager mTintManager;
    // 设置沉浸式状态栏
    public void setStatusBarTranslucent(int color, int alpha) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
        }
        mTintManager = new SystemBarTintManager(this);
        mTintManager.setStatusBarTintEnabled(true);
        mTintManager.setStatusBarAlpha(alpha);
        mTintManager.setStatusBarTintColor(color);
    }

    @TargetApi(19)
    private void setTranslucentStatus(boolean on) {
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }




    /**
     * 正确
     * @param content    内容
     * @param cancelable 是否可取消
     */
    public  void showSuccessToast(final String content, final String contentDes, final boolean cancelable) {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
               startToast(content,contentDes,cancelable);
            }
        },200);

    }

    /**
     * 弹出框
     * @param content
     * @param contentDes
     * @param cancelable
     */
    private void startToast(final String content, final String contentDes, final boolean cancelable){
        showDialog(this,content,contentDes, R.mipmap.complete,false,cancelable);
    }
    /**
     * 错误
     * @param content    内容
     * @param cancelable 是否可取消
     */
    public  void showErrorToast( String content, String contentDes, boolean cancelable) {
        showDialog(this,content,contentDes,R.mipmap.error,false,cancelable);
    }


    /**
     * 警告
     * @param content    内容
     * @param cancelable 是否可取消
     */
    public  void showWarningToast( String content, String contentDes, boolean cancelable) {
        dismissLoading();
        showDialog(this,content,contentDes,R.mipmap.hint,false,cancelable);
    }

    /**
     * 加载loading
     * @param content    内容
     * @param cancelable 是否可取消
     */
    public void showLoading(String content, String contentDes, boolean cancelable) {
        showDialog(this,content,contentDes,R.mipmap.hint,true,cancelable);
    }

    /**
     * 加载loading
     */
    public void showDefaultLoading() {
        LoadingDialog.loadingDialog(this);
    }
    protected   void  dismissDialog() {
            if (customerDialog != null) {
                if (customerDialog.isShowing()) {
                    customerDialog.dismiss();
                }
                customerDialog=null;
            }
    }


    private   void showDialog(Activity activity, String parameter,String des, int resID,boolean isLoading, boolean cancelable) {
         dismissDialog();
        if (activity != null) {
            customerDialog = new ToastDialog(activity, R.style.DialogTranslucentNoTitle);
            customerDialog.showLoadingDialog(parameter, des,resID, isLoading,cancelable);
        } else {
            if(customerDialog.isShowing()){
                mHandler.removeMessages(DISMISS_DIALOG);
                customerDialog.dismiss();
            }
            customerDialog.show();
        }
        if(!isLoading)
        mHandler.sendEmptyMessageDelayed(DISMISS_DIALOG, DIALOG_SHOW_TIME);

    }



    public void dismissLoading() {
        dismissDialog();
        LoadingDialog.cancleDialog();
    }

    @Override
    protected void onStop() {
        super.onStop();
        dismissDialog();
        LoadingDialog.cancleDialog();
    }
}
