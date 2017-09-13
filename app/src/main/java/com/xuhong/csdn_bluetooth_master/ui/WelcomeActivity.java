package com.xuhong.csdn_bluetooth_master.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.xuhong.csdn_bluetooth_master.R;

import java.util.ArrayList;
import java.util.List;

public class WelcomeActivity extends AppCompatActivity {


    private static final int REQUEST_CODE = 205;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        checkPermission();
    }

    private void checkPermission() {
        //是否大于6.0版本
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //检查是否已经授权
            int Code_ACCESS_FINE_LOCATION = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
            int Code_ACCESS_COARSE_LOCATION = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
            //授权结果判断
            if (Code_ACCESS_FINE_LOCATION != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
            }else if(Code_ACCESS_COARSE_LOCATION != PackageManager.PERMISSION_GRANTED){
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_CODE);
            } else {
                mHandler.sendEmptyMessageDelayed(105, 3000);
            }
        } else {
            mHandler.sendEmptyMessageDelayed(105, 3000);
        }
    }


    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 105) {
                startActivity(new Intent(WelcomeActivity.this, DevicesListActivity.class));
                finish();
            }
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE) {
            if (grantResults.length > 0) {
                List<String> deniedPermission = new ArrayList<>();
                for (int i = 0; i < grantResults.length; i++) {
                    int grantResult = grantResults[i];
                    String permission = permissions[i];
                    if (grantResult != PackageManager.PERMISSION_GRANTED) {
                        deniedPermission.add(permission);
                    }
                }
                if (deniedPermission.isEmpty()) {
                    mHandler.sendEmptyMessage(105);
                } else {
                    Toast.makeText(this,"您拒绝了部分权限！可以在设置—应用详情授权，否则无法搜索出蓝牙设备哦。",Toast.LENGTH_LONG).show();
                    mHandler.sendEmptyMessageDelayed(105, 3000);
                }

            }
        }
    }
}
