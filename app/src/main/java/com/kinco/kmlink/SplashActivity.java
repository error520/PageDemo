package com.kinco.kmlink;


import android.app.Activity;

import android.content.Intent;

import android.os.Bundle;

import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;

import com.kinco.kmlink.ui.functionpage.DeviceList;


public class SplashActivity extends Activity {
     private final int SPLASH_DISPLAY_LENGTH = 1800; // 两秒后进入系统
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //避免重复加载
        if ((getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0) {
            finish();
            return;
        }
        setContentView(R.layout.activity_splash);
        ImageView imgBuilding = findViewById(R.id.img_building);
        ScaleAnimation animation = new ScaleAnimation(1.0f,1.2f,1.0f,1.2f
        ,Animation.RELATIVE_TO_SELF, 0.4f, Animation.RELATIVE_TO_SELF, 0.1f);
        animation.setDuration(2000);
        animation.setFillAfter(true);
        imgBuilding.startAnimation(animation);
        Thread myThread=new Thread(){//创建子线程



            @Override



            public void run() {
                try{
                    sleep(SPLASH_DISPLAY_LENGTH);//使程序休眠
                    Intent it=new Intent(getApplicationContext(), DeviceList.class);//启动MainActivity
                    startActivity(it);
                    finish();//关闭当前活动
                }catch (Exception e){
                    e.printStackTrace();
                }
            }



        };
        myThread.start();//启动线程
    }
}


