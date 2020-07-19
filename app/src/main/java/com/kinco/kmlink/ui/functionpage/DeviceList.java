package com.kinco.kmlink.ui.functionpage;

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
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.kinco.kmlink.BluetoothService.BleService;
import com.kinco.kmlink.LanguageUtils.LanguageUtil;
import com.kinco.kmlink.utils.PrefUtil;
import com.kinco.kmlink.MainActivity;
import com.kinco.kmlink.alertdialog.LoadingDialog;
import com.kinco.kmlink.alertdialog.PasswordDialog;
import com.kinco.kmlink.utils.util;
import com.kinco.kmlink.R;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;


/**
 * 用于显示蓝牙设备列表，并返回蓝牙设备信息
 */
public class DeviceList extends AppCompatActivity{
    private ArrayAdapter<String> mPairedDevicesArrayAdapter;
    private ArrayAdapter<String> mNewDevicesArrayAdapter;
    private ProgressBar progressBar;
    private Button BLEScan;
    private Button SKIP;
    private PasswordDialog dialog;
    private LoadingDialog loadingDialog;
    Timer timer = new Timer();
    private static final long SCAN_PERIOD = 10000;
    private String TAG = "DeviceList";
    private String slaveAddress="null";
    private LocalBroadcastManager localBroadcastManager;
    ArrayList<String> device_list = new ArrayList<String>();
    ArrayList<String> connected_list = new ArrayList<String>();
    private LocalReceiver localReceiver;
    private BleService mBluetoothLeService;
    private String editPassword="";
    private String name = "";

    private void initUI(){
        setContentView(R.layout.activity_device_connection);
        setResult(Activity.RESULT_CANCELED);
        progressBar = (ProgressBar)findViewById(R.id.progressBar);
//        BLEScan=(Button)findViewById(R.id.BLEscan);
        SKIP = findViewById(R.id.btn_skip);
        SKIP.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                stopScan();
                Intent activityIntent = new Intent(DeviceList.this, MainActivity.class);
                startActivity(activityIntent);
                finish();
            }
        });
//        BLEScan.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//               try {
//                   mBluetoothLeService.scanLeDevice(true);
//                   device_list.clear();
//                   mNewDevicesArrayAdapter.notifyDataSetChanged();
//                   BLEScan.setVisibility(View.GONE);
//                   progressBar.setVisibility(View.VISIBLE);
//               }catch(Exception e ){Log.d("activity_device_connection",e.toString());}
//            }
//        });

        mPairedDevicesArrayAdapter=new ArrayAdapter<String>(this,
                R.layout.list_item,connected_list);
        mNewDevicesArrayAdapter=new ArrayAdapter<String>(this,
                R.layout.list_item,device_list);
        ListView newDeviceListView=(ListView)findViewById(R.id.new_devices);
        newDeviceListView.setAdapter(mNewDevicesArrayAdapter);
        newDeviceListView.setOnItemClickListener(mDeviceClickListen);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState){
        LanguageUtil.changeAppLanguage(this, PrefUtil.getLanguage(this)); // onCreate 之前调用 否则不起作用
        super.onCreate(savedInstanceState);
        initUI();
        getBlePermissionFromSys();
        Intent BLEIntent = new Intent(this, BleService.class);
        bindService(BLEIntent, connection, BIND_AUTO_CREATE);
        localReceiver = new LocalReceiver();
        localBroadcastManager = LocalBroadcastManager.getInstance(this);
        localBroadcastManager.registerReceiver(localReceiver, util.makeGattUpdateIntentFilter());
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        stopScan();
        unbindService(connection);
        localBroadcastManager.unregisterReceiver(localReceiver);
    }

    //选项监听器
    private OnItemClickListener mDeviceClickListen=new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView,
                                View view, int i, long l) {
            stopScan();
            String info=((TextView) view).getText().toString();
            String address=info.substring(info.length()-17);
            name = info.split("\n")[0];
            slaveAddress = info;
            if(!slaveAddress.equals("null")) {
                connected_list.clear();
                mPairedDevicesArrayAdapter.notifyDataSetChanged();
            }
            loadingDialog = new LoadingDialog(DeviceList.this,"",getResources().getString(R.string.connecting_text),true);
            loadingDialog.setOnClickCancelListener(new LoadingDialog.OnClickCancelListener(){
                public void onNegativeClick(){
                    loadingDialog.gone();
                    mBluetoothLeService.close();
                }
            });
            mBluetoothLeService.connect(address);

        }
    };




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

    private class LocalReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.d(TAG,action);
            //Toast.makeText(DeviceList.this, intent.getStringExtra(BLEService.EXTRA_DATA), Toast.LENGTH_SHORT).show();
            if (action.equals(BleService.ACTION_GET_DEVICE_NAME)){
                device_list.add(intent.getStringExtra(BleService.ACTION_GET_DEVICE_NAME));
                mNewDevicesArrayAdapter.notifyDataSetChanged();
            }
            else if(action.equals(BleService.ACTION_SEARCH_COMPLETED)) {
                progressBar.setVisibility(View.INVISIBLE);
            }
            else if(action.equals(BleService.ACTION_GATT_CONNECTED)){
//                stopScan();
                util.centerToast(DeviceList.this,getString(R.string.connected),0);
                mBluetoothLeService.slaveAddress = slaveAddress;
                try {
                    loadingDialog.gone();
                    mBluetoothLeService.slaveCode = util.intToByte2(Integer.valueOf(slaveAddress.substring(slaveAddress.indexOf("_") + 1, slaveAddress.indexOf("\n"))))[1];
                    Log.d(TAG,slaveAddress.substring(slaveAddress.indexOf("_")+1,slaveAddress.indexOf("\n")));
                    connected_list.clear();
                    connected_list.add(slaveAddress);
                    mPairedDevicesArrayAdapter.notifyDataSetChanged();
                    showPasswordDialog();

                }catch(Exception e){
                    util.centerToast(DeviceList.this,getString(R.string.get_slaveAddress_failed),0);
                    e.printStackTrace();
                    Log.d(TAG,e.toString());
                }
            }
            else if(action.equals(BleService.ACTION_GATT_DISCONNECTED)){
                if(!(loadingDialog==null))
                    loadingDialog.gone();
                scanPeriodically();
                BleService.deviceName.postValue(getString(R.string.unconnected));
                util.centerToast(DeviceList.this,getString(R.string.connection_failed),0);
            }
            else if(action.equals(BleService.ACTION_DATA_AVAILABLE)){
                byte[] message = new byte[2];
                message[0]=intent.getByteArrayExtra(BleService.EXTRA_MESSAGE_DATA)[3];
                message[1]=intent.getByteArrayExtra(BleService.EXTRA_MESSAGE_DATA)[4];
                String password=util.toHexString(message,false);
                if(dialog!=null){
                    if(editPassword.equals(password)) {
                        util.centerToast(DeviceList.this, getString(R.string.password_correct), 0);
                        dialog.gone();
                        dialog=null;
                        BleService.deviceName.setValue(name);
                        Intent activityIntent = new Intent(DeviceList.this, MainActivity.class);
                        startActivity(activityIntent);
                        finish();

                    }else
                        util.centerToast(DeviceList.this, getString(R.string.password_wrong), 0);
                }


            }
        }
    }

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mBluetoothLeService = ((BleService.localBinder) service)
                    .getService();
            scanPeriodically();
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
                      editPassword = dialog.getPassword();

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
            device_list.clear();
            runOnUiThread(()->{
                progressBar.setVisibility(View.VISIBLE);
                mNewDevicesArrayAdapter.notifyDataSetChanged();
            });
            mBluetoothLeService.scanLeDevice(7000);
        }
    }
}
