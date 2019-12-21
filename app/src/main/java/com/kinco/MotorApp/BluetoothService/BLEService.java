package com.kinco.MotorApp.BluetoothService;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
//import androidx.localbroadcastmanager.content;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Binder;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.kinco.MotorApp.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;


public class BLEService extends Service {
    private final String TAG = "BLEService";
    private BluetoothAdapter mBtAdapter;
    private Handler mHandler;
    private ArrayList<String> device_list = new ArrayList<String>();
    private LocalBroadcastManager localBroadcastManager;
    public final static String ACTION_GATT_CONNECTED = "com.kinco.BLEService.ACTION_GATT_CONNECTED";
    public final static String ACTION_GATT_DISCONNECTED = "com.kinco.BLEService.ACTION_GATT_DISCONNECTED";
    public final static String ACTION_GATT_SERVICES_DISCOVERED = "com.kinco.BLEService.ACTION_GATT_SERVICES_DISCOVERED";
    public final static String ACTION_DATA_AVAILABLE = "com.kinco.BLEService.ACTION_DATA_AVAILABLE";
    public final static String ACTION_GET_DEVICE_NAME = "com.kinco.BLEService.ACTION_GET_DEVICE_NAME";
    public final static String ACTION_SEARCH_COMPLETED = "com.kinco.BLEService.ACTION_SEARCH_COMPLETED";
    public final static String EXTRA_MESSAGE_DATA = "com.kinco.BLEService.EXTRA_DATA";
    public final static String ACTION_DATA_LENGTH_FALSE = "com.kinco.BLEService.ACTION_DATA_LENGTH_FALSE";
    public final static  String ACTION_ERROR_CODE="com.kinco.BLEService.ACTION_ERROR_CODE";
    private boolean mScanning;
    public boolean mConnected=false;
    public boolean mFilter = true;
    public static String slaveAddress="null";
    public static byte slaveCode = 0x00;
    private final  IBinder mBinder = new localBinder();
    private BluetoothGatt mBluetoothGatt;
    private List<BluetoothGattCharacteristic> gattCharacteristics;
    private List<UUID> readUuid = new ArrayList<UUID>();
    private List<UUID> writeUuid = new ArrayList<UUID>();
    private List<UUID> notifyUuid = new ArrayList<UUID>();
    private UUID notify_UUID_service;
    private UUID notify_UUID_chara;
    private BluetoothGattCharacteristic readCharacteristic;
    private BluetoothGattCharacteristic writeCharacteristic;


    public boolean init(){
        localBroadcastManager = LocalBroadcastManager.getInstance(this);
        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBtAdapter = bluetoothManager.getAdapter();
        mHandler = new Handler();
        //创建日志文件夹
        File dir = new File(Environment.getExternalStorageDirectory()+"/KincoLog");
        if(!dir.exists())
            dir.mkdir();
        //检查手机是否支持BLE
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Log.d("BLEService","不支持BLE!");
            return false;
        }
        if (mBtAdapter == null || !mBtAdapter.isEnabled()) {
              mBtAdapter.enable();//强制打开蓝牙
//            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//            activity.startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
        return true;
    }
    /**
    *发送普通带action字段的广播
    */
    private void broadcastUpdate(final String action){
        Intent intent = new Intent(action);
        localBroadcastManager.sendBroadcast(intent);
    }
    /**
     * 返回搜索到的设备信息
     */
    private void broadcastUpdate(final String action, final String device){
        Intent intent = new Intent(action);
        intent.putExtra(action,device);
        localBroadcastManager.sendBroadcast(intent);
    }
    /**
     * 接收到消息, 并传递一定信息量到前台
     * 0是正常接收, 1是错误码, 2是长度不正常
     * */
    public void broadcastUpdate(String message,int i){
        switch(i) {
            case 0:{Intent intent = new Intent(ACTION_DATA_AVAILABLE);
                intent.putExtra(EXTRA_MESSAGE_DATA, message);
                localBroadcastManager.sendBroadcast(intent);}break;
            case 1:{Intent intent = new Intent(ACTION_ERROR_CODE);
                intent.putExtra(ACTION_ERROR_CODE, message);
                localBroadcastManager.sendBroadcast(intent);}break;
            case 2:{Intent intent = new Intent(ACTION_DATA_LENGTH_FALSE);
                intent.putExtra(ACTION_DATA_LENGTH_FALSE, message);
                localBroadcastManager.sendBroadcast(intent);}break;
        }
    }

    /**
     * @description 传递byte数组到前台
     * @param message
     * @param i
     */
    public void broadcastUpdate(byte[] message, int i){
        switch(i){
            case 0:{Intent intent = new Intent(ACTION_DATA_AVAILABLE);
                intent.putExtra(EXTRA_MESSAGE_DATA, message);
                localBroadcastManager.sendBroadcast(intent);}break;
            case 1:{Intent intent = new Intent(ACTION_ERROR_CODE);
                intent.putExtra(ACTION_ERROR_CODE, message);
                localBroadcastManager.sendBroadcast(intent);}break;
            case 2:{Intent intent = new Intent(ACTION_DATA_LENGTH_FALSE);
                intent.putExtra(ACTION_DATA_LENGTH_FALSE, message);
                localBroadcastManager.sendBroadcast(intent);}break;
        }
    }

    public void scanLeDevice(final boolean enable) {
        if (enable) {//true

            try {
                device_list.clear();
                //5秒后停止搜索
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mScanning = false;
                        mBtAdapter.stopLeScan(mLeScanCallback);
                        Log.d(TAG,"扫描完成");
                        broadcastUpdate(ACTION_SEARCH_COMPLETED);

                    }
                }, 5000);
                mScanning = true;
                mBtAdapter.startLeScan(mLeScanCallback); //开始搜索
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {//false
            mScanning = false;
            mBtAdapter.stopLeScan(mLeScanCallback);//停止搜索
        }
    }
    public void stopLeScan(){
        if (mScanning == true)
            mBtAdapter.stopLeScan(mLeScanCallback);
    }

    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
                if(!device_list.contains(device.getName()+"\n" +
                        device.getAddress())) {
                    device_list.add(device.getName()+"\n" +
                            device.getAddress());
                    if(!Objects.isNull(device.getName())||!mFilter)
                        broadcastUpdate(ACTION_GET_DEVICE_NAME,device.getName()+"\n" +
                            device.getAddress());
                    Log.d(TAG,device.getName()+"\n" +
                            device.getAddress());
                }


        }
    };

    public boolean connect(final String address) {//4
//        Log.d(TAG, "连接" + mBluetoothDeviceAddress);
        if(!(mBluetoothGatt==null))
            close();
        if (mBtAdapter == null || address == null) {
            Log.d(TAG,"BluetoothAdapter不能初始化 or 未知 address.");
            Toast.makeText(this,"未能连接",Toast.LENGTH_LONG).show();
            broadcastUpdate(ACTION_GATT_DISCONNECTED);
            return false;
        }

        final BluetoothDevice device = mBtAdapter
                .getRemoteDevice(address);
        if (device == null) {
            Toast.makeText(this,"设备没找到，不能连接",Toast.LENGTH_SHORT).show();
            Log.d("data", "设备没找到，不能连接");
            broadcastUpdate(ACTION_GATT_DISCONNECTED);
            return false;
        }

        mBluetoothGatt = device.connectGatt(this, true, mBluetoothGattCallback);//真正的连接
        //这个方法需要三个参数：一个Context对象，自动连接（boolean值,表示只要BLE设备可用是否自动连接到它），和BluetoothGattCallback调用。
        Log.d(TAG, "尝试新的连接.");
        mConnected=true;
        return true;

    }
    BluetoothGattCallback mBluetoothGattCallback = new BluetoothGattCallback() {
        //当连接状态发生改变(有信息来)
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);
            Log.e(TAG,"onConnectionStateChange()");
            if (status==BluetoothGatt.GATT_SUCCESS){
                //连接成功
                if (newState== BluetoothGatt.STATE_CONNECTED){
                    Log.e(TAG,"连接成功");
                    //发现服务
                    gatt.discoverServices();

                }
            }else{
                //连接失败
                Log.e(TAG,"失败=="+status);
                close();

            }
        }

        //发现新服务，即调用了mBluetoothGatt.discoverServices()后，返回的数据
        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);
            if (status == BluetoothGatt.GATT_SUCCESS) {
                //真正连接成功
                mConnected = true;
                broadcastUpdate(ACTION_GATT_CONNECTED);
                //得到所有Service
                List<BluetoothGattService> supportedGattServices = gatt.getServices();
                Log.d("ga6tt",supportedGattServices.size()+"");
//                supportedGattServices.remove(0);
//                supportedGattServices.remove(2);
                for (BluetoothGattService gattService : supportedGattServices) {
                    //得到每个Service的Characteristics
                    gattCharacteristics = gattService.getCharacteristics();
                    for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
                        int charaProp = gattCharacteristic.getProperties();
                        //所有Characteristics按属性分类
                        if ((charaProp | BluetoothGattCharacteristic.PROPERTY_READ) > 0) {
                            Log.d(TAG, "gattCharacteristic的UUID为:" + gattCharacteristic.getUuid());
                            Log.d(TAG, "gattCharacteristic的属性为:  可读");
                            readUuid.add(gattCharacteristic.getUuid());
                            readCharacteristic = gattCharacteristic;
                        }
                        if ((charaProp | BluetoothGattCharacteristic.PROPERTY_WRITE) > 0) {
                            Log.d(TAG, "gattCharacteristic的UUID为:" + gattCharacteristic.getUuid());
                            Log.d(TAG, "gattCharacteristic的属性为:  可写");
                            writeUuid.add(gattCharacteristic.getUuid());
                            writeCharacteristic = gattCharacteristic;
                        }
                        if ((charaProp | BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
                            Log.d(TAG, "gattCharacteristic的UUID为:" + gattCharacteristic.getUuid() + gattCharacteristic);
                            Log.d(TAG, "gattCharacteristic的属性为:  具备通知属性");
                            notifyUuid.add(gattCharacteristic.getUuid());
                            notify_UUID_service = gattService.getUuid();

                            notify_UUID_chara = gattCharacteristic.getUuid();
                        }
                    }
                }
            }

            mBluetoothGatt.setCharacteristicNotification(mBluetoothGatt
                    .getService(notify_UUID_service).getCharacteristic(notify_UUID_chara),true);

//            mBluetoothGatt.setCharacteristicNotification(UUID.fromString("00002b13-0000-1000-8000-00805f9b34fb"),true);


        }

        //调用mBluetoothGatt.readCharacteristic(characteristic)读取数据回调，在这里面接收数据
        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicRead(gatt, characteristic, status);
        }

        //发送数据后的回调
        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
        }

        //数据产生变化
        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);
            byte bb2[] = characteristic.getValue();
            Log.d(TAG, "Slave:"+util.toHexString(bb2,true));
            if(bb2.length>5) {
                broadcastUpdate(bb2,0);//收到的消息进行广播
            }else if((bb2.length==5)){
                broadcastUpdate(util.toHexString(bb2,true),1);//广播错误码
            }
            else{
                Log.e(TAG,"长度错误!当前长度为"+bb2.length+"");
            broadcastUpdate(bb2.length+"",2);
            }

        }


        //调用mBluetoothGatt.readRemoteRssi()时的回调，rssi即信号强度
        @Override
        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {//读Rssi
            super.onReadRemoteRssi(gatt, rssi, status);
        }

    };

    public class localBinder extends Binder {
        public BLEService getService(){
            return BLEService.this;
        };
    }
    public void hello(){
        broadcastUpdate(ACTION_GET_DEVICE_NAME,"nihao");
        Log.d(TAG,"hello");
    }
    @Override
    public void onCreate() {
        super.onCreate();
        init();
        Log.d(TAG,"创建了服务");
//        //调试用
//        mHandler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                //scanLeDevice(true);
//                connect("78:DB:2F:C7:63:A8");
//            }
//        },2000);


    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }
    public void close() {
        if (mBluetoothGatt == null) {
            return;
        }
        mBluetoothGatt.close();
        mBluetoothGatt = null;
        mConnected=false;
        slaveAddress = "null";
        slaveCode=0x00;
        broadcastUpdate(ACTION_GATT_DISCONNECTED);
    }

    //读取数据
    public void readData(String data1, String data2){
        if(mConnected)
            sendDataPackage(data1,data2,true);
        else
            broadcastUpdate(ACTION_GATT_DISCONNECTED);
    }
    //发送数据
    public void writeData(String data1,String data2){
        if(mConnected)
            sendDataPackage(data1,data2,false);
        else
            broadcastUpdate(ACTION_GATT_DISCONNECTED);
    }

    public void writeData(String address,byte[] data){
        int numA = Integer.parseInt(address, 16);
        byte dataA[] = util.intToByte2(numA);
        byte send[] = {slaveCode, 0X06, dataA[0], dataA[1], data[0], data[1], 0X00, 0X00};
        byte CRC[] = util.CRC16_Check(send, 6);
        send[6] = CRC[0];
        send[7] = CRC[1];
        if(mConnected) {
            writeCharacteristic.setValue(send);
            mBluetoothGatt.writeCharacteristic(writeCharacteristic);
        } else
            broadcastUpdate(ACTION_GATT_DISCONNECTED);
    }

    //生成数据包, true为读取数据, false为写数据
    private void sendDataPackage(String data1, String data2, boolean mode){
        if(data2.length()<4)
            for(int i=0;i<(4-data2.length());i++)
                data2 = "0"+data2;
        int numA = Integer.parseInt(data1, 16);
        int numB = Integer.parseInt(data2, 16);
        byte dataA[] = util.intToByte2(numA);
        byte dataB[] = util.intToByte2(numB);
        byte send[] = {slaveCode, 0X06, 0X00, 0X00, 0X00, 0X00, 0X00, 0X00};
        if(mode)    send[1] = 0x03;
        send[2] = dataA[0];
        send[3] = dataA[1];
        send[4] = dataB[0];
        send[5] = dataB[1];
        byte CRC[] = util.CRC16_Check(send, 6);
        send[6] = CRC[0];
        send[7] = CRC[1];
        try {
            writeCharacteristic.setValue(send);
            mBluetoothGatt.writeCharacteristic(writeCharacteristic);
            Log.d(TAG,"Master:"+util.toHexString(send,true));
        } catch (Exception e) {
            Log.d("data", e.toString());
        }
    }



}
