package com.xuhong.csdn_bluetooth_master;

import android.app.AlertDialog;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.xuhong.csdn_bluetooth_master.ui.BaseActivity;

import java.io.IOException;
import java.util.UUID;

import cn.pedant.SweetAlert.SweetAlertDialog;


public class MainActivity extends BaseActivity {


    //要传出下个界面的蓝牙对象
    private BluetoothDevice mBluetoothDevice;
    //创建socket
    private BluetoothSocket mSocket = null;
    // uuid
    private static final String SPP_UUID = "00001101-0000-1000-8000-00805F9B34FB";

    //ui
    private SweetAlertDialog progerssAlertDialog;
    private Context mContext;

    private static final int HANDLER_SUCCEED = 201;
    private static final int HANDLER_FAIL = 202;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case HANDLER_SUCCEED:
                    progerssAlertDialog.dismiss();
                    Toast.makeText(mContext, "连接成功", Toast.LENGTH_SHORT).show();
                    break;
                case HANDLER_FAIL:
                    progerssAlertDialog.dismiss();
                    showFailAlertDialog("连接失败");
                    break;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;
        initView();
        initData();
    }

    private void initView() {
        progerssAlertDialog = new SweetAlertDialog(mContext, SweetAlertDialog.PROGRESS_TYPE);
        progerssAlertDialog.getProgressHelper().setBarColor(getResources().getColor(R.color.yellow));
        progerssAlertDialog.setTitleText("正在连接...");
        progerssAlertDialog.show();
        //显示返回按钮
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //取消阴影
        getSupportActionBar().setElevation(0);

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void initData() {
        registerReceiver();
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            mBluetoothDevice = bundle.getParcelable("device");
            if (mBluetoothDevice != null) {
                Log.e("BaseActivity", "收到的蓝牙名字：" + mBluetoothDevice.getName());
                setTitle(mBluetoothDevice.getName());
                new mThread(mBluetoothDevice).start();
            }
        }
    }

    //内部类子线程
    private class mThread extends Thread {

        private BluetoothDevice bluetoothDevice;

        mThread(BluetoothDevice bluetoothDevice) {
            this.bluetoothDevice = bluetoothDevice;
        }

        @Override
        public void run() {
            UUID uuid = UUID.fromString(SPP_UUID);
            try {
                mSocket = bluetoothDevice.createRfcommSocketToServiceRecord(uuid);
                mSocket.connect();
                mHandler.sendEmptyMessage(HANDLER_SUCCEED);
            } catch (IOException e) {
                mHandler.sendEmptyMessage(HANDLER_FAIL);
            }
        }
    }

    @Override
    protected void succeedBindBTDevices() {
        mHandler.sendEmptyMessage(HANDLER_SUCCEED);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (mSocket != null) {
                        mSocket.close();
                        mSocket = null;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    private void showFailAlertDialog(String message) {
        SweetAlertDialog failAlertDialog = new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE);
        failAlertDialog.setCustomImage(R.mipmap.ic_launcher);
        failAlertDialog.setTitleText(message);
        failAlertDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                sweetAlertDialog.dismiss();
                finish();
            }
        });
        failAlertDialog.show();
    }

    private void showDevicesDetailInf() {
        SweetAlertDialog mAlertDialog = new SweetAlertDialog(this, SweetAlertDialog.NORMAL_TYPE);
        mAlertDialog.setTitleText("设备信息");
        mAlertDialog.setContentText("设备地址：" + mBluetoothDevice.getAddress() +"\n设备名字：" + mBluetoothDevice.getName());
        mAlertDialog.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //关闭当前Activity
        switch (item.getItemId()) {
            case R.id.menu_detial:
                showDevicesDetailInf();
                break;
            case R.id.menu_rename:
                break;
            case android.R.id.home:
                new SweetAlertDialog(this, SweetAlertDialog.NORMAL_TYPE)
                        .setTitleText("提示")
                        .setContentText("确定要退出控制该设备吗？")
                        .setCancelText("取消")
                        .setConfirmText("确定")
                        .showCancelButton(true)
                        .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                sDialog.cancel();
                            }
                        })
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                finish();
                            }
                        })
                        .show();
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_devices, menu);
        return true;
    }


}
