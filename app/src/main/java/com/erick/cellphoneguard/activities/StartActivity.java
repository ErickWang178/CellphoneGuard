package com.erick.cellphoneguard.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.erick.cellphoneguard.R;

public class StartActivity extends AppCompatActivity implements View.OnClickListener{
    Button btnLogin;
    private Button btnCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnLogin = (Button) findViewById(R.id.btn_login);
        btnCancel = (Button) findViewById(R.id.btn_cancel);

        btnLogin.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_cancel:
                finish();
                break;
            case R.id.btn_login:
                login();
                break;
        }
    }

    private void login() {
        // get username and password to validate

        Intent intent = new Intent();
        intent.setClass(this,SettingActivity.class);
        startActivity(intent);
        finish();
    }
}
