package com.erick.cellphoneguard.fragments;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.erick.cellphoneguard.R;
import com.erick.cellphoneguard.Utils.Util;
import com.erick.cellphoneguard.activities.SettingActivity;
import com.erick.cellphoneguard.interfaces.IServiceCallback;
import com.erick.cellphoneguard.interfaces.IUpdateTimer;
import com.erick.cellphoneguard.services.SensorService;

import java.text.SimpleDateFormat;
import java.util.Date;

import static com.erick.cellphoneguard.Utils.Util.UPDATE_TIMER_PROGRESS;

/**
 * Created by Administrator on 2018/1/7 0007.
 */

public class SettingFragment extends Fragment implements View.OnClickListener,
        CompoundButton.OnCheckedChangeListener{
    private static String TAG = SettingFragment.class.getSimpleName();
    private Context mContext;
    private TextView tvSensitivity;
    private SeekBar seekbarSensitivity;
    private TextView tvTimerLabel;
    private TextView tvRemainTime;
    private ProgressBar mRemainTimeBar;
    private TextView tvRingtone;
    private SeekBar seekbarVolume;
    private TextView tvListen;
    private TextView tvContacts;
    private ImageView mSetTimer;
    private int mHour;
    private int mMinute;

    private SensorService mService;
    private SwitchCompat switchButtonAcceleration;
    private SwitchCompat switchButtonGyroscope;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mContext = getContext();

        View v = inflater.inflate(R.layout.fragment_setting, container, false);
        initView(v);

        SettingActivity.addCallback(mCallback);

        return v;
    }

    private IServiceCallback mCallback = new IServiceCallback() {
        @Override
        public void onConnect(SensorService service) {
            mService = service;
            mService.setUpdateTimer(mUpdateTimer);
            Log.d(TAG, "onConnect: mservice = "+ mService);
        }

        @Override
        public void onDisconnect() {
            mService = null;
        }
    };

    private void initView(View view) {
        tvSensitivity = (TextView) view.findViewById(R.id.tv_sensitivity);
        seekbarSensitivity = (SeekBar) view.findViewById(R.id.seekbar_sensitivity);
        tvTimerLabel = (TextView) view.findViewById(R.id.tv_timer_label);
        tvRemainTime = (TextView) view.findViewById(R.id.tv_remain_time);
        mRemainTimeBar = (ProgressBar) view.findViewById(R.id.seekbar_remain_time);
        tvRingtone = (TextView) view.findViewById(R.id.tv_ringtone);
        seekbarVolume = (SeekBar) view.findViewById(R.id.seekbar_volume);
        tvListen = (TextView) view.findViewById(R.id.tv_listen);
        tvContacts = (TextView) view.findViewById(R.id.tv_contacts);
        mSetTimer = (ImageView) view.findViewById(R.id.set_timer);
        mSetTimer.setOnClickListener(this);

        switchButtonAcceleration = (SwitchCompat) view.findViewById(R.id.switch_button_acceleration);
        switchButtonAcceleration.setOnCheckedChangeListener(this);

        switchButtonGyroscope = (SwitchCompat) view.findViewById(R.id.switch_button_gyroscope);
        switchButtonGyroscope.setOnCheckedChangeListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.set_timer:
                setTimer();
                break;
        }
    }

    private void setTimer() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_timer,null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(view);
        builder.setCancelable(true);

        final AlertDialog dialog = builder.create();
        dialog.show();

        final int[] hour = new int[1];
        final int[] min = new int[1];

        SimpleDateFormat format = new SimpleDateFormat("HH:mm");
        Date date = new Date(System.currentTimeMillis());
        final String currentTime = format.format(date);
        String[] times = currentTime.split(":");
        hour[0] = Integer.parseInt(times[0]);
        min[0] = Integer.parseInt(times[1]);

        TimePicker timePicker = (TimePicker) view.findViewById(R.id.timer_picker);
        Button btnOK = (Button) view.findViewById(R.id.btn_ok);
        Button btnCancel = (Button) view.findViewById(R.id.btn_cancel);
        timePicker.setIs24HourView(true);
        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                hour[0] = hourOfDay;
                min[0] = minute;
            }
        });

        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mHour = hour[0];
                mMinute = min[0];
                String time = "";

                if (mMinute < 10){
                    time = mHour + ":0" + mMinute;
                } else {
                    time = mHour + ":" + mMinute;
                }

                if (time.equals(currentTime)){
                    Toast.makeText(mContext,"不能设置为当前时间：" + time,Toast.LENGTH_SHORT).show();
                    return;
                }
                tvRemainTime.setText(time);
                String difTime =  Util.getDiffTime(currentTime,time);
                Toast.makeText(mContext,"剩余时间：" + difTime,Toast.LENGTH_SHORT).show();

                int maxTime = (int) Util.stringToSecond(difTime);

                mRemainTimeBar.setMax(maxTime);
                mRemainTimeBar.setProgress(maxTime);

                if (mService != null){
                    mService.startTimer(time);
                }

                dialog.cancel();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });

    }
    private IUpdateTimer mUpdateTimer = new IUpdateTimer() {
        @Override
        public void updateTimer(int step) {
            mHandler.sendMessage(mHandler.obtainMessage(UPDATE_TIMER_PROGRESS,step,-1));
        }
    };


    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch(msg.what){
                case UPDATE_TIMER_PROGRESS:
                    int value = mRemainTimeBar.getProgress() - msg.arg1;
                    mRemainTimeBar.setProgress(value);
                    break;
            }
        }
    };

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (mService == null) return;

        switch (buttonView.getId()){
            case R.id.switch_button_acceleration:
                if (isChecked){
                    mService.openSensorAcceleration();
                } else {
                    mService.closeSensorAcceleration();
                }
                break;
            case R.id.switch_button_gyroscope:
                if (isChecked){
                    mService.openSensorGyroscope();
                } else {
                    mService.closeSensorGyroscope();
                }
                break;
        }


    }


}
