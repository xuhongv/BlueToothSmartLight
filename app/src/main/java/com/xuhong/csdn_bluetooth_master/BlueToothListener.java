package com.xuhong.csdn_bluetooth_master;

/**
 * 项目名： CSDN_BlueTooth-master
 * 包名： com.xuhong.csdn_bluetooth_master
 * 文件名字： BlueToothListener
 * 创建时间：2017/9/6 23:06
 * 项目名： Xuhong
 * 描述： TODO
 */

public interface BlueToothListener {

    void startScanDevices();

    void stateChanged();

    void succeedConnect();

    void failConnection();

    void stateScan();

}
