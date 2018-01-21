package com.erick.cellphoneguard;

import android.app.Application;
import android.content.Context;

/**
 * Created by Administrator on 2018/1/7 0007.
 */

public class MyApplication extends Application {
    private static String TAG = MyApplication.class.getSimpleName();

    private static Context mContext;

    public static Context getAppContext(){
        return mContext;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mContext = this;
    }
}
