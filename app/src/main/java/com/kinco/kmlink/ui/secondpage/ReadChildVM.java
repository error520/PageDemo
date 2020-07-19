package com.kinco.kmlink.ui.secondpage;

import android.app.Activity;
import android.app.Application;
import android.util.Log;

import com.kinco.kmlink.BluetoothService.BleService;
import com.kinco.kmlink.EventBusUtils.BleDataEvent;
import com.kinco.kmlink.EventBusUtils.MessageEvent;
import com.kinco.kmlink.EventBusUtils.RequestEvent;
import com.kinco.kmlink.ParameterItem.ParameterBean;
import com.kinco.kmlink.utils.util;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ReadChildVM extends ViewModel {
    private RefreshThread thread;
    MutableLiveData<Boolean> refreshing;
    private int timeoutCount = 0;
    private Queue<MutableLiveData<List<ParameterBean>>> queue = new LinkedList<>();

    public void init(){
        EventBus.getDefault().register(this);
    }

    @Subscribe
    public void onNewBleData(BleDataEvent event){
        if(thread!=null){
            thread.message = event.getBleData();
            synchronized (thread){
                thread.notify();
            }
        }
    }

    public LiveData<Boolean> getRefreshing(){
        if(refreshing == null){
            refreshing = new MutableLiveData<>();
            refreshing.setValue(false);
        }
        return refreshing;
    }

    public Boolean startRefresh(){
        if(BleService.isConnected.getValue()){
            EventBus.getDefault().post(new MessageEvent("start thread"));
            refreshing.setValue(true);
            thread = new RefreshThread();
            thread.start();
            return true;
        }else {
            return false;
        }
    }

    public void stopRefresh(){
        if(refreshing.getValue()){
            EventBus.getDefault().post(new MessageEvent("stop thread"));
            refreshing.postValue(false);
            if(thread!=null){
                thread.interrupt();
            }
            queue.clear();  //停止时会清空队列里还未请求的参数
        }
    }

    public void addToQueue(MutableLiveData<List<ParameterBean>> parameterLiveData) {
        queue.offer(parameterLiveData);
    }

    private class RefreshThread extends Thread {
        byte[] message;
        @Override
        public void run() {
            super.run();
            while (refreshing.getValue()) {
                if (!queue.isEmpty()) {
                    MutableLiveData<List<ParameterBean>> liveData = queue.poll();
                    List<ParameterBean> list = liveData.getValue();
                    try {
                        for (int i = 0; i<list.size(); i++) {
                            ParameterBean parameter = list.get(i);
                            String[] request = {parameter.getAddress(),"0001","read"};
                            EventBus.getDefault().post(new RequestEvent(request));
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
                                    timeoutCount++;
                                    if(timeoutCount>3){
                                        BleService.isConnected.getValue();
                                        timeoutCount = 0;
                                        stopRefresh();
                                    }
                                    EventBus.getDefault().post(new MessageEvent("timeout"));
                                }
                                message = null; //等待下一次信息更新
                                sleep(BleService.reloadGap);   //每条读取之间的间隔
                            }
                        }
                        liveData.postValue(list);
                        Log.d("child","完成更新一组参数");
                        sleep(200);    //每组之间间隔
                    } catch (InterruptedException e) {
                        Log.d("child","读取线程被中断");
                        liveData.postValue(list);   //更新已经读取到的数据
                    }
                }
            }
        }
    }
}
