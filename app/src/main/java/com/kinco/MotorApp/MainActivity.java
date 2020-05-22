package com.kinco.MotorApp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.codingending.popuplayout.PopupLayout;
import com.kinco.MotorApp.LanguageUtils.LanguageUtil;
import com.kinco.MotorApp.LanguageUtils.PrefUtils;
import com.kinco.MotorApp.alertdialog.ContactDialog;
import com.kinco.MotorApp.alertdialog.PasswordDialog;
import com.kinco.MotorApp.alertdialog.SetLanguageDialog;
import com.kinco.MotorApp.sys.MyFragment;
import com.kinco.MotorApp.ui.functionpage.DeviceList;

import com.kinco.MotorApp.ui.functionpage.DevicePopup;
import com.kinco.MotorApp.ui.functionpage.LogActivity;
import com.kinco.MotorApp.ui.firstpage.FirstpageFragment;
import com.kinco.MotorApp.ui.fourthpage.FourthpageFragment;
import com.kinco.MotorApp.ui.menupage.MenuFragment;
import com.kinco.MotorApp.ui.secondpage.SecondpageFragment;
import com.kinco.MotorApp.ui.thirdpage.ThirdpageFragment;
import com.kinco.MotorApp.utils.util;
import com.wuhenzhizao.titlebar.widget.CommonTitleBar;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity  {
    private FirstpageFragment firstpageFragment=new FirstpageFragment();
    private SecondpageFragment secondpageFragment=new SecondpageFragment();
    private FourthpageFragment thirdpageFragment=new FourthpageFragment();
    private MenuFragment fourthpageFragment=new MenuFragment();
    private String TAG = "MainActivity";
    public static boolean  flag = false;
    public static TabFragmentUtils tabFragmentUtils;
    public static String path = "null";
    private Handler mHandler;
    RadioGroup mainRadioGroupId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LanguageUtil.changeAppLanguage(this, PrefUtils.getLanguage(this)); // onCreate 之前调用 否则不起作用
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initTitleBar();
        mainRadioGroupId = (RadioGroup) findViewById(R.id.main_radioGroupId);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.fragment_id, firstpageFragment);
        fragmentTransaction.commit();
        List<MyFragment> fragments = new ArrayList<>();
        fragments.add(firstpageFragment);
        fragments.add(secondpageFragment);
        fragments.add(thirdpageFragment);
        fragments.add(fourthpageFragment);

        tabFragmentUtils = new TabFragmentUtils(mainRadioGroupId, R.id.fragment_id, fragments, getSupportFragmentManager());
        Log.d(TAG,"onCreate()");

        mHandler = new Handler();
        //在里面会判断是不是外部wave文件进来的
        showWave(getIntent());

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        //MainActivity不在任务栈内时不会调用
        showWave(intent);
        Log.d(TAG,"onNewIntent()");
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //小技巧 重写这个方法  然后不重写父类的方法，可以避免程序闪退之后，几个fragment，会重叠！
        super.onSaveInstanceState(outState);

    }

    //返回键不会销毁程序
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent home = new Intent(Intent.ACTION_MAIN);
            home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            home.addCategory(Intent.CATEGORY_HOME);
            startActivity(home);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item00:
                Intent intent = new Intent(this, DeviceList.class);
                startActivity(intent);
             break;
            case R.id.item01:
                SetLanguageDialog sld = new SetLanguageDialog(this);
                break;
            case R.id.item02:
                //util.centerToast(this,"Comming soon",0);
                ContactDialog contactDialog = new ContactDialog(this);
                break;
            case R.id.item03:
                Intent intent2 = new Intent(this, LogActivity.class);
                startActivity(intent2);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.top_menu, menu);
        return true;
    }

    private void initTitleBar(){
        final CommonTitleBar titleBar = findViewById(R.id.main_titleBar);
        View rightLayout = titleBar.getRightCustomView();
        rightLayout.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
//                LayoutInflater inflater = LayoutInflater.from(MainActivity.this);
//                View layout = inflater.inflate(R.layout.language_layout,null);
//                PopupLayout popupLayout=PopupLayout.init(MainActivity.this, layout);
//                RadioButton radioButton = layout.findViewById(R.id.radio_en);
//                radioButton.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        util.centerToast(MainActivity.this,"切换成英语啦",0);
//                        PasswordDialog dialog = new PasswordDialog(MainActivity.this);
//
//                    }
//                });
//                popupLayout.show(PopupLayout.POSITION_TOP);
                DevicePopup deviceList = new DevicePopup(MainActivity.this);
                deviceList.setAlignBackground(true);
                deviceList.showPopupWindow(titleBar);

            }
        });
    }

    /**
     * 切换到波形界面展示波形
     */
    private void showWave(Intent intent){
        String action = intent.getAction();
        //Log.d(TAG,"action:"+action);
        if(action!=null && action.equals("android.intent.action.VIEW")){
            final Uri uri = intent.getData();
            if(uri.getPath().matches(".*\\.wave")){
                tabFragmentUtils.showPage(3);
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        thirdpageFragment.readWave(uri);
                    }
                },1000);
            }else{
                util.centerToast(this,"文件格式错误!",0);
            }
            //Log.d(TAG,uri.toString());


        }
    }


}
