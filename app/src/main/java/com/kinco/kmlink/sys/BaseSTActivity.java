package com.kinco.kmlink.sys;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.view.KeyEvent;

import com.kinco.kmlink.LanguageUtils.LanguageUtil;
import com.kinco.kmlink.ui.main.MainActivity;
import com.kinco.kmlink.R;
import com.kinco.kmlink.utils.PrefUtil;
import com.wuhenzhizao.titlebar.widget.CommonTitleBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

/**
 * SingleTask 在更换语言时需要特殊操作, 写一个基类包含这些操作
 */
public class BaseSTActivity extends AppCompatActivity {

    @Override
    protected void onStart() {
        super.onStart();
        CommonTitleBar titleBar = findViewById(R.id.toolBar);
        titleBar.setListener((v, action, extra)->{
            if(action == CommonTitleBar.ACTION_LEFT_BUTTON){
                backToMain();
            }
        });
    }

    /**
     * 修改语言必备
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

    protected void backToMain(){
        moveTaskToBack(true);
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_right_in,R.anim.slide_right_out);
    }
}
