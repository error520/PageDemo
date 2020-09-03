package com.kinco.kmlink.ui.main;

import android.Manifest;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.google.android.material.button.MaterialButton;
import com.kinco.kmlink.BluetoothService.BleService;
import com.kinco.kmlink.EventBusUtils.BleDataEvent;
import com.kinco.kmlink.EventBusUtils.MessageEvent;
import com.kinco.kmlink.LanguageUtils.LanguageUtil;
import com.kinco.kmlink.R;
import com.kinco.kmlink.alertdialog.PasswordDialog;
import com.kinco.kmlink.ui.widget.DeviceAdapter;
import com.kinco.kmlink.utils.PrefUtil;
import com.kinco.kmlink.utils.util;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Pattern;

import androidx.appcompat.app.AppCompatActivity;


/**
 * 用于显示蓝牙设备列表，并返回蓝牙设备信息
 */
public class DeviceList extends AppCompatActivity{
    private DeviceAdapter deviceAdapter;
    private ProgressBar progressBar;
    private MaterialButton btnScan;
    private Button btnSkip;
    private PasswordDialog dialog;
    Timer timer = new Timer();
    private static final long SCAN_PERIOD = 10000;
    private String TAG = "DeviceList";
    ArrayList<String> deviceList = new ArrayList<String>();
    private BleService mBluetoothLeService;
    private String inputPassword="";
    private Boolean scanning = false;

    private void initUI(){
        setContentView(R.layout.activity_device_connection);
        progressBar = findViewById(R.id.progressBar);
        btnScan=findViewById(R.id.btn_scan);
        btnSkip = findViewById(R.id.btn_skip);
        btnScan.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!scanning){
                    scanDevice(true);
                    deviceAdapter.showConnecting(-1);
                }else{
                    scanDevice(false);
                }
            }
        });
        btnSkip.setOnClickListener(v -> startMainActivity());
        deviceAdapter=new DeviceAdapter(deviceList);
        ListView newDeviceListView=findViewById(R.id.new_devices);
        newDeviceListView.setAdapter(deviceAdapter);
        newDeviceListView.setOnItemClickListener(((parent, view, position, id) -> {
            String[] info = deviceList.get(position).split("\n");   //0是设备名, 1是地址
            if(Pattern.matches(BleService.namePattern,info[0])){
                mBluetoothLeService.connect(info[1]);
                deviceAdapter.showConnecting(position);
            }else{
                util.centerToast(this,getString(R.string.not_specific_device),0);
            }
        }));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState){
        LanguageUtil.changeAppLanguage(this, PrefUtil.getLanguage(this)); // onCreate 之前调用 否则不起作用
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        initUI();
        getBlePermissionFromSys();
        Intent BLEIntent = new Intent(this, BleService.class);
        bindService(BLEIntent, connection, BIND_AUTO_CREATE);
        BleService.isConnected.observe(this,isConnected->{
            if(isConnected){
                showPasswordDialog();
            }else{
                deviceAdapter.showConnecting(-1);
            }
        });
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        scanDevice(false);
        unbindService(connection);
        EventBus.getDefault().unregister(this);
//        localBroadcastManager.unregisterReceiver(localReceiver);
    }

    //获取位置权限
    public void getBlePermissionFromSys() {
        //Log.d(TAG, Build.VERSION.SDK_INT+"");
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

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mBluetoothLeService = ((BleService.localBinder) service)
                    .getService();
//            scanPeriodically();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBluetoothLeService.stopLeScan();
        }
    };


    private void showPasswordDialog(){
        try {
              dialog = new PasswordDialog(this);
              dialog.setOnClickBottomListener(new PasswordDialog.OnClickBottomListener(){
                  @Override
                  public void onPositiveClick() {
                      mBluetoothLeService.readData("0000","0001");
                      inputPassword = dialog.getPassword();
                  }
                  @Override
                  public void onNegativeClick() {
                      mBluetoothLeService.close();
                      dialog.gone();
                  }
              });
        }catch(Exception e){
            Log.d(TAG,"PasswordDialog error");
        }
    }

    /**
     * 周期性自动扫描
     */
    private void scanPeriodically(){
        if (timer == null) {
            timer = new Timer();
        }
        TimerTask task = new ScanTimeTask();
        timer.schedule(task,0,10000);
    }

    private void stopScan(){
        if(timer!=null){
            timer.cancel();
            timer = null;
        }
        mBluetoothLeService.stopLeScan();
        progressBar.setVisibility(View.INVISIBLE);
    }

    class ScanTimeTask extends TimerTask{
        @Override
        public void run() {
            deviceList.clear();
            runOnUiThread(()->{
                progressBar.setVisibility(View.VISIBLE);
                deviceAdapter.notifyDataSetChanged();
            });
            mBluetoothLeService.scanLeDevice(7000);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNewMessage(MessageEvent event) {
        String message = event.getMessage();
        if (message.startsWith("d:")) {
            String device = message.split("d:")[1];
            if (!deviceList.contains(device)) {
                deviceList.add(device);
                deviceAdapter.notifyDataSetChanged();
            }
        }else if (message.equals("search completed")) {
            scanDevice(false);
        }else if(message.equals("connect failed")){
            deviceAdapter.showConnecting(-1);
            util.centerToast(this,getString(R.string.connection_failed),0);
        }
    }
    
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNewBleData(BleDataEvent event){
        byte[] message = event.getBleData();
        byte[] bytesPassword = new byte[]{message[3], message[4]};
        String password = util.toHexString(bytesPassword, false);
        if(this.inputPassword.equals(password)&&dialog!=null){
            dialog.gone();
            dialog = null;
            BleService.varified = true;
            util.centerToast(this,getString(R.string.password_correct),0);
            startMainActivity();
        }else{
            util.centerToast(this,getString(R.string.password_wrong),0);
        }
    }

    /**
     * 是否扫描设备
     */
    private void scanDevice(Boolean scanning) {
        this.scanning = scanning;
        if (scanning) {
            deviceList.clear();
            deviceAdapter.notifyDataSetChanged();
            mBluetoothLeService.scanLeDevice(7000);
            btnScan.setText(getString(R.string.stop_scan));
            btnScan.setIcon(null);
            progressBar.setVisibility(View.VISIBLE);
        } else {
            mBluetoothLeService.stopLeScan();
            btnScan.setText(getString(R.string.SCAN));
            btnScan.setIcon(getDrawable(R.drawable.ic_search));
            progressBar.setVisibility(View.INVISIBLE);
        }
    }

    private void startMainActivity(){
        Intent activityIntent = new Intent(DeviceList.this, MainActivity.class);
        startActivity(activityIntent);
        finish();
    }
}
