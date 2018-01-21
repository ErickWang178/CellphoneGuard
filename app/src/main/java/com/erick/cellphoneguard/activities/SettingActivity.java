package com.erick.cellphoneguard.activities;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.erick.cellphoneguard.MyApplication;
import com.erick.cellphoneguard.R;
import com.erick.cellphoneguard.fragments.HelpFragment;
import com.erick.cellphoneguard.fragments.SettingFragment;
import com.erick.cellphoneguard.interfaces.IServiceCallback;
import com.erick.cellphoneguard.services.SensorService;

import java.util.ArrayList;

/**
 * Created by Administrator on 2018/1/7 0007.
 */

public class SettingActivity extends AppCompatActivity implements View.OnClickListener{
    private static String TAG = SettingActivity.class.getSimpleName();

    private TextView tvHelp;
    private TextView tvTitle;
    private ImageView mBack;
    private SensorService mService;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        initView();
        showSettingFragment();
    }

    private void initView() {
        tvHelp = (TextView) findViewById(R.id.tv_help);
        tvHelp.setOnClickListener(this);

        tvTitle = (TextView) findViewById(R.id.main_title);
        mBack = (ImageView) findViewById(R.id.im_back);
        mBack.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_help:
                showHelpFragment();
                break;
            case R.id.im_back:
                showSettingFragment();
                break;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent();
        intent.setClass(MyApplication.getAppContext(),SensorService.class);
        bindService(intent,mConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        removeCallback();

        unbindService(mConnection);
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mService = ((SensorService.SensorBinder)service).getService();
            Log.d(TAG, "onServiceConnected: mService = " + mService);

            for (IServiceCallback callback : mCallbacks)
                callback.onConnect(mService);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG, "onServiceDisconnected: name=" + name);
        }
    };

    private void showSettingFragment(){
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.setting_panel,new SettingFragment());
        fragmentTransaction.setCustomAnimations(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
        fragmentTransaction.commit();

        tvHelp.setVisibility(View.VISIBLE);
        tvTitle.setText("Setting");
        mBack.setVisibility(View.GONE);
    }

    private void showHelpFragment(){
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.setting_panel,new HelpFragment());
        fragmentTransaction.setCustomAnimations(R.anim.slide_in_right,R.anim.slide_out_right);
        fragmentTransaction.commit();

        tvHelp.setVisibility(View.GONE);
        tvTitle.setText("Help");
        mBack.setVisibility(View.VISIBLE);
    }

    @Override
    public void onBackPressed() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.setting_panel);
        if (fragment instanceof HelpFragment){
            showSettingFragment();
        } else {
            super.onBackPressed();
        }
    }

    private static ArrayList<IServiceCallback> mCallbacks = new ArrayList<>();
    public static void addCallback(IServiceCallback callback) {
        if (!mCallbacks.contains(callback)){
            mCallbacks.add(callback);
        }
    }

    public static void removeCallback(){
        mCallbacks.clear();
    }
}
