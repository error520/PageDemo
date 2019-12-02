package com.kinco.MotorApp.ui.thirdpage;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.kinco.MotorApp.BluetoothService.BLEService;
import com.kinco.MotorApp.ui.secondpage.SecondpageFragment;
import com.kinco.MotorApp.util;
import com.kinco.MotorApp.R;

import java.util.ArrayList;
import java.util.List;


public class ThirdpageFragment extends Fragment {
    private View view;//得到碎片对应的布局文件,方便后续使用
    private Spinner spinner0;
    private Spinner spinner1;
    private Button btnRead;
    private Button btnWrite;
    private TextView currentValue;
    private TextView setValue;
    private ArrayAdapter<String> arr_adapter;
    private String TAG = "third";
    BLEService mBluetoothLeService;
    private String state = "";
    private List<String> data_list0;
    private List<String> data_list1;
    private ArrayAdapter<String> arr_adapter0;
    private ArrayAdapter<String> arr_adapter1;
    private String address;
    private LocalBroadcastManager localBroadcastManager;
    private BroadcastReceiver receiver=new LocalReceiver();

    //记住一定要重写onCreateView方法
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.read_and_write, container, false);//得到对应的布局文件
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initUI();
        initService();
    }

    @Override
    public void onResume() {
        super.onResume();
        localBroadcastManager.registerReceiver(receiver, util.makeGattUpdateIntentFilter());
    }

    @Override
    public void onPause() {
        super.onPause();
        localBroadcastManager.unregisterReceiver(receiver);
    }

    private void initService(){
        //绑定服务
        Intent BLEIntent = new Intent(getActivity(), BLEService.class);
        getActivity().bindService(BLEIntent,new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                mBluetoothLeService = ((BLEService.localBinder) service)
                        .getService();
            }
            @Override
            public void onServiceDisconnected(ComponentName name) {
            }
        }, Context.BIND_AUTO_CREATE);
        localBroadcastManager =LocalBroadcastManager.getInstance(getContext());
        localBroadcastManager.registerReceiver(receiver, util.makeGattUpdateIntentFilter());
    }
    private void initUI(){
        spinner0 = (Spinner)getActivity().findViewById(R.id.spinner0);
        spinner1 = (Spinner)getActivity().findViewById(R.id.spinner1);
        data_list0 = new ArrayList<String>();
        data_list1 = new ArrayList<String>();
        currentValue = (TextView)getActivity().findViewById(R.id.currentValue);
        setValue = (TextView)getActivity().findViewById(R.id.setValue);
        for(int i=0;i<30;i++){
            if(i<16) {
                data_list0.add("0x000" + Integer.toHexString(i).toUpperCase());
                data_list1.add("0x000" + Integer.toHexString(i).toUpperCase());
            }
            else {
                data_list0.add("0x00" + Integer.toHexString(i).toUpperCase());
                data_list1.add("0x00" + Integer.toHexString(i).toUpperCase());
            }
        }
        for(int i=0;i<30;i++){
            if(i<16) {
                data_list0.add("0x010" + Integer.toHexString(i).toUpperCase());
            }
            else {
                data_list0.add("0x01" + Integer.toHexString(i).toUpperCase());
            }
        }
        arr_adapter0 = new ArrayAdapter<String>(this.getActivity(),android.R.layout.simple_spinner_item,data_list0);
        arr_adapter1 = new ArrayAdapter<String>(this.getActivity(),android.R.layout.simple_spinner_item,data_list1);
        spinner0.setAdapter(arr_adapter0);
        spinner1.setAdapter(arr_adapter1);
        btnRead = (Button)getActivity().findViewById(R.id.btnRead);
        btnRead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBluetoothLeService.readData(spinner0.getSelectedItem().toString().substring(2,6),"0001");
                state = "read";
            }
        });
        btnWrite = (Button)getActivity().findViewById(R.id.btnWrite);
        btnWrite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBluetoothLeService.writeData(spinner1.getSelectedItem().toString().substring(2,6),
                        setValue.getText().toString());
                state = "write";
            }
        });
    }

    public ThirdpageFragment newInstance(int i) {
        Bundle args = new Bundle();
        args.putInt("int", i);
        ThirdpageFragment fragment = new ThirdpageFragment();
//        fragment.setArguments(args);
        return fragment;
    }

    private class LocalReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.d(TAG,action);
            if(action.equals(BLEService.ACTION_DATA_AVAILABLE)) {
                byte[] message = intent.getByteArrayExtra(BLEService.EXTRA_MESSAGE_DATA);
                if(state.equals("read"))
                    currentValue.setText(util.toHexString(message,3));
                if(state.equals("write"))
                    util.centerToast(context,"succeed!",Toast.LENGTH_SHORT);

            }
            else if(action.equals(BLEService.ACTION_GATT_DISCONNECTED)) {
                util.centerToast(context,"Bluetooth disconnected!", Toast.LENGTH_SHORT);
            }
            else if(action.equals(BLEService.ACTION_DATA_LENGTH_FALSE)) {
                Toast.makeText(context, "Length error!", Toast.LENGTH_SHORT).show();
            }
            else if(action.equals(BLEService.ACTION_ERROR_CODE)) {
                String message = intent.getStringExtra(BLEService.ACTION_ERROR_CODE);
                util.centerToast(context,"error code:"+message, Toast.LENGTH_SHORT);
            }
        }
    }
}