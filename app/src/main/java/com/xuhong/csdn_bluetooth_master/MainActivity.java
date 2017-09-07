package com.xuhong.csdn_bluetooth_master;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.xuhong.csdn_bluetooth_master.ui.BaseActivity;

import java.io.IOException;
import java.util.UUID;


public class MainActivity extends BaseActivity {


    //要传出下个界面的蓝牙对象
    private BluetoothDevice mBluetoothDevice;
    //创建socket
    private BluetoothSocket mSocket = null;
    // uuid
    private static final String SPP_UUID = "00001101-0000-1000-8000-00805F9B34FB";

    //ui
    private Dialog progressDialog;
    private Context mContext;

    private static final int HANDLER_SUCCEED= 201;
    private static final int HANDLER_FAIL= 202;

    private Handler mHandler =new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case HANDLER_SUCCEED:
                    progressDialog.dismiss();
                    break;
                case HANDLER_FAIL:
                    progressDialog.show();
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
        progressDialog = new Dialog(this);
        progressDialog.setTitle("正在连接...");
        progressDialog.show();
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
            Log.e("BaseActivity", "准备连接");
            try {
                mSocket = bluetoothDevice.createRfcommSocketToServiceRecord(uuid);
                mSocket.connect();
                mHandler.sendEmptyMessage(HANDLER_SUCCEED);
                Log.e("BaseActivity", "连接succeed:");
            } catch (IOException e) {
                mHandler.sendEmptyMessage(HANDLER_FAIL);
                Log.e("BaseActivity", "连接失败:" + e);
            }
        }
    }




    @Override
    protected void succeedBindBTDevices() {
        Log.e("BaseActivity", "succeedBindBTDevices");
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
                        mSocket=null;
                        Log.e("BaseActivity", "关闭成功");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e("BaseActivity", "关闭失败");
                }
            }
        }).start();

    }
}
