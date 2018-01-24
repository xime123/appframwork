package com.app.base.baseui.loadingdialog;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import com.app.R;


/**
 * Created by soffice_user on 2015/9/9.
 */
public class LoadingDialog {
    private static LoadingDialog mLoading;
    public static Dialog loadingDialog;

    public static void loadingDialog(Context ct) {
        cancleDialog();
        View view = LayoutInflater.from(ct).inflate(R.layout.loading_dialog, null);
        loadingDialog = new Dialog(ct, R.style.style_loading_dialog);
        loadingDialog.setContentView(view);
        loadingDialog.setCanceledOnTouchOutside(false);
        loadingDialog.show();
    }

    public static void cancleDialog() {
        if (loadingDialog != null && loadingDialog.isShowing()) {
            loadingDialog.dismiss();
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//                if (loadingDialog.getWindow().getDecorView().isAttachedToWindow()) {
//                    loadingDialog.dismiss();
//                } else {
//                    Log.d("", " warn dialog not attached to window!!!");
//                }
//            } else {
//                loadingDialog.dismiss();
//            }
        }
        loadingDialog = null;

    }

}
