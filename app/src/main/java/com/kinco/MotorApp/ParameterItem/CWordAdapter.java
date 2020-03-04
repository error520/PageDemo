package com.kinco.MotorApp.ParameterItem;

import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;

import com.kinco.MotorApp.BluetoothService.BLEService;
import com.kinco.MotorApp.R;
import com.kinco.MotorApp.sys.SysApplication;
import com.kinco.MotorApp.utils.util;


import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

public class CWordAdapter extends BaseAdapter {
    private Context context = SysApplication.getContext();
    private Resources resources = context.getResources();
    private List<CWordBean> beans = new ArrayList<>();
    private byte[] data = new byte[2];
    private StringBuilder byte0 = new StringBuilder("00000111");
    private StringBuilder byte1 = new StringBuilder("00000000");
    LayoutInflater inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    //private BLEService mBluetoothLeService;
    public CWordAdapter(String[] ori_options){
        //this.mBluetoothLeService = mBluetoothLeService;
        String[] Name = resources.getStringArray(R.array.A023_bits_Name);
        //String[] ori_options = resources.getStringArray(R.array.CWord_options);
        String[][] options = new String[ori_options.length][];
        for(int i=0;i<ori_options.length;i++){
            options[i] = ori_options[i].split(",");
        }
        for(int i=0;i<Name.length;i++){
            beans.add(new CWordBean(Name[i],options[i]));
        }

    }
    @Override
    public int getCount() {
        return beans.size();
    }

    @Override
    public Object getItem(int position) {
        return beans.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int bitPosition, View convertView, ViewGroup parent) {
        if(convertView==null)
            convertView = inflater.inflate(R.layout.controlword_item,null);
        final CWordBean bean= beans.get(bitPosition);
        final TextView textView = convertView.findViewById(R.id.CWtextview);
        textView.setText(bean.bit);
        final Spinner spinner = convertView.findViewById(R.id.CWspinner);
        spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(bitPosition==0){
                    String value = (bean.content)[position];
                    value = value.split("B")[0];
                    byte0.replace(5,8,value);
                }else {
                    int bit = bitPosition + 2;
                    if (bitPosition < 6) {
                        bit = 7-bit;
                        byte0.replace(bit,bit+1,String.valueOf(position));
                    } else {
                        bit = 15-bit;
                        byte1.replace(bit,bit+1,String.valueOf(position));
                    }
                }
            }


            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context,android.R.layout.simple_spinner_item,bean.content);
        spinner.setAdapter(adapter);
        return convertView;
    }

    public byte[] getData(){
        try{
            //低位在后
            data[0] = Byte.parseByte(byte1.toString(),2);
            data[1] = Byte.parseByte(byte0.toString(),2);
        }catch (Exception e){
            util.centerToast(context,e.toString(),1);
        }
        return data;
    }

}
