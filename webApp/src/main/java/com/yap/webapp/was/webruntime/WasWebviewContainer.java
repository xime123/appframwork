package com.yap.webapp.was.webruntime;


import android.webkit.WebView;

public interface WasWebviewContainer
{
    void putWebview(WebView webview);
    void removeWebview(WebView webview);
    void showError(String message);
    void onClose();
    void onHome();
    void onLoading();
    void onLoadfinish();
    void setScreenOrientation(int orientation);
}
