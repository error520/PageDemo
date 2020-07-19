package com.kinco.kmlink;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import com.kinco.kmlink.BluetoothService.BleService;
import com.kinco.kmlink.ParameterItem.ParameterBean;
import com.kinco.kmlink.alertdialog.LoadingDialog;
import com.kinco.kmlink.alertdialog.PasswordDialog;
import com.kinco.kmlink.alertdialog.SetDataDialog;
import com.kinco.kmlink.utils.util;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.TimerTask;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MainViewModel extends ViewModel {
    Context context;
    PasswordDialog passwordDialog;  //密码框
    SetDataDialog setDataDialog;   //设置参数框
    String editPassword;    //用户输入的密码
    LoadingDialog loadingDialog;    //载入框
    static BleService bleService;
    List<String> deviceList = new ArrayList();
    int position;   //将要连接的设备在列表的哪一个
    private MutableLiveData<List<String>> deviceLiveData;
    private MutableLiveData<Boolean> bleScanning;
    private MutableLiveData<Boolean> bleConnected = new MutableLiveData<>();    //可能先被查询
    private MutableLiveData<Boolean> newConnected = new MutableLiveData<>();    //新连接标志位, 被设置界面读取后会清零
    private MutableLiveData<String> bleMessage = new MutableLiveData<>();     //随时可能有数据来
    private MutableLiveData<Boolean> refreshing = new MutableLiveData<>();
    private Queue<MutableLiveData<List<ParameterBean>>> queue = new LinkedList<>();
    private ParameterBean currentBean;
    RefreshThread thread;

    public void setContext(Context context) {
        this.context = context;
        Intent bleIntent = new Intent(context, BleService.class);
        context.bindService(bleIntent, connection, Context.BIND_AUTO_CREATE);
        newConnected.setValue(false);
        refreshing.setValue(false);
//        localReceiver = new LocalReceiver();
//        localBroadcastManager = LocalBroadcastManager.getInstance(context);
//        localBroadcastManager.registerReceiver(localReceiver, util.makeGattUpdateIntentFilter());
    }

    /**
     * 获取LiveData实例
     **/

    public LiveData<List<String>> getDeviceLiveData() {
        if (deviceLiveData == null) {
            deviceLiveData = new MutableLiveData<>();
            deviceLiveData.setValue(deviceList);
        }
        return deviceLiveData;
    }

    public LiveData<Boolean> getScanningFlag() {
        if (bleScanning == null) {
            bleScanning = new MutableLiveData<>();
            bleScanning.setValue(false);
        }
        return bleScanning;
    }

    public LiveData<Boolean> getConnectedFlag() {
        return bleConnected;
    }

    public LiveData<Boolean> getNewConnectedFlag(){
        return newConnected;
    }

    public LiveData<Boolean> getRefreshingFlag(){
        return refreshing;
    }

    public Boolean getRefreshing(){
        return refreshing.getValue();
    }

    public LiveData<String> getBleMessage() {
        return bleMessage;
    }

    /**
     * 功能事件
     **/

    public void addDevice(String device) {
        deviceList.add(device);
        deviceLiveData.setValue(deviceList);
    }

    public void clearDevice() {
        deviceList.clear();
        deviceLiveData.setValue(deviceList);
    }

    public void startScanning() {
        clearDevice();
        bleScanning.setValue(true);
        if (bleService != null) {
            bleService.scanLeDevice(5000);
        }
    }

    public void stopScanning() {
        bleScanning.setValue(false);
        if (bleService != null) {
            bleService.stopLeScan();
        }
    }

    public void onSearchCompleted() {
        bleScanning.setValue(false);
    }

    public void connectDevice(String address, int position) {
        if (bleService != null) {
            this.position = position;
            bleService.connect(address);
        }
    }

    public void verifyPassword() {
        passwordDialog = new PasswordDialog(context);
        passwordDialog.setOnClickBottomListener(new PasswordDialog.OnClickBottomListener() {
            @Override
            public void onPositiveClick() {
                editPassword = passwordDialog.getPassword();
                bleService.readData("0000", "0001");
            }

            @Override
            public void onNegativeClick() {
                if (bleConnected.getValue()) {
                    bleService.close();
                }
                passwordDialog.gone();
                passwordDialog = null;
            }
        });
    }

    public void disconnectCurrent() {
        if (bleService != null) {
            bleService.close();
        }
    }

    public String onConnected() {
        String name = deviceList.get(position).split("\n")[0];
        bleConnected.setValue(true);
        verifyPassword();
        return name;
    }

    public void onDisconnected() {
        bleConnected.setValue(false);
    }

    public void set(){
        newConnected.setValue(true);
    }

    /**
     * 解析蓝牙传过来的数据
     */
    public void parseData(byte[] message) {
        //验证密码
        if (passwordDialog != null) {
            byte[] bytesPassword = new byte[]{message[3], message[4]};
            String password = util.toHexString(bytesPassword, false);
            if (editPassword.equals(password)) {
                passwordDialog.gone();
                passwordDialog = null;
                util.centerToast(context, context.getString(R.string.password_correct), 0);
                deviceList.remove(position);    //从搜索列表中移除
                deviceLiveData.setValue(deviceList);
            } else {
                util.centerToast(context, context.getString(R.string.password_wrong), 0);
            }
        } else {  //不是在验证密码
            if (message[1] == 0x06) {
                if (currentBean != null) {
                    String result = util.setParameterByMessage(message,currentBean);
                    bleMessage.setValue(result);
//                    util.centerToast(context,context.getString(R.string.succeed),0);
                }
            } else if (message[1] == 0x03) {
                if (thread != null) {
                    thread.message = message;   //传入线程内进行解析
                    synchronized (thread){
                        thread.notify();
                    }
                }
            }

        }

    }

    public void writeData(ParameterBean bean) {
        if (bleService != null) {
            int type = bean.getType();
            if(type<2){
                currentBean = bean;
                String data = Integer.toHexString(Integer.parseInt(bean.getCurrentValue().split(":")[0]));    //转成16进制字符串
                bleService.writeData(bean.getAddress(), data);
            }else if(type==2){
                byte[] data = util.toByteData(bean.getCurrentValue(),bean.getAccuracy());
                bleService.writeData(bean.getAddress(),data);
            }else if(type==3){
                double temp = Double.parseDouble(bean.getCurrentValue())+Double.parseDouble(bean.getRangeHint().split("~")[1]);
                byte[] data = util.toByteData(String.valueOf(temp),bean.getAccuracy());
                bleService.writeData(bean.getAddress(),data);
            }
        }
    }

    public void writeData(ParameterBean bean, String data){
        int type = bean.getType();
        if(type==2){

        }
    }

    public void readData(ParameterBean bean) {
        if (bleService != null) {
            currentBean = bean;
            bleService.readData(bean.getAddress(), "0001");
        }
    }

    public void showSetDataDialog(ParameterBean bean) {
        List<String> description = bean.getDescription();
        double accuracy = Double.parseDouble(description.get(0));    //最小单位
        String unit = description.get(1);
        String[] rawHint = description.get(2).split("~");
        String hint = bean.getRangeHint();
        double small = Double.parseDouble(hint.split("~")[0]);
        double big = Double.parseDouble(hint.split("~")[1]);
        setDataDialog = new SetDataDialog(context, bean.getName(),
                unit, hint, bean.getDefaultValue(), bean.getCurrentValue());
        setDataDialog.setOnClickBottomListener(new SetDataDialog.OnClickBottomListener() {
            @Override
            public void onPositiveClick() {
                double setData = Double.parseDouble(setDataDialog.getSetData());
                if (setData < small || setData > big) {
                    util.centerToast(context, context.getString(R.string.data_out_of_range), 0);
                } else {
                    byte data[] = util.toByteData(setDataDialog.getSetData(), accuracy);
                    currentBean = bean;
                    bean.setCurrentValue(setDataDialog.getSetData());
                    writeData(bean);
//                    bleService.writeData(bean.getAddress(), data);
                }
            }

            @Override
            public void onNegativeClick() {
                setDataDialog.gone();
            }
        });
    }

    public void showLoadingDialog(){
        loadingDialog = new LoadingDialog(context,"",context.getString(R.string.loading),true);
        loadingDialog.setOnClickCancelListener(()->{
            if(thread!=null){
                thread.interrupt();
            }
        });
    }

    //定时清理连接
    void clearDevicesInTime() {

    }

    public static BleService getBleService() {
        if (bleService != null) {
            return bleService;
        } else {
            return null;
        }
    }

    public void addToQueue(MutableLiveData<List<ParameterBean>> parameterLiveData) {
        queue.offer(parameterLiveData);
    }

    public boolean startRefreshThread(int mode) {
        if (refreshing.getValue() == false) {
            if(!bleConnected.getValue()){
                util.centerToast(context,context.getString(R.string.device_disconnected),0);
                return false;   //蓝牙未连接
            }
            util.centerToast(context,"开始接收",0);
            refreshing.setValue(true);
//            queue.clear();
            thread = new RefreshThread(mode);
            thread.start();
            return true;
        } else {
            util.centerToast(context,"停止接收",0);
            refreshing.setValue(false);
            if(thread!=null){
                thread.interrupt();
            }
            queue.clear();  //停止时会清空队列里还未请求的参数
            return false;
        }
    }

    public void stopRefreshThread(){
        if(refreshing.getValue()){
            util.centerToast(context,"停止接收",0);
        }
        refreshing.setValue(false);
        if(thread!=null){
            thread.interrupt();
        }
        queue.clear();  //停止时会清空队列里还未请求的参数
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        deviceList = null;
//        util.centerToast(context,"onClear()触发",0);
    }

    //获取服务
    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            bleService = ((BleService.localBinder) service).getService();
            bleConnected.setValue(bleService.mConnected);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    private class RefreshThread extends Thread {
        byte[] message;
        int mode;   //0为单次模式, 1为持续刷新模式
        RefreshThread(int mode){
            this.mode = mode;
        }
        @Override
        public void run() {
            super.run();
            if(mode==0){
                ((Activity)context).runOnUiThread(()->{
                    showLoadingDialog();
                });
            }
            while (refreshing.getValue()) {
                if (!queue.isEmpty()) {
                    MutableLiveData<List<ParameterBean>> liveData = queue.poll();
                    List<ParameterBean> list = liveData.getValue();
                    try {
                        for (int i=0; i<list.size(); i++) {
                            ParameterBean parameter = list.get(i);
                            readData(parameter);
                            synchronized (this) {
                                this.wait(2000); //阻塞直到数据刷新上来
                                if (message != null) {
                                    if(parameter.getResourceId().contains("B0_05")){
                                        util.setWordParameterByMessage(message, new ArrayList<>(list.subList(i,list.size())));  //接下来的全是字类型的参数
                                        break;  //跳出当前列表遍历, 因为把接下来的字类型参数全解析完了
                                    }else{
                                        util.setParameterByMessage(message,parameter);
                                    }
                                } else {    //超时未等到消息
                                    ((Activity)context).runOnUiThread(()->{
                                        util.centerToast(context, "等待超时",0);
                                    });
                                }
                                message = null; //等待下一次信息更新
                                this.sleep(BleService.reloadGap);   //每条读取之间的间隔
                            }
                        }
                        liveData.postValue(list);
                        Log.d("child","完成更新一组参数");
                        this.sleep(200);    //每组之间间隔
                    } catch (InterruptedException e) {
                        Log.d("child","读取线程被中断");
                        liveData.postValue(list);   //更新已经读取到的数据
                    }
                    if(mode==0){
                        refreshing.postValue(false); //单次模式, 只需更新完一组参数即可
                        if(loadingDialog!=null){
                            ((Activity)context).runOnUiThread(()->{
                                Log.d("child","刷新完了");
                                loadingDialog.gone();
                            });
                        }
                        loadingDialog.gone();
                    }
                }
            }
        }
    }

    interface onParameterRead {
        void onRead();
    }

    class task extends TimerTask {
        @Override
        public void run() {
            synchronized (thread) {
                thread.message = new byte[]{0x66,0x77};
                thread.notify();
            }
        }
    }

}
