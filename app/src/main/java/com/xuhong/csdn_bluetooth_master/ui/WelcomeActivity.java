package com.xuhong.csdn_bluetooth_master.ui;

import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.xuhong.csdn_bluetooth_master.R;

public class WelcomeActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        mHandler.sendEmptyMessageDelayed(105,3000);
        checkPermission();
    }

    private void checkPermission() {
        //是否大于6.0版本
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {



        }
    }


    private Handler mHandler =new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if (msg.what==105){
                startActivity(new Intent(WelcomeActivity.this,DevicesListActivity.class));
                finish();
            }
        }
    };
}
