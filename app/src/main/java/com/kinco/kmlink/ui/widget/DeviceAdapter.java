package com.kinco.kmlink.ui.widget;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.kinco.kmlink.R;

import java.util.List;

public class DeviceAdapter extends BaseAdapter {
    private List<String> devices;
    private int connectingIndex = -1;

    public DeviceAdapter(List<String> devices) {
        this.devices = devices;
    }

    @Override
    public int getCount() {
        return devices.size();
    }

    @Override
    public Object getItem(int position) {
        return devices.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView==null){
            convertView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_item_device,parent,false);
        }
        String[] info = devices.get(position).split("\n");
        TextView tvName = convertView.findViewById(R.id.tv_device_name);
        tvName.setText(info[0]);
        TextView tvAddress = convertView.findViewById(R.id.tv_device_address);
        tvAddress.setText(info[1]);
        ProgressBar progressBar = convertView.findViewById(R.id.pb_connecting);
        if(connectingIndex == position){
            progressBar.setVisibility(View.VISIBLE);
        }else{
            progressBar.setVisibility(View.INVISIBLE);
        }
        return convertView;
    }

    public void showConnecting(int position){
        this.connectingIndex = position;
        notifyDataSetChanged();
    }
}
