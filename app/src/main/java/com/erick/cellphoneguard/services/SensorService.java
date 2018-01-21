package com.erick.cellphoneguard.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.util.Log;

import com.erick.cellphoneguard.interfaces.IUpdateTimer;

import java.io.UTFDataFormatException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static com.erick.cellphoneguard.Utils.Util.UPDATE_TIMER;

public class SensorService extends Service{
    private static final String TAG = SensorService.class.getSimpleName();

    // 两次检测的时间间隔
    private static final int UPDATE_INTERVAL_TIME = 100;

    // 速度变化的阈值
    private static final int SPEED_THRESHOLD = 1000;

    private SensorBinder mBinder = new SensorBinder();
    private SensorManager mSensorManager;
    private Sensor mSensorAcceleration;
    private Sensor mSensorGyroscope;
    private Vibrator mVibrator;
    private String mSetTime;
    private IUpdateTimer mUpdateTimer;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate: =================");
        //获取传感器管理器
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
    }

    private boolean checkSensorType(int type){
        if (mSensorManager == null) return false;

        //获取所有传感器
        List<Sensor> sensorList = mSensorManager.getSensorList(Sensor.TYPE_ALL);
        for (Sensor ss : sensorList){
            if (ss.getType() == type){
                return true;
            }
        }

        return false;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind: -------------");
        
        return mBinder;
    }

    public void startTimer(String time) {
        mSetTime = time;

        mHandler.sendEmptyMessage(UPDATE_TIMER);
    }

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case UPDATE_TIMER:
                    SimpleDateFormat format = new SimpleDateFormat("HH:mm");
                    Date date = new Date(System.currentTimeMillis());
                    String str = format.format(date);
                    int step = 1;
                    if (str.equals(mSetTime)){
                        mHandler.removeMessages(UPDATE_TIMER);
                        openMusic();
                    } else {
                        mHandler.sendEmptyMessageDelayed(UPDATE_TIMER,step * 1000);
                    }

                    if (mUpdateTimer != null){
                        mUpdateTimer.updateTimer(step);
                    }
                    break;
            }
        }
    };

    private void openMusic() {
        Log.d(TAG, "openMusic: 播放设置的音乐");
    }

    public void setUpdateTimer(IUpdateTimer updateTimer) {
        mUpdateTimer = updateTimer;
    }

    public class SensorBinder extends Binder {
        public SensorService getService(){
            Log.d(TAG, "getService: 3333333333333333333333333");
            return SensorService.this;
        }
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(TAG, "onUnbind: 22222222222222222222");
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        closeSensorAcceleration();
        mSensorManager = null;
    }

    //---------------------- 加速度传感器 ---------------------------------------//
    long lastUpdateTimeAcc;
    float lastXAcc;
    float lastYAcc;
    float lastZAcc;

    private SensorEventListener mSensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            long time = System.currentTimeMillis();
            long timeDiff = time - lastUpdateTimeAcc;

            if (timeDiff < UPDATE_INTERVAL_TIME){
                return;
            }
            lastUpdateTimeAcc = time;

            float[] values = event.values;
            float x,y,z;
            x = values[0];
            y = values[1];
            z = values[2];

            // 获取x,y,z 方向加速度变化
            float deltX = lastXAcc - x;
            float deltY = lastYAcc - y;
            float deltZ = lastZAcc - z;

            lastXAcc = x;
            lastYAcc = y;
            lastZAcc = z;

            double speed = Math.sqrt(deltX * deltX + deltY * deltY + deltZ * deltZ)
                    / timeDiff * 10000;

            Log.d(TAG, "onSensorChanged: speed = " + speed);

            if (speed > SPEED_THRESHOLD){
                Log.d(TAG, "onSensorChanged: 66666666666666666");
                mVibrator.vibrate(300);
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    };

    public void openSensorAcceleration(){
        if (mSensorManager == null) return;

        Log.d(TAG, "openSensorAcceleration: -----");

        if (!checkSensorType(Sensor.TYPE_ACCELEROMETER)) return;

        mSensorAcceleration = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if (mSensorAcceleration != null){
            mSensorManager.registerListener(mSensorEventListener,
                    mSensorAcceleration,
                    SensorManager.SENSOR_DELAY_NORMAL);

            Log.d(TAG, "openSensorAcceleration: sensor = " + mSensorAcceleration.toString());
        }
    }

    public void closeSensorAcceleration(){
        lastUpdateTimeAcc = 0;
        lastXAcc = 0;
        lastYAcc = 0;
        lastZAcc = 0;

        if (mSensorManager != null){
            Log.d(TAG, "closeSensorAcceleration: ");
            mSensorManager.unregisterListener(mSensorEventListener);
        }
    }

    //------------------------ 陀螺仪 --------------------------//
    long lastUpdateTimeGyroscope;
    float lastXGyroscope;
    float lastYGyroscope;
    float lastZGyroscope;

    private SensorEventListener mSensorEventListenerGyroscope = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            long time = System.currentTimeMillis();
            long timeDiff = time - lastUpdateTimeGyroscope;

            if (timeDiff < UPDATE_INTERVAL_TIME) return;

            lastUpdateTimeGyroscope = time;

            float x,y,z;
            float[] values = event.values;

            x = values[0];
            y = values[1];
            z = values[2];

            Log.d(TAG, "gyroscopeChange: x= " + x + ",y=" + y + ",z=" + z);

            lastXGyroscope = x;
            lastYGyroscope = y;
            lastZGyroscope = z;
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    };

    public void openSensorGyroscope(){
        if (!checkSensorType(Sensor.TYPE_GYROSCOPE)) return;

        mSensorGyroscope = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        if (mSensorGyroscope != null){
            mSensorManager.registerListener(mSensorEventListenerGyroscope,mSensorGyroscope,
                    SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    public void closeSensorGyroscope(){
        if (mSensorManager != null){
            mSensorManager.unregisterListener(mSensorEventListenerGyroscope);
        }
    }
}
