package com.kinco.kmlink.ui.readpage.parameterpage;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.kinco.kmlink.BluetoothService.BleService;
import com.kinco.kmlink.R;
import com.kinco.kmlink.sys.BaseSTActivity;
import com.kinco.kmlink.utils.util;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;

public class ReadChildActivity extends BaseSTActivity {
    ReadPagerAdapter adapter;
    ViewPager viewPager;
    ReadChildVM viewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_child);
        viewModel = new ViewModelProvider(this).get(ReadChildVM.class);
        adapter = new ReadPagerAdapter(this,getSupportFragmentManager(),1);
        viewPager = findViewById(R.id.viewpager);
        viewPager.setAdapter(adapter);
        TabLayout tabs = findViewById(R.id.tabLayout);
        tabs.setupWithViewPager(viewPager);
        viewPager.setCurrentItem(getIntent().getIntExtra("index",0));
        FloatingActionButton fab = findViewById(R.id.fab_refresh);
        BleService.isConnected.observe(this, flag->{
            if(!flag&&viewModel.refreshing){
                viewModel.stopRefresh();
                util.centerToast(this,getString(R.string.device_disconnected),0);
            }
        });
        fab.setOnClickListener(v->{
            if(!viewModel.refreshing){
                if(BleService.isConnected.getValue()){
                    adapter.currentFragment.startFirstRequest();    //开启线程并发送第一次请求
                    viewModel.startRefresh();
                    util.centerToast(this,getString(R.string.start_receiving),0);
                }else{
                    util.centerToast(this,getString(R.string.device_disconnected),0);
                }
            }else{
                viewModel.stopRefresh();
                util.centerToast(this,getString(R.string.stop_receiving),0);
            }
        });
        //线程回调
        viewModel.init(new ReadChildVM.RefreshCallback() {
            @Override
            public void onToast(String toast) {
                runOnUiThread(()->{
                    util.centerToast(ReadChildActivity.this,toast,0);
                });
            }
            @Override
            public void onStateChange(boolean refreshing) {
                runOnUiThread(()->{
                    fab.setActivated(refreshing);
                });
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
//        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        int index = intent.getIntExtra("index",0);
        if(viewPager!=null){
            viewPager.setCurrentItem(index);
        }
    }

    @Override
    protected void backToMain() {
        super.backToMain();
        if(viewModel.refreshing){
            viewModel.stopRefresh();
        }
    }
}
