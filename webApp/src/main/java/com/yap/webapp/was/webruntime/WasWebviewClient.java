package com.yap.webapp.was.webruntime;

import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.yap.webapp.plugin.PluginManager;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class WasWebviewClient extends WebViewClient
{
    private static final String TAG = "WebRuntime";
    private WasWebview mWebview;
    private boolean isAlreadNotifyLoadObserver=false;

    public WasWebviewClient(WasWebview webview)
    {
        this.mWebview = webview;
    }

//    private static String buildDeviceJS()
//    {
//        if (deviceJS == null)
//        {
//            StringBuffer sb = new StringBuffer();
//            sb.append("Was.deviceInfo={");
//            sb.append("os:'");
//            sb.append(DeviceInfo.getOS());
//            sb.append("',type:'");
//            sb.append(DeviceInfo.getDevType());
//            sb.append("',pixel:'");
//            sb.append(DeviceInfo.getPixel());
//            sb.append("',osVersion:'");
//            sb.append(DeviceInfo.getAndroidOsVersion());
//            sb.append("',manufatory:'");
//            sb.append(DeviceInfo.getManufatory());
//            sb.append("',osModel:'");
//            sb.append(DeviceInfo.getOsModel());
//            sb.append("'};");
//            deviceJS = sb.toString();
//        }
//        return deviceJS;
//    }

    @Override
    public void onPageStarted(WebView webView, String s, Bitmap bitmap) {
        super.onPageStarted(webView, s, bitmap);
    }

    @Override
    public void onPageFinished(WebView webView, String url) {
        if (!isAlreadNotifyLoadObserver) {
            //easymicore.js 已经转到web上动态加载，本地不再处理该js文件
            mWebview.execJS(Was.getInstance().getCoreJSString());
            // 向应用发出Was注入完毕的事件
            String eventJS = "(function(){var event = document.createEvent('Events');event.initEvent('JUZIXJSBridgeReady',true,false);document.dispatchEvent(event);})();";
//            String eventJS = "(function(){var event = document.createEvent('Events');event.initEvent('YMTJSBridgeReady',true,false);document.dispatchEvent(event);})();";
            mWebview.execJS(eventJS);

            // 注入设备信息和用户信息
            WasWebviewContext wasWebviewContext = mWebview.getContext();
            Map<String, String> statusMap = wasWebviewContext.getPreParams();

            //向应用发出应用启动完毕的事件
            Map<String, String> eventData = (Map<String, String>) this.mWebview.getContext().removeParam("__onCreateEventData");
            if (eventData != null) {
                mWebview.fireEvent("onCreate", eventData);
                mWebview.fireEvent("onResume", eventData);
            }

            String userInfoJS = createUserInfo(statusMap);
            Log.d(TAG, "userInfoJS= " + userInfoJS);
            mWebview.execJS(userInfoJS + "Was.netState=" + WasEngine.getInstance().getNetState() + ";");


            isAlreadNotifyLoadObserver=true;
            Log.d(TAG, "onPageFinished(" + url + ")");
            mWebview.notifyLoadPageFinish();
        }
        super.onPageFinished(webView, url);

    }

    //    @Override
//    public void onPageStarted(WebView view, String url, Bitmap favicon) {
//    	Log.d(TAG, "onPageStarted(" + url + ")");
//    }
//
//     @Override
//    public void onPageFinished(WebView view, String url)
//    {
//        if (!isAlreadNotifyLoadObserver) {
//            //easymicore.js 已经转到web上动态加载，本地不再处理该js文件
//            mWebview.execJS(Was.getInstance().getCoreJSString());
//            // 向应用发出Was注入完毕的事件
//            String eventJS = "(function(){var event = document.createEvent('Events');event.initEvent('YMTJSBridgeReady',true,false);document.dispatchEvent(event);})();";
//            mWebview.execJS(eventJS);
//
//            // 注入设备信息和用户信息
//            WasWebviewContext wasWebviewContext = mWebview.getContext();
//            Map<String, String> statusMap = wasWebviewContext.getPreParams();
//
//            //向应用发出应用启动完毕的事件
//            Map<String, String> eventData = (Map<String, String>) this.mWebview.getContext().removeParam("__onCreateEventData");
//            if (eventData != null) {
//                mWebview.fireEvent("onCreate", eventData);
//                mWebview.fireEvent("onResume", eventData);
//            }
//
//            String userInfoJS = createUserInfo(statusMap);
//            Log.d(TAG, "userInfoJS= " + userInfoJS);
//            mWebview.execJS(userInfoJS + "Was.netState=" + WasEngine.getInstance().getNetState() + ";");
//
//
//            isAlreadNotifyLoadObserver=true;
//            Log.d(TAG, "onPageFinished(" + url + ")");
//            mWebview.notifyLoadPageFinish();
//        }
//    }

    private String createUserInfo(Map<String, String> map) {
        StringBuilder sb = new StringBuilder();
        Iterator<String> it = map.keySet().iterator();
        while (it.hasNext()) {
            String key = it.next();
            sb.append(key).append(":'").append(map.get(key)).append("'");
            if(it.hasNext()) {
                sb.append(",");
            }
        }
        return "Was.userInfo={" + sb.toString() + "};";
    }

    public boolean shouldOverrideUrlLoading(WebView view, String url)
    {
        Log.d(TAG, "shouldOverrideUrlLoading(" + url + ")");
        if (url.startsWith("sms:"))
        {
            // 发送短信
            String number = url.substring(4);
            Log.d(TAG, "phone number:" + number);
            HashMap<String, String> args = new HashMap<String, String>();
            args.put("__number", number);
            PluginManager.getInstance().exec("sms", "send", args, null, this.mWebview, null);
            return true;
        }
        else if (url.startsWith("tel:"))
        {
            // 拨打电话
            String number = url.substring(4);
            Log.d(TAG, "phone number:" + number);
            HashMap<String, String> args = new HashMap<String, String>();
            args.put("__number", number);
            PluginManager.getInstance().exec("phone", "call", args, null, this.mWebview, null);
            return true;
        }
        else if (url.startsWith("mailto:"))
        {
            Intent intent=new Intent(Intent.ACTION_SEND); 
            intent.setType("message/rfc822"); 
            
            String p = url.substring(7);
            int idx = p.indexOf('?');
            String addresses = p;
            String query = null;
            if(idx>=0)
            {
                addresses = p.substring(0,idx);
                query = p.substring(idx+1);
            }
            Log.d(TAG, "addresses:"+addresses);
            Log.d(TAG, "query:"+query);
            //收件人
            String[] emailAddList = addresses.split(";");
            if(emailAddList!=null && emailAddList.length>0)
            {
                intent.putExtra(Intent.EXTRA_EMAIL, emailAddList); 
            }
            if(query!=null)
            {
                String[] params = query.split("&");
                for(String param:params)
                {
                    int ii = param.indexOf('=');
                    if(ii>0)
                    {
                        String name = param.substring(0,ii);
                        String value = param.substring(ii+1);
                        try
                        {
                            value = java.net.URLDecoder.decode(value,"utf-8");
                        }
                        catch (UnsupportedEncodingException e)
                        {
                            Log.e(TAG, "",e);
                            continue;
                        }
                        //Log.debug(TAG, "parse param:["+name+","+value+"]");
                        if(name.equalsIgnoreCase("SUBJECT"))
                        {
                            //标题
                            //Log.debug(TAG, "SUBJECT:"+value);
                            intent.putExtra(Intent.EXTRA_SUBJECT, value); 
                        }
                        else if(name.equalsIgnoreCase("BODY"))
                        {
                            //内容
                            //Log.debug(TAG, "BODY:"+value);
                            intent.putExtra(Intent.EXTRA_TEXT, value); 
                        }
                        else if(name.equalsIgnoreCase("CC"))
                        {
                            //抄送
                            Log.d(TAG, "CC:"+value);
                            String[] cc = value.split(";");
                            if(cc!=null && cc.length>0)
                            {
                                intent.putExtra(Intent.EXTRA_CC, cc); 
                            }
                        }
                        else if(name.equalsIgnoreCase("BCC"))
                        {
                            //密送
                            //Log.debug(TAG, "BCC:"+value);
                        }
                    }
                }
            }
            WasEngine.getInstance().getActivityContext().startActivity(Intent.createChooser(intent, "发送邮件"));
            
            return true;
        }
        return false;
    }
}
