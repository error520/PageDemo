package com.example.pagedemo.ui;

import androidx.appcompat.app.AppCompatActivity;
import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import com.example.pagedemo.R;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.util.ArrayList;

import com.example.pagedemo.BluetoothService.BLEService;
import com.example.pagedemo.util;
//远程推送

/**
 * Created by Excalibur on 2017/6/1.
 * 用于显示蓝牙设备列表，并返回蓝牙设备信息
 */
public class DeviceList extends AppCompatActivity{
    public static String EXTRA_DEVICE_ADDRESS="device_address";
    private BluetoothAdapter mBtAdapter;
    private ArrayAdapter<String> mPairedDevicesArrayAdapter;
    private ArrayAdapter<String> mNewDevicesArrayAdapter;
    private IntentFilter filter = new IntentFilter();
    private ProgressBar progressBar;
    private Button BLEScan;
    private Switch Filter;
    private boolean mScanning;//是否正在搜索
    private Handler mHandler;
    private TextView count;
    private static final long SCAN_PERIOD = 10000;
    private String TAG = "DeviceList";
    private String slaveAddress="null";
    BLEService mBLE;
    private LocalBroadcastManager localBroadcastManager;
    ArrayList<String> device_list = new ArrayList<String>();
    ArrayList<String> connected_list = new ArrayList<String>();
    private LocalReceiver localReceiver;
    private BLEService mBluetoothLeService;

    private void initUI(){
        setContentView(R.layout.device_list);
        setResult(Activity.RESULT_CANCELED);
        progressBar = (ProgressBar)findViewById(R.id.processbar);
        progressBar.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mBluetoothLeService.stopLeScan();//点击停止
                progressBar.setVisibility(View.GONE);
                BLEScan.setVisibility(View.VISIBLE);
            }
        });
        BLEScan=(Button)findViewById(R.id.BLEscan);
        Filter=(Switch)findViewById(R.id.filter);
        Filter.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                    mBluetoothLeService.mFilter=true;
                else
                    mBluetoothLeService.mFilter=false;
            }
        });
        BLEScan.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
               try {
                   mBluetoothLeService.scanLeDevice(true);
                   device_list.clear();
                   mNewDevicesArrayAdapter.notifyDataSetChanged();
                   BLEScan.setVisibility(View.GONE);
                   progressBar.setVisibility(View.VISIBLE);
               }catch(Exception e ){Log.d("device_list",e.toString());}
            }
        });

        mPairedDevicesArrayAdapter=new ArrayAdapter<String>(this,
                R.layout.list_item,connected_list);
        mNewDevicesArrayAdapter=new ArrayAdapter<String>(this,
                R.layout.list_item,device_list);

        ListView pairedListView=(ListView)findViewById(R.id.connected_devices);
        pairedListView.setAdapter(mPairedDevicesArrayAdapter);
        pairedListView.setOnItemClickListener(mDisconnectListen);
        if(!BLEService.slaveAddress.equals("null")){
            slaveAddress = BLEService.slaveAddress;
            connected_list.clear();
            connected_list.add(slaveAddress);
            mPairedDevicesArrayAdapter.notifyDataSetChanged();
        }
        ListView newDeviceListView=(ListView)findViewById(R.id.new_devices);
        newDeviceListView.setAdapter(mNewDevicesArrayAdapter);
        newDeviceListView.setOnItemClickListener(mDeviceClickListen);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
//        try {
            initUI();
            getBlePermissionFromSys();
            Intent BLEIntent = new Intent(this, BLEService.class);
            bindService(BLEIntent, connection, BIND_AUTO_CREATE);
            mHandler = new Handler();
            localReceiver = new LocalReceiver();
            localBroadcastManager = LocalBroadcastManager.getInstance(this);
            localBroadcastManager.registerReceiver(localReceiver, util.makeGattUpdateIntentFilter());
    }
    @Override
    protected void onDestroy(){
        super.onDestroy();
        localBroadcastManager.unregisterReceiver(localReceiver);
    }

    private OnItemClickListener mDisconnectListen=new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView,
                                View view, int i, long l) {
                mBluetoothLeService.close();
                connected_list.clear();
                mPairedDevicesArrayAdapter.notifyDataSetChanged();
        }
    };


    //选项监听器
    private OnItemClickListener mDeviceClickListen=new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView,
                                View view, int i, long l) {
            mBluetoothLeService.stopLeScan();
            String info=((TextView) view).getText().toString();
            String address=info.substring(info.length()-17);
//            Intent intent =new Intent();
//            intent.putExtra(EXTRA_DEVICE_ADDRESS,address);
            slaveAddress = info;
            if(!slaveAddress.equals("null")) {
                connected_list.clear();
                mPairedDevicesArrayAdapter.notifyDataSetChanged();
            }
            Toast toast = Toast.makeText(DeviceList.this,"Connecting...please wait", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER,0,0);
            toast.show();
            mBluetoothLeService.connect(address);
//            finish();
//            Intent intent2 = new Intent(DeviceList.this,ChatActivity.class);
//            intent2.putExtra(EXTRA_DEVICE_ADDRESS,address);

//            startActivity(intent2);
        }
    };




    //获取位置权限
    public void getBlePermissionFromSys() {
        Log.d(TAG, Build.VERSION.SDK_INT+"");
        if (Build.VERSION.SDK_INT >= 23) {
            int REQUEST_CODE_CONTACT = 102;
            String[] permissions = {Manifest.permission.ACCESS_COARSE_LOCATION};
            this.requestPermissions(permissions, REQUEST_CODE_CONTACT);
            //验证是否许可权限
            for (String str : permissions) {
                if (this.checkSelfPermission(str) != PackageManager.PERMISSION_GRANTED) {
                    //申请权限
                    this.requestPermissions(permissions, REQUEST_CODE_CONTACT);
                    Log.d(TAG,"申请了位置权限");
                    return;
                }
            }
        }
    }

    /**
     * 强制帮用户打开GPS
     * @param context
     */
    public static final void openGPS(Context context) {
        Intent GPSIntent = new Intent();
        GPSIntent.setClassName("com.android.settings",
                "com.android.settings.widget.SettingsAppWidgetProvider");
        GPSIntent.addCategory("android.intent.category.ALTERNATIVE");
        GPSIntent.setData(Uri.parse("custom:3"));
        try {
            PendingIntent.getBroadcast(context, 0, GPSIntent, 0).send();
        } catch (PendingIntent.CanceledException e) {
            e.printStackTrace();
        }
    }

    private class LocalReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.d(TAG,action);
            //Toast.makeText(DeviceList.this, intent.getStringExtra(BLEService.EXTRA_DATA), Toast.LENGTH_SHORT).show();
            if (action.equals(BLEService.ACTION_GET_DEVICE_NAME)){
                device_list.add(intent.getStringExtra(BLEService.ACTION_GET_DEVICE_NAME));
                mNewDevicesArrayAdapter.notifyDataSetChanged();
            }
            else if(action.equals(BLEService.ACTION_SEARCH_COMPLETED)) {
                progressBar.setVisibility(View.GONE);
                BLEScan.setVisibility(View.VISIBLE);
            }
            else if(action.equals(BLEService.ACTION_GATT_CONNECTED)){
                Toast toast = Toast.makeText(getApplicationContext(),"Connection succeed!",Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER,0,0);
                toast.show();
                mBluetoothLeService.slaveAddress = slaveAddress;
                connected_list.clear();
                connected_list.add(slaveAddress);
                mPairedDevicesArrayAdapter.notifyDataSetChanged();
                finish();
            }
            else if(action.equals(BLEService.ACTION_GATT_DISCONNECTED)){
                Toast toast = Toast.makeText(getApplicationContext(),"Connection failed!",Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER,0,0);
                toast.show();
            }
        }
    }

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mBluetoothLeService = ((BLEService.localBinder) service)
                    .getService();
            mBluetoothLeService.mFilter=true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };





}
