package com.erick.cellphoneguard.Utils;

import android.support.annotation.IntegerRes;
import android.text.TextUtils;
import android.util.Log;

/**
 * Created by Administrator on 2018/1/7 0007.
 */

public class Util {
    private static final String TAG = "Util";

    public static final boolean D = true;

    public static final int BASE = 10000;
    public static final int UPDATE_TIMER = BASE + 1;
    public static final int UPDATE_TIMER_PROGRESS = BASE + 2;

    public static String getDiffTime(String startTime,String endTime){
        if (TextUtils.isEmpty(startTime) || TextUtils.isEmpty(endTime)) return "00:00";
        String[] times;
        int hour1,hour2,min1,min2;

        times = startTime.split(":");
        hour1 = Integer.parseInt(times[0]);
        min1 = Integer.parseInt(times[1]);

        times = endTime.split(":");
        hour2 = Integer.parseInt(times[0]);
        min2 = Integer.parseInt(times[1]);

        int difHour = ((hour2 - hour1) + 24) % 24;
        int difMin = ((min2 - min1) + 60) % 60;

        String result = "";
        if (difMin < 10){
            result =difHour + ":0" + difMin;
        } else {
           result =difHour + ":" + difMin;
        }

        Log.d(TAG, "getDiffTime: result = " + result);

        return result;
    }

    public static long stringToSecond(String time){
        if (TextUtils.isEmpty(time)) return 0;

        String[] times;
        int hour,min;

        times = time.split(":");
        hour = Integer.parseInt(times[0]);
        min = Integer.parseInt(times[1]);

        return hour * 60 * 60 + min * 60;
    }
}
