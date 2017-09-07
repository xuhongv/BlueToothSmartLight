package com.xuhong.csdn_bluetooth_master.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.xuhong.csdn_bluetooth_master.MainActivity;
import com.xuhong.csdn_bluetooth_master.R;
import com.xuhong.csdn_bluetooth_master.RippleView;
import com.xuhong.csdn_bluetooth_master.adapter.DeviceListAdapter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class DevicesListActivity extends BaseActivity implements View.OnClickListener {

    //ui
    private TextView tvInf, tvCancle;
    private ImageView imageView;
    private ListView listview;
    private ProgressDialog progressDialog;

    private DeviceListAdapter mLeDeviceListAdapter;
    private List<BluetoothDevice> mBluetoothDeviceList = new ArrayList<>();

    // uuid
    private static final String SPP_UUID = "00001101-0000-1000-8000-00805F9B34FB";

    // 蓝牙适配器
    private BluetoothAdapter mBluetoothAdapter;
    //创建socket
    private BluetoothSocket mSocket = null;
    //要传出下个界面的蓝牙对象
    private BluetoothDevice tempBluetoothDevice;

    private static final int CODE_REQUEST_OPENBT = 101;
    private static final int HANDLER_FAIL = 102;
    private static final int HANDLER_SUCCEED = 103;

    private RippleView radarView;


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

        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("您关闭了蓝牙功能！");

    }

    private void initView() {

        tvInf = (TextView) findViewById(R.id.tvInf);
        tvCancle = (TextView) findViewById(R.id.tvCancle);
        tvCancle.setOnClickListener(this);
        radarView = (RippleView) findViewById(R.id.content);
        imageView = (ImageView) findViewById(R.id.imageView);
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
        //如果用户拒绝了蓝牙开启，则退出程序
        if (resultCode == Activity.RESULT_CANCELED) {
            finish();
        }
        switch (requestCode) {
            case CODE_REQUEST_OPENBT:
                if (mBluetoothAdapter.isEnabled()) {
                    mBluetoothAdapter.startDiscovery();
                    registerReceiver();
                }
                break;
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            //取消扫描
            case R.id.tvCancle:
                tvCancle.setVisibility(View.INVISIBLE);
                radarView.setVisibility(View.INVISIBLE);
                imageView.setVisibility(View.INVISIBLE);
                listview.setVisibility(View.VISIBLE);
                radarView.stopRippleAnimation();
                mBluetoothAdapter.cancelDiscovery();
                Toast.makeText(DevicesListActivity.this, "取消扫描成功！", Toast.LENGTH_LONG).show();
                break;
        }
    }

    @Override
    protected void getBTDevices(BluetoothDevice device) {
        //过滤设备
        if (device != null) {
            if (!mBluetoothDeviceList.contains(device)) {
                if (device.getName() != null) {
                    //if (device.getName().contains("Light")) {
                        mBluetoothDeviceList.add(device);
                        mLeDeviceListAdapter.notifyDataSetChanged();
                        tvInf.setText("恭喜，已找到了" + mBluetoothDeviceList.size() + "个蓝牙灯了！");
                   // }
                }
            }
        }
    }

    @Override
    protected void startScanBTDevices() {
        imageView.setVisibility(View.VISIBLE);
        radarView.startRippleAnimation();
        tvInf.setText("正在寻找蓝牙灯设备，耐心等待...");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver();
    }


}

