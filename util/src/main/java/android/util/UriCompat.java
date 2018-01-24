package android.util;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.FileProvider;

import java.io.File;



public class UriCompat {

    public static Uri fromFile(Context context, File file) {
        //判断是否是AndroidN以及更高的版本
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.N ?
                FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".fileProvider", file) :
                Uri.fromFile(file);
    }
}