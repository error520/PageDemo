package com.kinco.kmlink.ui.secondpage;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Button;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.kinco.kmlink.EventBusUtils.BleDataEvent;
import com.kinco.kmlink.EventBusUtils.MessageEvent;
import com.kinco.kmlink.LanguageUtils.LanguageUtil;
import com.kinco.kmlink.MainActivity;
import com.kinco.kmlink.R;
import com.kinco.kmlink.utils.PrefUtil;
import com.kinco.kmlink.utils.util;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;

public class ReadChildActivity extends AppCompatActivity {
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
        Button btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(v->{
            backToMain();
        });
        FloatingActionButton fab = findViewById(R.id.fab_refresh);
        fab.setOnClickListener(v->{
            if(!viewModel.getRefreshing().getValue()){
                adapter.currentFragment.startFirstRequest();
            }else{
                viewModel.stopRefresh();
            }
        });
        viewModel.init();
        viewModel.getRefreshing().observe(this, fab::setActivated);
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        int index = intent.getIntExtra("index",0);
        if(viewPager!=null){
            viewPager.setCurrentItem(index);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event){
        String message = event.getMessage();
        switch (message) {
            case "device disconnected":
                if(viewModel.getRefreshing().getValue()==true){
                    viewModel.stopRefresh();
                    Log.d("july18","你该停止了");
                }
                util.centerToast(this, getString(R.string.device_disconnected), 0);
                break;
            case "timeout":
                util.centerToast(this, getString(R.string.timeout), 0);
                break;
            case "start thread":
                util.centerToast(this, getString(R.string.start_receiving), 0);
                break;
            case "stop thread":
                util.centerToast(this, getString(R.string.stop_receiving), 0);
                break;
        }
    }

    /**
     *  返回不销毁当前活动
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode==KeyEvent.KEYCODE_BACK){
            backToMain();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void backToMain(){
        moveTaskToBack(true);
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_right_in,R.anim.slide_right_out);
    }

    /**
     * Android7修改语言必备
     * @param newBase
     */
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LanguageUtil.attachBaseContext(newBase,getAppLanguage(newBase)));
    }

    /**
     * Handling Configuration Changes
     * @param newConfig newConfig
     */
    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        onLanguageChange();
    }

    private void onLanguageChange() {
        //AppLanguageUtils.changeAppLanguage(this, AppLanguageUtils.getSupportLanguage(getAppLanguage(this)));
        LanguageUtil.changeAppLanguage(this, getAppLanguage(this));
    }

    private String getAppLanguage(Context context) {
        String appLang = PrefUtil.getLanguage(context);
        return appLang ;
    }

}
