package com.kinco.kmlink.BluetoothService;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
//import androidx.localbroadcastmanager.content;
import androidx.lifecycle.MutableLiveData;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.kinco.kmlink.EventBusUtils.BleDataEvent;
import com.kinco.kmlink.EventBusUtils.MessageEvent;
import com.kinco.kmlink.EventBusUtils.RequestEvent;
import com.kinco.kmlink.R;
import com.kinco.kmlink.utils.util;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;


public class BleService extends Service {
    private final String TAG = "BLEService";
    private BluetoothAdapter mBtAdapter;
    private Timer timer = new Timer();
    private ArrayList<String> device_list = new ArrayList<String>();
    private Map<String,String> deviceMap = new HashMap<>();
    private LocalBroadcastManager localBroadcastManager;
    public static String namePattern = "KINCO_\\d+";
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
    public static byte slaveCode = 0x00;
    private final  IBinder mBinder = new localBinder();
    private BluetoothGatt mBluetoothGatt;
    private List<BluetoothGattCharacteristic> gattCharacteristics;
    private List<UUID> readUuid = new ArrayList<UUID>();
    private List<UUID> writeUuid = new ArrayList<UUID>();
    private List<BluetoothGattCharacteristic> writeCList = new ArrayList<BluetoothGattCharacteristic>();
    private List<BluetoothGattCharacteristic> notifyCList = new ArrayList<BluetoothGattCharacteristic>();
    private List<UUID> notifyUuid = new ArrayList<UUID>();
    private UUID notify_UUID_service;
    private UUID notify_UUID_chara;
    private BluetoothGattCharacteristic readCharacteristic;
    private BluetoothGattCharacteristic writeCharacteristic;
    Handler handler = new Handler(Looper.getMainLooper());

    //记录蓝牙通信日志
    public static List<String> BLELog = new ArrayList<>();

    //刷新间隔
    public static int reloadGap = 50;

    public static MutableLiveData<Boolean> isConnected = new MutableLiveData<>();
    public static MutableLiveData<String> deviceName = new MutableLiveData<>();
    public static boolean newConnection = false;    //是否是新连接的设备(需要初始化参数)
    public static boolean varified = false;     //是否通过密码验证

    public void init(){
        isConnected.setValue(false);
        deviceName.setValue(getString(R.string.unconnected));
        localBroadcastManager = LocalBroadcastManager.getInstance(this);
        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBtAdapter = bluetoothManager.getAdapter();
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Log.d("BLEService","不支持BLE!");
            return;
        }
        if (mBtAdapter == null || !mBtAdapter.isEnabled()) {
              mBtAdapter.enable();//强制打开蓝牙
//            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//            activity.startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
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

    /**
     *
     * @param period    扫描周期(milli)
     */
    public void scanLeDevice(int period) {
        try {
            device_list.clear();
            deviceMap.clear();
            if(timer == null){
                timer = new Timer();
            }
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    mScanning = false;
                    mBtAdapter.stopLeScan(mLeScanCallback);
                    Log.d(TAG,"扫描完成");
                    EventBus.getDefault().post(new MessageEvent("search completed"));
                    broadcastUpdate(ACTION_SEARCH_COMPLETED);
                }
            },period);
            mScanning = true;
            mBtAdapter.startLeScan(mLeScanCallback); //开始搜索
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    public void stopLeScan(){
        if (mScanning){
            mBtAdapter.stopLeScan(mLeScanCallback);
            if(timer!=null){
                timer.cancel();
                timer = null;
            }
        }
    }

    private BluetoothAdapter.LeScanCallback mLeScanCallback = (device, rssi, scanRecord) -> {
            if(!Objects.isNull(device.getName())){  //有名字才加入
                if(!deviceMap.containsKey(device.getName())){   //避免重复添加
                    deviceMap.put(device.getName(),device.getAddress());
                    broadcastUpdate(ACTION_GET_DEVICE_NAME,device.getName()+"\n" +
                            device.getAddress());
                    EventBus.getDefault().post(new MessageEvent("d:"+device.getName()+"\n" +
                            device.getAddress()));  //发送设备信息
                    Log.d(TAG,device.getName()+"\n"+device.getAddress());   //打印日志
                }
            }

    };

    public void connect(final String address) {//4
        if(deviceMap.containsValue(address)){
            for(String name:deviceMap.keySet()){
                if(deviceMap.get(name).equals(address)){
                    try{
                        String code = name.substring(name.indexOf("_")+1);
                        slaveCode = util.intToByte2(Integer.parseInt(code))[1];
                    }catch (Exception e){
                        String text = getResources().getString(R.string.connection_failed);
                        util.centerToast(this,text,0);
                        return;   //获取从站地址失败, 返回连接失败
                    }
                    break;
                }
            }
        }
        //如果当前有设备连接, 关闭这个连接
        if(mBluetoothGatt!=null)
             close();
        if (mBtAdapter == null || address == null) {
            Log.d(TAG,"BluetoothAdapter不能初始化 or 未知 address.");
//            Toast.makeText(this,"未能连接",Toast.LENGTH_LONG).show();
            EventBus.getDefault().post(new MessageEvent("connect failed"));
//            broadcastUpdate(ACTION_GATT_DISCONNECTED);
            return;
        }

        final BluetoothDevice device = mBtAdapter
                .getRemoteDevice(address);
        if (device == null) {
            EventBus.getDefault().post(new MessageEvent("connect failed"));
//            String text = getResources().getString(R.string.device_out_of_range);
//            util.centerToast(this,text,0);
            Log.d("data", "设备没找到，不能连接");
//            broadcastUpdate(ACTION_GATT_DISCONNECTED);
            return;
        }

        mBluetoothGatt = device.connectGatt(this, false, connectCallback);//真正的连接
        //这个方法需要三个参数：一个Context对象，自动连接（boolean值,表示只要BLE设备可用是否自动连接到它），和BluetoothGattCallback调用。
        Log.d(TAG, "尝试新的连接:"+address);

    }

    /**
     * 连接回调
     */
    BluetoothGattCallback connectCallback = new BluetoothGattCallback() {
        //当连接状态发生改变(有信息来)
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);
            Log.e(TAG,"onConnectionStateChange()");
            Log.e(TAG,"status:"+status);
            Log.e(TAG,"newState:"+newState);
            if (status==BluetoothGatt.GATT_SUCCESS){
                //连接成功
                if (newState== BluetoothGatt.STATE_CONNECTED){
                    Log.e(TAG,"连接成功");
                    //发现服务
                    gatt.discoverServices();
                }
            }else {
                //连接失败
                Log.e(TAG,"失败=="+status);
                EventBus.getDefault().post(new MessageEvent("connect failed"));
                close();
            }
        }

        //发现新服务，即调用了mBluetoothGatt.discoverServices()后，返回的数据
        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);
            if (status == BluetoothGatt.GATT_SUCCESS) {
                //真正连接成功
//                FirstpageFragment.initializing = true;//让碎片1初始化
//                FirstMoreActivity.initializing = true;//让二级界面初始化
                //mNewConnection = true;
                broadcastUpdate(ACTION_GATT_CONNECTED);
                //得到所有Service
                List<BluetoothGattService> supportedGattServices = gatt.getServices();
                Log.d("ga6tt",supportedGattServices.size()+"");
                int index=1;
                writeCList.clear();
                notifyCList.clear();
                for (BluetoothGattService gattService : supportedGattServices) {
                    //得到每个Service的Characteristics
                    Log.d(TAG,"第"+index+"组服务:");
                    gattCharacteristics = gattService.getCharacteristics();
                    for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
                        int charaProp = gattCharacteristic.getProperties();
                        //所有Characteristics按属性分类
                        if ((charaProp | BluetoothGattCharacteristic.PROPERTY_READ) > 0) {
//                            Log.d(TAG, "gattCharacteristic的UUID为:" + gattCharacteristic.getUuid());
//                            Log.d(TAG, "gattCharacteristic的属性为:  可读");
                            readUuid.add(gattCharacteristic.getUuid());
                            readCharacteristic = gattCharacteristic;
                        }
                        if ((charaProp | BluetoothGattCharacteristic.PROPERTY_WRITE) > 0) {
//                            Log.d(TAG, "gattCharacteristic的UUID为:" + gattCharacteristic.getUuid());
//                            Log.d(TAG, "gattCharacteristic的属性为:  可写");
                            //writeUuid.add(gattCharacteristic.getUuid());
                            writeCList.add(gattCharacteristic);
                            writeCharacteristic = gattCharacteristic;
                        }
                        if ((charaProp | BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
//                            Log.d(TAG, "gattCharacteristic的UUID为:" + gattCharacteristic.getUuid() + gattCharacteristic);
//                            Log.d(TAG, "gattCharacteristic的属性为:  具备通知属性");
                            notifyCList.add(gattCharacteristic);

                            notifyUuid.add(gattCharacteristic.getUuid());
                            notify_UUID_service = gattService.getUuid();
                            notify_UUID_chara = gattCharacteristic.getUuid();
                        }

                    }index++;
                }
            }
            boolean result = mBluetoothGatt.setCharacteristicNotification(notifyCList.get(2),true);
            if(result) {
                List<BluetoothGattDescriptor> descriptorList = notifyCList.get(2).getDescriptors();
                if(descriptorList != null && descriptorList.size() > 0) {
                    for(BluetoothGattDescriptor descriptor : descriptorList) {
                        descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                        mBluetoothGatt.writeDescriptor(descriptor);
                    }
                }
            }

            writeCharacteristic = writeCList.get(3);
            Log.d(TAG,"write:"+writeCharacteristic.getUuid());

            Log.d(TAG,"notify result:"+notifyCList.get(2).getUuid()+","+result);
//            mBluetoothGatt.setCharacteristicNotification(mBluetoothGatt
//                    .getService(notify_UUID_service).getCharacteristic(notify_UUID_chara),true);


            Log.d(TAG,"写特征的信息如下");
            for(int i=0;i<writeCList.size();i++) {
                Log.d(TAG, writeCList.get(i).getUuid() + ":");
            }
            Log.d(TAG,"通知的信息如下");
            for(int i=0;i<notifyCList.size();i++) {
                Log.d(TAG, notifyCList.get(i).getUuid() + ":");
            }
            //真正连接成功
            isConnected.postValue(true);
            deviceName.postValue(gatt.getDevice().getName());
            newConnection = true;
            BLELog.clear();
//            slaveCode = util.intToByte2(Integer.valueOf(slaveAddress.substring(slaveAddress.indexOf("_") + 1, slaveAddress.indexOf("\n"))))[1];
            //mBluetoothGatt.setCharacteristicNotification(UUID.fromString("00002b13-0000-1000-8000-00805f9b34fb"),true);

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
            if(bb2.length>5) {
                broadcastUpdate(bb2,0);//收到的消息进行广播
                EventBus.getDefault().post(new BleDataEvent(bb2));
            }else if((bb2.length==5)){
                broadcastUpdate(util.toHexString(bb2,true),1);//广播错误码
                EventBus.getDefault().post(new BleDataEvent(bb2));
            }
            else{
                Log.e(TAG,"长度错误!当前长度为"+bb2.length+"");
                broadcastUpdate(bb2.length+"",2);
            }
            String logText = "Slave: "+util.toHexString(bb2,true);
            BLELog.add(logText);
            Log.d(TAG, logText);

        }


        //调用mBluetoothGatt.readRemoteRssi()时的回调，rssi即信号强度
        @Override
        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {//读Rssi
            super.onReadRemoteRssi(gatt, rssi, status);
        }



    };

    public class localBinder extends Binder {
        public BleService getService(){
            return BleService.this;
        };
    }
    @Override
    public void onCreate() {
        super.onCreate();
        init();
        EventBus.getDefault().register(this);
        Log.d(TAG,"创建了服务");
    }

    @Subscribe
    public void onNewRequestEvent(RequestEvent event){
        String[] request = event.getRequest();
        String function = request[0];
        switch (function) {
            case "read":
                readData(request[1], request[2]);
                break;
            case "write":
                writeData(request[1], request[2]);
                break;
            case "connect":
                connect(request[1]);
                break;
            case "disconnect":
                close();
                break;
            case "scan":
                scanLeDevice(Integer.parseInt(request[1]));
                break;
            case "stop scan":
                stopLeScan();
                break;
            case "send bytes":
                byte[] data = util.hexStringToBytes(request[1]);
                sendRawBytes(data,Boolean.parseBoolean(request[2]));
                break;
        }
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
        isConnected.postValue(false);
        deviceName.postValue(getString(R.string.unconnected));
        varified = false;
        slaveCode=0x00;
        broadcastUpdate(ACTION_GATT_DISCONNECTED);
    }


    //读取数据
    public void readData(String data1, String data2){
        if(isConnected.getValue())
            sendDataPackage(data1,data2,true);
        else{
            broadcastUpdate(ACTION_GATT_DISCONNECTED);
            EventBus.getDefault().post(new MessageEvent(getString(R.string.device_disconnected)));
        }
    }
    //发送数据
    public void writeData(String data1,String data2){
        if(isConnected.getValue())
            sendDataPackage(data1,data2,false);
        else{
            broadcastUpdate(ACTION_GATT_DISCONNECTED);
            EventBus.getDefault().post(new MessageEvent("device disconnected"));
        }
    }

    /**
     * 传入字节数组的写数据方式
     * @param address
     * @param data
     */
    public void writeData(String address,byte[] data){
        int numA = Integer.parseInt(address, 16);
        byte dataA[] = util.intToByte2(numA);
        byte send[] = {slaveCode, 0X06, dataA[0], dataA[1], data[0], data[1], 0X00, 0X00};
        byte CRC[] = util.CRC16_Check(send, 6);
        send[6] = CRC[0];
        send[7] = CRC[1];
        if(isConnected.getValue()) {
            writeCharacteristic.setValue(send);
            mBluetoothGatt.writeCharacteristic(writeCharacteristic);
            String logText = "Master: "+util.toHexString(send,true);
            BLELog.add(logText);
            Log.d(TAG,logText);
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
        byte[] dataA = util.intToByte2(numA);
        byte[] dataB = util.intToByte2(numB);
        byte[] send = {slaveCode, 0X06, 0X00, 0X00, 0X00, 0X00, 0X00, 0X00};
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
            String logText = "Master: "+util.toHexString(send,true);
            BLELog.add(logText);
            Log.d(TAG,logText);
        } catch (Exception e) {
            Log.d("data", e.toString());
        }
    }

    private void sendRawBytes(byte[] data, boolean addCRC){
        byte[] finalData = data;
        if(addCRC){
            finalData = Arrays.copyOf(data,data.length+2);
            byte[] crc = util.CRC16_Check(data,data.length);
            finalData[data.length] = crc[0];
            finalData[data.length+1] = crc[1];
        }
        writeCharacteristic.setValue(finalData);
        mBluetoothGatt.writeCharacteristic(writeCharacteristic);
        String logText = "Master: "+util.toHexString(finalData,true);
        BLELog.add(logText);
        Log.d(TAG,logText);
    }



}
