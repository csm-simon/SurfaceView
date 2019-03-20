package com.example.surfaceview.CommonUtil;

import android.content.Context;
import android.util.DisplayMetrics;

import static com.example.surfaceview.MyApplication.getApplication;

public class DeviceUtil {
    public static int getScreenHeight() {
        return getScreenHeight(getApplication());
    }

    public static int getScreenHeight(Context context) {
        DisplayMetrics dm = context.getApplicationContext().getResources().getDisplayMetrics();
        return dm.widthPixels > dm.heightPixels ? dm.widthPixels : dm.heightPixels;
    }

    public static int getScreenWidth() {
        return getScreenWidth(getApplication());
    }

    public static int getScreenWidth(Context context) {
        DisplayMetrics dm = context.getApplicationContext().getResources().getDisplayMetrics();
        return dm.widthPixels > dm.heightPixels ? dm.heightPixels : dm.widthPixels;
    }
}
