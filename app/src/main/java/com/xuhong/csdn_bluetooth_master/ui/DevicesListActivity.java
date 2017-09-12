package com.xuhong.csdn_bluetooth_master.ui;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.BounceInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.xuhong.csdn_bluetooth_master.MainActivity;
import com.xuhong.csdn_bluetooth_master.R;
import com.xuhong.csdn_bluetooth_master.view.RippleView;
import com.xuhong.csdn_bluetooth_master.adapter.DeviceListAdapter;

import java.util.ArrayList;
import java.util.List;


public class DevicesListActivity extends BaseActivity implements View.OnClickListener {

    //ui
    private TextView tvInf, tvCancle;
    private ImageView imageView, ivRefresh;
    private LinearLayout iVNull;
    private ListView listview;

    private DeviceListAdapter mLeDeviceListAdapter;
    private List<BluetoothDevice> mBluetoothDeviceList = new ArrayList<>();
    // 蓝牙适配器
    private BluetoothAdapter mBluetoothAdapter;
    //要传出下个界面的蓝牙对象
    private BluetoothDevice tempBluetoothDevice;

    private static final int CODE_REQUEST_OPENBT = 101;
    private RippleView radarView;
    private RotateAnimation rotateAnimation;

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
        } else {
            mBluetoothAdapter.startDiscovery();
            registerReceiver();
        }


    }

    private void initView() {
        rotateAnimation = new RotateAnimation(0, 365, RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        rotateAnimation.setRepeatCount(ObjectAnimator.INFINITE);
        rotateAnimation.setRepeatMode(ObjectAnimator.INFINITE);
        rotateAnimation.setDuration(2000);
        rotateAnimation.setFillAfter(false);

        tvInf = (TextView) findViewById(R.id.tvInf);
        tvCancle = (TextView) findViewById(R.id.tvCancle);
        tvCancle.setOnClickListener(this);
        radarView = (RippleView) findViewById(R.id.content);
        radarView.setAnimationProgressListener(new RippleView.AnimationListener() {
            @Override
            public void startAnimation() {

            }

            @Override
            public void EndAnimation() {

                tvCancle.setVisibility(View.INVISIBLE);
                radarView.setVisibility(View.INVISIBLE);
                imageView.setVisibility(View.INVISIBLE);
                listview.setVisibility(View.VISIBLE);
                ivRefresh.clearAnimation();
            }
        });

        imageView = (ImageView) findViewById(R.id.imageView);
        ivRefresh = (ImageView) findViewById(R.id.ivRefresh);
        ivRefresh.setOnClickListener(this);
        iVNull = (LinearLayout) findViewById(R.id.iVNull);
        iVNull.setOnClickListener(this);
        listview = (ListView) findViewById(R.id.listview);
        mLeDeviceListAdapter = new DeviceListAdapter(this, mBluetoothDeviceList);
        listview.setAdapter(mLeDeviceListAdapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                tempBluetoothDevice = mBluetoothDeviceList.get(position);
                Intent intent = new Intent();
                intent.setClass(DevicesListActivity.this, MainActivity.class);
                Bundle bundle = new Bundle();
                bundle.putParcelable("device", tempBluetoothDevice);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case CODE_REQUEST_OPENBT:
                if (mBluetoothAdapter.isEnabled()) {
                    mBluetoothAdapter.startDiscovery();
                    registerReceiver();
                } else {
                    Toast.makeText(DevicesListActivity.this, "您拒绝了开启蓝牙！点击重试吧！", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            //取消扫描
            case R.id.tvCancle:
                mBluetoothAdapter.cancelDiscovery();
                break;
            case R.id.iVNull:
                intData();
                break;
            //刷新按钮
            case R.id.ivRefresh:
                mBluetoothDeviceList.clear();
                listview.setVisibility(View.INVISIBLE);
                ivRefresh.startAnimation(rotateAnimation);
                radarView.startRippleAnimation();
                intData();
                break;
        }
    }

    @Override
    protected void getBTDevices(BluetoothDevice device) {
        //过滤设备
        if (device != null) {
            if (!mBluetoothDeviceList.contains(device)) {
                if (device.getName() != null) {
                    if (device.getName().contains("XuHong")) {
                    mBluetoothDeviceList.add(device);
                    mLeDeviceListAdapter.notifyDataSetChanged();
                    }
                }
            }
            if (mBluetoothDeviceList.size() > 0) {
                iVNull.setVisibility(View.INVISIBLE);
                tvInf.setText("恭喜，已找到了" + mBluetoothDeviceList.size() + "个蓝牙灯了！");
            }
        }
    }

    @Override
    protected void startScanBTDevices() {
        imageView.setVisibility(View.VISIBLE);
        radarView.setVisibility(View.VISIBLE);
        iVNull.setVisibility(View.INVISIBLE);
        tvCancle.setVisibility(View.VISIBLE);
        radarView.startRippleAnimation();
        tvInf.setText("正在寻找蓝牙灯设备，耐心等待...");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver();
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver();
        mLeDeviceListAdapter.notifyDataSetChanged();
    }

    @Override
    protected void disableBTDevices() {
        if (!mBluetoothAdapter.isEnabled()) {
            mBluetoothDeviceList.clear();
            listview.setVisibility(View.INVISIBLE);
            iVNull.setVisibility(View.VISIBLE);
            tvInf.setText("蓝牙功能未开启！");
        }
    }

    @Override
    protected void stopScanBTDevices() {
        Toast.makeText(DevicesListActivity.this, "取消扫描成功！", Toast.LENGTH_LONG).show();
        tvCancle.setVisibility(View.INVISIBLE);
        radarView.setVisibility(View.INVISIBLE);
        imageView.setVisibility(View.INVISIBLE);
        listview.setVisibility(View.VISIBLE);
        radarView.stopRippleAnimation();
        if (mBluetoothDeviceList.size()==0){
            iVNull.setVisibility(View.VISIBLE);
            ivRefresh.setVisibility(View.INVISIBLE);
            tvInf.setText("啥都没搜到！");
        }else {
            ivRefresh.setVisibility(View.VISIBLE);
        }
    }
}

