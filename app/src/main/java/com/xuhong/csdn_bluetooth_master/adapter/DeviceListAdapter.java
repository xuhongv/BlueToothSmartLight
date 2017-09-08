package com.xuhong.csdn_bluetooth_master.adapter;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.xuhong.csdn_bluetooth_master.R;
import com.xuhong.csdn_bluetooth_master.other.ShareUtils;

import java.util.List;

/**
 * 项目名： CSDN_BlueTooth-master
 * 包名： com.xuhong.csdn_bluetooth_master.adapter
 * 文件名字： DeviceListAdapter
 * 创建时间：2017/9/6 22:42
 * 项目名： Xuhong
 * 描述： TODO
 */

public class DeviceListAdapter extends BaseAdapter {

    private List<BluetoothDevice> list;

    private LayoutInflater layoutInflater;

    private Context mContext;

    public DeviceListAdapter(Context mContext, List<BluetoothDevice> list) {
        this.mContext = mContext;
        layoutInflater = (LayoutInflater) mContext.getSystemService(mContext.LAYOUT_INFLATER_SERVICE);
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHoler viewHoler;
        if (view == null) {
            viewHoler = new ViewHoler();
            view = layoutInflater.inflate(R.layout.item_devices, null);
            viewHoler.tvName = view.findViewById(R.id.tvName);
            view.setTag(viewHoler);
        } else {
            viewHoler = (ViewHoler) view.getTag();
        }
        if (null == ShareUtils.getString(mContext, list.get(i).getName(), null)) {
            viewHoler.tvName.setText(list.get(i).getName());
        } else {
            viewHoler.tvName.setText(ShareUtils.getString(mContext, list.get(i).getName(), null));
        }
        return view;
    }

    private class ViewHoler {
        TextView tvName;
    }
}
