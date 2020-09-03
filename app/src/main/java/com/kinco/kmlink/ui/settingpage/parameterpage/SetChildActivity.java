package com.kinco.kmlink.ui.settingpage.parameterpage;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.android.material.tabs.TabLayout;
import com.kinco.kmlink.BluetoothService.BleService;
import com.kinco.kmlink.R;
import com.kinco.kmlink.sys.BaseSTActivity;

import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

/**
 * 负责加载子片段
 */
public class SetChildActivity extends BaseSTActivity {
    SettingPagerAdapter adapter;
    ViewPager viewPager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_child);
        adapter = new SettingPagerAdapter(this, getSupportFragmentManager(), 1);
        viewPager = findViewById(R.id.viewpager);
        viewPager.setAdapter(adapter);
        TabLayout tabs = findViewById(R.id.tabLayout);
        tabs.setupWithViewPager(viewPager);
        viewPager.setCurrentItem(getIntent().getIntExtra("index", 0));
    }

    //进入时切换到相应界面
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        int index = intent.getIntExtra("index", 0);
        if (viewPager != null) {
            viewPager.setCurrentItem(index);
        }
        if(BleService.isConnected.getValue() && BleService.newConnection){
            adapter.enableChildInitial();
            Log.d("july18","我已让子片段都非初始化");
            BleService.newConnection = false;
        }
    }

}
