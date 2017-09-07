package com.xuhong.csdn_bluetooth_master.ui;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Process;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.xuhong.csdn_bluetooth_master.R;
import com.xuhong.csdn_bluetooth_master.adapter.DeviceListAdapter;


public class DevicesListActivity extends BaseActivity implements View.OnClickListener {

    //ui
    private ProgressBar mProgressBar;
    private TextView tvScan;


    private DeviceListAdapter mLeDeviceListAdapter;

    // 蓝牙适配器
    private BluetoothAdapter mBluetoothAdapter;

    private static final int CODE_REQUEST_OPENBT = 101;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_devices_list);
        initView();
        intData();
    }

    private void intData() {

        // 初始化本地蓝牙设备
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "您的设备不支持蓝牙功能", Toast.LENGTH_SHORT).show();
            finish();
        }

        // 检测蓝牙设备是否开启，如果未开启，发起Intent并回调
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, CODE_REQUEST_OPENBT);
        }


    }

    private void initView() {

        final com.xuhong.csdn_bluetooth_master.RippleView radarView= (com.xuhong.csdn_bluetooth_master.RippleView) findViewById(R.id.content);

        //开始动画
        ImageView imageView=(ImageView)findViewById(R.id.centerImage);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //停止动画
                radarView.startRippleAnimation();
            }
        }); }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //如果用户拒绝了蓝牙开启，则退出程序
        if (resultCode == Activity.RESULT_CANCELED) {
            finish();
        }
        switch (requestCode) {
            case CODE_REQUEST_OPENBT:
                if (mBluetoothAdapter.isEnabled()) {
                    registerReceiver();
                }
                break;
        }
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {

        }
    }


}
