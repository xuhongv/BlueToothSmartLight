package com.xuhong.csdn_bluetooth_master.ui;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.util.Arrays;
import java.util.Objects;

/**
 * 项目名： CSDN_BlueTooth-master
 * 包名： com.xuhong.csdn_bluetooth_master.ui
 * 文件名字： BaseActivity
 * 创建时间：2017/9/6 23:59
 * 项目名： Xuhong
 * 描述： TODO
 */

public class BaseActivity extends AppCompatActivity {


    private static final String TAG = "BaseActivity";


    //注册广播接收器

    protected void registerReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);//蓝牙状态改变
        filter.addAction(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);//蓝牙扫描状态(SCAN_MODE)发生改变
        filter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED); //指明一个远程设备的连接状态的改变。比如，当一个设备已经被匹配。
        filter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);//指明一个与远程设备建立的低级别（ACL）连接。
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);//指明一个来自于远程设备的低级别（ACL）连接的断开
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED);//指明一个为远程设备提出的低级别（ACL）的断开连接请求，并即将断开连接。
        filter.addAction(BluetoothDevice.ACTION_FOUND);//发现远程设备
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);//本地蓝牙适配器已经开始对远程设备的搜寻过程。

        registerReceiver(mReceiver, filter);
    }

    //注销广播
    private void unregisterReceiver() {
        if (mReceiver != null) {
            unregisterReceiver(mReceiver);
        }
    }


    /**
     * 广播接收器接收返回的蓝牙信息
     */
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.d(TAG, "蓝牙广播:" + action);
            //未配对的设备
            if (Objects.equals(BluetoothDevice.ACTION_FOUND, action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            }

            if (Objects.equals(BluetoothAdapter.ACTION_DISCOVERY_STARTED, action)) {
                Log.d(TAG, "蓝牙搜索设备的广播");
            }

            //断开连接的广播
            if (Objects.equals(BluetoothDevice.ACTION_ACL_DISCONNECTED, action)) {
                Log.d(TAG, "断开连接的广播");
            }

            //刚刚连接的广播
            if (Objects.equals(BluetoothDevice.ACTION_ACL_CONNECTED, action)) {
                Log.d(TAG, "刚刚连接成功的广播，但是可能需要密码配对？");
            }

            //设备配对状态的广播
            if (Objects.equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED, action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String name = device.getName();
                Log.d(TAG, " 设备配对的device name: " + name);
                int state = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, -1);
                switch (state) {
                    case BluetoothDevice.BOND_NONE:
                        Log.d(TAG, "BOND_NONE 删除配对");
                        break;
                    case BluetoothDevice.BOND_BONDING:
                        Log.d(TAG, "BOND_BONDING 正在配对");
                        break;
                    case BluetoothDevice.BOND_BONDED:
                        Log.d(TAG, "BOND_BONDED 配对成功");
                        break;
                }
            }


        }
    };



}
