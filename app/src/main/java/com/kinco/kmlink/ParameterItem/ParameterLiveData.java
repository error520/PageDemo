package com.kinco.kmlink.ParameterItem;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import androidx.lifecycle.LiveData;

public class ParameterLiveData extends LiveData<List<ParameterBean>> {
    @Override
    protected void onActive() {
        super.onActive();
//        EventBus.getDefault().register();
    }
}
