package com.kinco.kmlink.ui.firstpage;

import android.util.Log;

import com.kinco.kmlink.EventBusUtils.BleDataEvent;
import com.kinco.kmlink.EventBusUtils.RequestEvent;
import com.kinco.kmlink.ParameterItem.ParameterBean;
import com.kinco.kmlink.utils.PrefUtil;
import com.kinco.kmlink.utils.util;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.sql.ParameterMetaData;
import java.util.List;

public class InitialThread extends Thread {
    private byte[] message;
    private List<ParameterBean> beanList;
    private int timeoutCount = 0;
    private OnFinishedListener listener;

    InitialThread(List<ParameterBean> beanList, OnFinishedListener listener) {
        this.beanList = beanList;
        this.listener = listener;
        EventBus.getDefault().register(this);
    }

    @Override
    public void run() {
        super.run();
        Boolean timeoutFlag = false;
        for (ParameterBean bean : beanList) {
            String[] request = {"read", bean.getAddress(), "0001"};
            EventBus.getDefault().post(new RequestEvent(request));
            try {
                synchronized (this) {
                    wait(PrefUtil.timeoutWaiting);
                    if (message != null) {
                        util.setParameterByMessage(message, bean);
                        bean.setDefaultValue(bean.getCurrentValue());
                        message = null;
                    } else {
                        timeoutCount++;
                        if (timeoutCount >= 3) {
                            timeoutFlag = true;
                            break;
                        }
                    }
                }
                sleep(PrefUtil.bleGap);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        listener.onFinished(timeoutFlag);
    }

    @Subscribe
    public void onNewBleData(BleDataEvent event) {
        message = event.getBleData();
        synchronized (this) {
            notify();
        }
    }

    interface OnFinishedListener {
        void onFinished(Boolean timeout);
    }

}
