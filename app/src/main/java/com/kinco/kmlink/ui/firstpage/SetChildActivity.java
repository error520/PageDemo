package com.kinco.kmlink.ui.firstpage;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.Button;

import com.google.android.material.tabs.TabLayout;
import com.kinco.kmlink.LanguageUtils.LanguageUtil;
import com.kinco.kmlink.MainActivity;
import com.kinco.kmlink.R;
import com.kinco.kmlink.utils.PrefUtil;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

public class SetChildActivity extends AppCompatActivity {
    SettingPagerAdapter adapter;
    ViewPager viewPager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_child);
        adapter = new SettingPagerAdapter(this,getSupportFragmentManager(),1);
        viewPager = findViewById(R.id.viewpager);
        viewPager.setAdapter(adapter);
        TabLayout tabs = findViewById(R.id.tabLayout);
        tabs.setupWithViewPager(viewPager);
        viewPager.setCurrentItem(getIntent().getIntExtra("index",0));
        Button btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(v->{
            backToMain();
        });

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        int index = intent.getIntExtra("index",0);
        if(viewPager!=null){
            viewPager.setCurrentItem(index);
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
