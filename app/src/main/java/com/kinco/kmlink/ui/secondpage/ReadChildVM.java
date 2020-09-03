package com.kinco.kmlink.ui.secondpage;

import android.os.AsyncTask;
import android.util.Log;

import com.kinco.kmlink.BluetoothService.BleService;
import com.kinco.kmlink.EventBusUtils.BleDataEvent;
import com.kinco.kmlink.EventBusUtils.MessageEvent;
import com.kinco.kmlink.EventBusUtils.RequestEvent;
import com.kinco.kmlink.ParameterItem.ParameterBean;
import com.kinco.kmlink.R;
import com.kinco.kmlink.sys.SysApplication;
import com.kinco.kmlink.utils.PrefUtil;
import com.kinco.kmlink.utils.util;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ReadChildVM extends ViewModel {
    private RefreshThread thread;
    private Queue<MutableLiveData<List<ParameterBean>>> queue = new LinkedList<>();
    public boolean refreshing = false;
    private RefreshCallback callback;

    public void init(RefreshCallback callback){
        EventBus.getDefault().register(this);
        this.callback = callback;
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

    public void startRefresh(){
        refreshing = true;
        thread = new RefreshThread();
        thread.start();
    }

    public void stopRefresh(){
        if(refreshing){
            refreshing = false;
            if(thread!=null){
                thread.interrupt();
            }
            queue.clear();  //停止时会清空队列里还未请求的参数
        }
    }

    public void addToQueue(MutableLiveData<List<ParameterBean>> parameterLiveData) {
        queue.offer(parameterLiveData);
    }

//   private class RefreshTask extends AsyncTask<Void, Integer, Void>{
//
//       @Override
//       protected Void doInBackground(Void...params) {
//           while(!isCancelled()){
//               if(){
//
//               }
//           }
//           return null;
//       }
//
//       @Override
//       protected void onProgressUpdate(Integer... values) {
//           super.onProgressUpdate(values);
//       }
//
//
//   }

    private class RefreshThread extends Thread {
        byte[] message;
        int timeoutCount = 0;
        @Override
        public void run() {
            super.run();
            if(callback!=null){
                callback.onStateChange(refreshing);
            }
            while (refreshing) {
                if (!queue.isEmpty()) {
                    MutableLiveData<List<ParameterBean>> liveData = queue.poll();
                    List<ParameterBean> list = liveData.getValue();
                    try {
                        //遍历一页参数列表中的参数
                        for (int i = 0; i<list.size(); i++) {
                            ParameterBean parameter = list.get(i);
                            String[] request = {"read",parameter.getAddress(),"0001"};
                            EventBus.getDefault().post(new RequestEvent(request));
                            synchronized (this) {
                                this.wait(PrefUtil.timeoutWaiting); //阻塞直到数据刷新上来
                                if (message != null) {
                                    if(parameter.getResourceId().contains("B0_05")){
                                        util.setWordParameterByMessage(message, new ArrayList<>(list.subList(i,list.size())));  //接下来的全是字类型的参数
                                        break;  //跳出当前列表遍历, 因为把接下来的字类型参数全解析完了
                                    }else{
                                        util.setParameterByMessage(message,parameter);
                                    }
                                    if(timeoutCount>0){
                                        timeoutCount--;     //消除前面超时影响
                                    }
                                } else {    //超时未等到消息
                                    timeoutCount++;
                                    if(timeoutCount>3){
                                        stopRefresh();
                                    }else{
                                        if(callback!=null){
                                            callback.onToast(SysApplication.getContext().getString(R.string.timeout));
                                        }
                                    }
                                }
                                message = null; //等待下一次信息更新
                                sleep(PrefUtil.bleGap);   //每条读取之间的间隔
                            }
                        }
                        liveData.postValue(list);
                        Log.d("child","完成更新一组参数");
                        sleep(200);    //每组之间间隔
                    } catch (InterruptedException e) {
                        Log.d("child","读取线程被中断");
                        liveData.postValue(list);   //更新已经读取到的数据
                        if(callback!=null){
                            callback.onStateChange(false);
                        }
                    }
                }
            }
            Log.d("july18","线程结束");
            if(callback!=null){
                callback.onStateChange(false);
            }
        }
    }

    interface RefreshCallback{
        void onToast(String toast);
        void onStateChange(boolean refreshing);
    }

}
