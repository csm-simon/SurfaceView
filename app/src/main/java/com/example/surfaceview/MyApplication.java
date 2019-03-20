package com.example.surfaceview;

import android.app.Application;

public class MyApplication extends Application {
    private static Application mBaseApplication = null;

    public MyApplication() {
    }

    public static Application getApplication() {
        return mBaseApplication;
    }

    public void onCreate() {
        super.onCreate();
        mBaseApplication = this;
    }
}
