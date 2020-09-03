package com.kinco.kmlink;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.kinco.kmlink.BluetoothService.BleService;
import com.kinco.kmlink.LanguageUtils.LanguageUtil;
import com.kinco.kmlink.ui.firstpage.SettingCategoryFragment;
import com.kinco.kmlink.ui.secondpage.ReadCategoryFragment;
import com.kinco.kmlink.utils.PrefUtil;
import com.kinco.kmlink.sys.MyFragment;

import com.kinco.kmlink.ui.widget.DeviceWindow;
import com.kinco.kmlink.ui.fourthpage.FourthpageFragment;
import com.kinco.kmlink.ui.menupage.MenuFragment;
import com.kinco.kmlink.utils.util;
import com.wuhenzhizao.titlebar.widget.CommonTitleBar;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    TextView tvDeviceName;
    ImageView ivStatus;
    DeviceWindow deviceList;
    private MyFragment firstpageFragment = new SettingCategoryFragment();
    private MyFragment secondpageFragment = new ReadCategoryFragment();
    private FourthpageFragment thirdpageFragment = new FourthpageFragment();
    private MenuFragment fourthpageFragment = new MenuFragment();
    private String TAG = "MainActivity";
    public static boolean flag = false;
    public static TabFragmentUtils tabFragmentUtils;
    public static String path = "null";
    private Handler mHandler;
    RadioGroup mainRadioGroupId;
    LocalReceiver localReceiver;
    LocalBroadcastManager localBroadcastManager;
    MainViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LanguageUtil.changeAppLanguage(this, PrefUtil.getLanguage(this)); // onCreate 之前调用 否则不起作用
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bindBleService();
        mainRadioGroupId = findViewById(R.id.main_radioGroupId);
        //初始化fragment
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

        mHandler = new Handler();
        //在里面会判断是不是外部wave文件进来的
        showWave(getIntent());

        localReceiver = new LocalReceiver();
        localBroadcastManager = LocalBroadcastManager.getInstance(this);
        localBroadcastManager.registerReceiver(localReceiver, util.makeGattUpdateIntentFilter());

        initTitleBar(viewModel);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        //MainActivity不在任务栈内时不会调用
        showWave(intent);
        Log.d(TAG, "onNewIntent()");
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //小技巧 重写这个方法  然后不重写父类的方法，可以避免程序闪退之后，几个fragment，会重叠！
//        super.onSaveInstanceState(outState);
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

    //初始化状态栏和下拉列表
    private void initTitleBar(MainViewModel viewModel) {
        final CommonTitleBar titleBar = findViewById(R.id.main_titleBar);
        View rightLayout = titleBar.getRightCustomView();
        tvDeviceName = rightLayout.findViewById(R.id.tv_device_name);
        ivStatus = rightLayout.findViewById(R.id.icon_status);
        BleService.isConnected.observe(this, isConnected -> {
            ivStatus.setActivated(isConnected);
        });
        BleService.deviceName.observe(this, name -> {
            tvDeviceName.setText(name);
//            if(!name.equals(getString(R.string.unconnected))&&deviceList!=null){
//                deviceList.onConnected(name);
//            }
        });
        rightLayout.setOnClickListener(v -> {
            if (deviceList == null) {
                deviceList = new DeviceWindow(MainActivity.this, viewModel);
                Log.d(TAG, "新建了一个设备列表");
            }
            deviceList.setAlignBackground(true);
            deviceList.showPopupWindow(titleBar);
        });
    }

    /**
     * 切换到波形界面展示波形
     */
    private void showWave(Intent intent) {
        String action = intent.getAction();
        //Log.d(TAG,"action:"+action);
        if (action != null && action.equals("android.intent.action.VIEW")) {
            final Uri uri = intent.getData();
            if (uri.getPath().matches(".*\\.dat")) {
                tabFragmentUtils.showPage(2);
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        thirdpageFragment.readWave(uri);
                    }
                }, 1000);
            } else {
                util.centerToast(this, "文件格式错误!", 0);
            }
            //Log.d(TAG,uri.toString());


        }
    }

    private class LocalReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(BleService.ACTION_GET_DEVICE_NAME)) {

            } else if (action.equals(BleService.ACTION_SEARCH_COMPLETED)) {
//                viewModel.onSearchCompleted();
            } else if (action.equals(BleService.ACTION_GATT_CONNECTED)) {
//                String name = viewModel.onConnected();
            } else if (action.equals(BleService.ACTION_GATT_DISCONNECTED)) {
                util.centerToast(MainActivity.this, getString(R.string.device_disconnected), 0);
//                if(deviceList!=null){
//                    deviceList.onDisconnected();
//                }
//                viewModel.onDisconnected();
            } else if (action.equals(BleService.ACTION_DATA_AVAILABLE)) {
//                final byte[] message = intent.getByteArrayExtra(BleService.EXTRA_MESSAGE_DATA);
//                viewModel.parseData(message);
            }
        }
    }

    private void bindBleService(){
        Intent intent = new Intent(this,BleService.class);
        bindService(intent, new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {

            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        },BIND_AUTO_CREATE);
    }

}
