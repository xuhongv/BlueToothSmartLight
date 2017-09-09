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
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.xuhong.csdn_bluetooth_master.other.ShareUtils;
import com.xuhong.csdn_bluetooth_master.ui.BaseActivity;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import cn.pedant.SweetAlert.SweetAlertDialog;


public class MainActivity extends BaseActivity implements View.OnClickListener, View.OnLongClickListener {


    //要传出下个界面的蓝牙对象
    private BluetoothDevice mBluetoothDevice;
    //创建socket
    private BluetoothSocket mSocket = null;
    //io流
    private OutputStream mOutS = null;
    private InputStream input = null;
    private boolean isRevices = true;

    // uuid
    private static final String SPP_UUID = "00001101-0000-1000-8000-00805F9B34FB";

    //ui
    private RelativeLayout rllOne, rllTwo, rllThree, rllFour;
    private SweetAlertDialog progerssAlertDialog;
    private Context mContext;
    private CheckBox mCbState;
    private CheckBox mIVIconLight1;
    private TextView mTvNameLight1;
    private CheckBox mIVIconLight2;
    private TextView mTvNameLight2;
    private CheckBox mIVIconLight3;
    private TextView mTvNameLight3;
    private CheckBox mIVIconLight4;
    private TextView mTvNameLight4;
    private Switch mSwitch1;
    private Switch mSwitch2;
    private Switch mSwitch3;
    private Switch mSwitch4;

    //灯名字
    private String LIGHTNAME_ONE;
    private String LIGHTNAME_TWO;
    private String LIGHTNAME_THREE;
    private String LIGHTNAME_FOUR;

    //协议
    private List<Byte> sendData = new ArrayList<>();
    private static final int HANDLER_SUCCEED = 201;
    private static final int HANDLER_FAIL = 202;
    private static final int HANDLER_GET = 203;
    private static final int RECIECE_SUCCED = 204;
    private String temRevices = null;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case HANDLER_SUCCEED:
                    new mThreadRecieve().start();
                    mHandler.sendEmptyMessageDelayed(HANDLER_GET,2000);
                    progerssAlertDialog.dismiss();
                    break;
                case HANDLER_FAIL:
                    progerssAlertDialog.dismiss();
                    if (isRevices) {
                        showFailAlertDialog("连接失败哦！");
                    }
                    break;
                case RECIECE_SUCCED:
                    upadataUI();
                    break;
                case HANDLER_GET:
                    writeStream(sendData.get(8));
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

        mCbState = (CheckBox) findViewById(R.id.cbState);
        mCbState.setOnClickListener(this);

        mIVIconLight1 = (CheckBox) findViewById(R.id.iVIconLight1);
        mTvNameLight1 = (TextView) findViewById(R.id.tvNameLight1);
        mIVIconLight2 = (CheckBox) findViewById(R.id.iVIconLight2);
        mTvNameLight2 = (TextView) findViewById(R.id.tvNameLight2);
        mIVIconLight3 = (CheckBox) findViewById(R.id.iVIconLight3);
        mTvNameLight3 = (TextView) findViewById(R.id.tvNameLight3);
        mIVIconLight4 = (CheckBox) findViewById(R.id.iVIconLight4);
        mTvNameLight4 = (TextView) findViewById(R.id.tvNameLight4);
        rllOne = (RelativeLayout) findViewById(R.id.rllone);
        rllTwo = (RelativeLayout) findViewById(R.id.rllTwo);
        rllThree = (RelativeLayout) findViewById(R.id.rllthree);
        rllFour = (RelativeLayout) findViewById(R.id.rllfour);
        rllOne.setOnLongClickListener(this);
        rllTwo.setOnLongClickListener(this);
        rllThree.setOnLongClickListener(this);
        rllFour.setOnLongClickListener(this);

        mSwitch1 = (Switch) findViewById(R.id.mSwitch1);
        mSwitch1.setOnClickListener(this);
        mSwitch2 = (Switch) findViewById(R.id.mSwitch2);
        mSwitch2.setOnClickListener(this);
        mSwitch3 = (Switch) findViewById(R.id.mSwitch3);
        mSwitch3.setOnClickListener(this);
        mSwitch4 = (Switch) findViewById(R.id.mSwitch4);
        mSwitch4.setOnClickListener(this);


        sendData.add((byte) 0x01);
        sendData.add((byte) 0x02);
        sendData.add((byte) 0x03);
        sendData.add((byte) 0x04);
        sendData.add((byte) 0x05);
        sendData.add((byte) 0x06);
        sendData.add((byte) 0x07);
        sendData.add((byte) 0x08);
        sendData.add((byte) 0x09);
        sendData.add((byte) 0x10);
        sendData.add((byte) 0x11);

        progerssAlertDialog = new SweetAlertDialog(mContext, SweetAlertDialog.PROGRESS_TYPE);
        progerssAlertDialog.getProgressHelper().setBarColor(getResources().getColor(R.color.yellow));
        progerssAlertDialog.setTitleText("正在连接...");
        progerssAlertDialog.show();

        //显示返回按钮
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //取消阴影
        getSupportActionBar().setElevation(0);

    }

    private void initData() {
        registerReceiver();
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            mBluetoothDevice = bundle.getParcelable("device");
            if (mBluetoothDevice != null) {
                if (null == ShareUtils.getString(this, mBluetoothDevice.getAddress(), null)) {
                    setTitle(mBluetoothDevice.getName());
                } else {
                    setTitle(ShareUtils.getString(this, mBluetoothDevice.getAddress(), null));
                }
                LIGHTNAME_ONE = mBluetoothDevice.getAddress() + "ONE";
                LIGHTNAME_TWO = mBluetoothDevice.getAddress() + "TWO";
                LIGHTNAME_THREE = mBluetoothDevice.getAddress() + "THREE";
                LIGHTNAME_FOUR = mBluetoothDevice.getAddress() + "FOUR";
                getLightName();
                new mThread(mBluetoothDevice).start();
            }
        }

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            //总开关
            case R.id.cbState:
                if (mCbState.isChecked()) {
                    writeStream(sendData.get(10));
                } else {
                    writeStream(sendData.get(9));
                }
                break;
            case R.id.mSwitch1:
                if (mSwitch1.isChecked()) {
                    writeStream(sendData.get(0));
                } else {
                    writeStream(sendData.get(1));
                }
                break;
            case R.id.mSwitch2:
                if (mSwitch2.isChecked()) {
                    writeStream(sendData.get(2));
                } else {
                    writeStream(sendData.get(3));
                }
                break;
            case R.id.mSwitch3:
                if (mSwitch3.isChecked()) {
                    writeStream(sendData.get(4));
                } else {
                    writeStream(sendData.get(5));
                }
                break;
            case R.id.mSwitch4:
                if (mSwitch4.isChecked()) {
                    writeStream(sendData.get(6));
                } else {
                    writeStream(sendData.get(7));
                }
                break;
        }
    }

    @Override
    public boolean onLongClick(View view) {
        switch (view.getId()) {
            case R.id.rllone:
                showRenameDialog(LIGHTNAME_ONE);
                break;
            case R.id.rllTwo:
                showRenameDialog(LIGHTNAME_TWO);
                break;
            case R.id.rllthree:
                showRenameDialog(LIGHTNAME_THREE);
                break;
            case R.id.rllfour:
                showRenameDialog(LIGHTNAME_FOUR);
                break;
        }
        return true;
    }

    //内部类子线程:开启连接
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

    private class mThreadRecieve extends Thread {
        @Override
        public void run() {
            int bytes;
            byte[] buffer = new byte[1024];
            String s1;
            try {
                input = mSocket.getInputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
            while (true) {
                try {
                    if ((bytes = input.read(buffer)) > 0) {
                        byte[] buf_data = new byte[bytes];
                        for (int i = 0; i < bytes; i++) {
                            buf_data[i] = buffer[i];
                        }
                        s1 = bytesToHexString(buf_data);
                        temRevices = s1;
                        mHandler.sendEmptyMessage(RECIECE_SUCCED);
                        s1 = null;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 将byte数组转为16进制字符串 此方法主要目的为方便Log的显示
     */
    public String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (byte aSrc : src) {
            int v = aSrc & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv.toUpperCase()).append("");
        }
        return stringBuilder.toString();
    }


    @Override
    protected void succeedBindBTDevices() {
        isRevices = false;
        mHandler.sendEmptyMessage(HANDLER_SUCCEED);
    }

    private void writeStream(byte data) {
        try {

            Log.e("BaseActivity", "成功发送 mOutS：" + mOutS);

            if (mOutS == null && mSocket != null) {
                mOutS = mSocket.getOutputStream();
            }

            if (mOutS != null) {
                mOutS.write(data);
                mOutS.flush();
            }

        } catch (IOException e) {
            mHandler.sendEmptyMessage(HANDLER_FAIL);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isRevices = false;
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
        mAlertDialog.setContentText("设备地址：" + mBluetoothDevice.getAddress()
                + "\n设备名字：" + mBluetoothDevice.getName()
                + "\n设备UUID：" + Arrays.toString(mBluetoothDevice.getUuids())
        );
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
                showRenameDialog(mBluetoothDevice.getAddress());
                break;
            case android.R.id.home:
                SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(this, SweetAlertDialog.NORMAL_TYPE);
                sweetAlertDialog.setTitleText("提示")
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
                                sweetAlertDialog.dismiss();
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

    @Override
    protected void disableBTDevices() {
        SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(mContext, SweetAlertDialog.ERROR_TYPE);
        sweetAlertDialog.setTitleText("设备连接已断开");
        sweetAlertDialog.show();
        sweetAlertDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                sweetAlertDialog.dismiss();
                finish();
            }
        });
    }

    private void upadataUI() {
        isRevices = false;
        switch (temRevices) {
            case "00":
                break;
            case "28":
                mCbState.setChecked(false);
                break;
            case "29":
                mCbState.setChecked(true);
                break;
            case "20":
                mIVIconLight1.setChecked(true);
                mSwitch1.setChecked(true);
                mTvNameLight1.setTextColor(getResources().getColor(R.color.black));
                break;
            case "21":
                mIVIconLight2.setChecked(true);
                mSwitch2.setChecked(true);
                mTvNameLight2.setTextColor(getResources().getColor(R.color.black));
                break;
            case "22":
                mIVIconLight3.setChecked(true);
                mSwitch3.setChecked(true);
                mTvNameLight3.setTextColor(getResources().getColor(R.color.black));
                break;
            case "23":
                mIVIconLight4.setChecked(true);
                mSwitch4.setChecked(true);
                mTvNameLight4.setTextColor(getResources().getColor(R.color.black));
                break;
            case "24":
                mIVIconLight1.setChecked(false);
                mSwitch1.setChecked(false);
                mTvNameLight1.setTextColor(getResources().getColor(R.color.slategray));
                break;
            case "25":
                mIVIconLight2.setChecked(false);
                mSwitch2.setChecked(false);
                mTvNameLight2.setTextColor(getResources().getColor(R.color.slategray));
                break;
            case "26":
                mIVIconLight3.setChecked(false);
                mSwitch3.setChecked(false);
                mTvNameLight3.setTextColor(getResources().getColor(R.color.slategray));
                break;
            case "27":
                mIVIconLight4.setChecked(false);
                mSwitch4.setChecked(false);
                mTvNameLight4.setTextColor(getResources().getColor(R.color.slategray));
                break;
        }
    }

    private void showRenameDialog(final String key) {
        View view = getLayoutInflater().inflate(R.layout.dialog_rename, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setView(view);
        final AlertDialog dialog = builder.show();

        final EditText text = view.findViewById(R.id.rename_et);
        view.findViewById(R.id.tv_cancel_rename).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        view.findViewById(R.id.tv_exit_rename).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (text.getText().toString().isEmpty()) {
                    Toast.makeText(mContext, "输入不能为空", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    return;
                }
                ShareUtils.putString(mContext, key, text.getText().toString());
                Toast.makeText(mContext, "修改成功！", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
                getLightName();
                setTitle(ShareUtils.getString(mContext, mBluetoothDevice.getAddress(), null));

            }
        });

    }

    private void getLightName() {
        String name1 = ShareUtils.getString(mContext, LIGHTNAME_ONE, null);
        String name2 = ShareUtils.getString(mContext, LIGHTNAME_TWO, null);
        String name3 = ShareUtils.getString(mContext, LIGHTNAME_THREE, null);
        String name4 = ShareUtils.getString(mContext, LIGHTNAME_FOUR, null);

        if (name1 != null) {
            mTvNameLight1.setText(name1);
        } else {
            mTvNameLight1.setText("灯一");
        }
        if (name2 != null) {
            mTvNameLight2.setText(name2);
        } else {
            mTvNameLight2.setText("灯二");
        }
        if (name3 != null) {
            mTvNameLight3.setText(name3);
        } else {
            mTvNameLight3.setText("灯三");
        }
        if (name4 != null) {
            mTvNameLight4.setText(name4);
        } else {
            mTvNameLight4.setText("灯四");
        }
    }

}
