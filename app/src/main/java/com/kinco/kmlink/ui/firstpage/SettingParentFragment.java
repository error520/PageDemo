package com.kinco.kmlink.ui.firstpage;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayout;
import com.kinco.kmlink.MainViewModel;
import com.kinco.kmlink.R;
import com.kinco.kmlink.sys.MyFragment;
import com.kinco.kmlink.utils.util;

import java.util.Timer;
import java.util.TimerTask;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;

public class SettingParentFragment extends MyFragment {
    SettingPagerAdapter adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MainViewModel viewModel = new ViewModelProvider(getActivity()).get(MainViewModel.class);
        MutableLiveData<Boolean> newConnectedLiveData = (MutableLiveData<Boolean>) viewModel.getNewConnectedFlag();
        newConnectedLiveData.observe(this,newConnected->{
            if(newConnected==true){
                adapter.enableChildInitial();   //让子片段自己去更新
                newConnectedLiveData.setValue(false);
            }
        });
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_set_parent,container,false);
        ViewPager viewPager = layout.findViewById(R.id.viewpager);
        adapter = new SettingPagerAdapter(getContext(),getActivity().getSupportFragmentManager(),1);
        viewPager.setAdapter(adapter);
        TabLayout tabs = layout.findViewById(R.id.tabLayout);
        tabs.setupWithViewPager(viewPager);
        return layout;
    }

    @Override
    public void onResume() {
        super.onResume();


    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopAllChildren();
//        util.centerToast(getContext(),"父片段被onPause",0);
    }
}
