package com.app.base.baseui;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.CoreApp;
import com.app.R;
import com.wang.avi.AVLoadingIndicatorView;


/**
 * Created by 徐敏 on 2017/8/31.
 */
public class ToastDialog extends Dialog {
    private View v;
    private TextView tipTextView;
    private ImageView img;
    private TextView desTv;
    private Context context;
    private AVLoadingIndicatorView loadingView;
    public ToastDialog(Context context) {
        super(context);
    }

    public ToastDialog(Context context, int themeResId) {
        super(context, themeResId);
        v = View.inflate(CoreApp.getAppInstance(), R.layout.customer_dialog, null);
        tipTextView = (TextView) v.findViewById(R.id.tipTextView);
        desTv=(TextView)v.findViewById(R.id.toast_des_tv);
        img = (ImageView) v.findViewById(R.id.img);
        loadingView=(AVLoadingIndicatorView)v.findViewById(R.id.av_loading_view);
        this.setContentView(v);
        this.context = context;
    }



    public void showLoadingDialog(final String parameter,String des, int resId,boolean loading, final boolean cancelable) {
            final Activity activity = (Activity) context;
            // TODO Auto-generated method stub
            if (activity != null && !activity.isFinishing()) {
                tipTextView.setText(parameter);
                img.setImageResource(resId);
                if(!TextUtils.isEmpty(des)){
                    desTv.setVisibility(View.VISIBLE);
                    desTv.setText(des);
                }
                if(loading){
                    loadingView.setVisibility(View.VISIBLE);
                    img.setVisibility(View.GONE);
                }
                ToastDialog.this.setCancelable(cancelable);
                ToastDialog.this.setCanceledOnTouchOutside(cancelable);
                ToastDialog.this.show();
            }

    }
}
