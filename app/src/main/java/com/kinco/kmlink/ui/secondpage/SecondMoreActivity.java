package com.kinco.kmlink.ui.secondpage;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Button;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.kinco.kmlink.BluetoothService.BleService;
import com.kinco.kmlink.LanguageUtils.LanguageUtil;
import com.kinco.kmlink.utils.PrefUtil;
import com.kinco.kmlink.MainActivity;
import com.kinco.kmlink.ParameterItem.OldParameter;
import com.kinco.kmlink.R;
import com.kinco.kmlink.ParameterItem.TableAdapter;
import com.kinco.kmlink.utils.util;


public class SecondMoreActivity extends Activity implements View.OnClickListener {
    public int[] colors = { Color.WHITE, Color.rgb(219, 238, 244) };//RGB颜色
    //去除了不要的
    boolean[] sign={true, false, false, false, true,  false, false,  false, false, false, false, false, false, false,  false, false, false, false, false, false, false};
    double[] min={0.01, 1, 0.1, 0.1, 0.01, 1, 1,  0.1, 1, 1, 0.1, 0.01, 1, 1,  1, 0.01, 0.01, 0.1, 0.1, 0.01, 0.01};
    int[] specialGroup={0,4,15,16,19,20};
    String[] specialHint={"~300.00","~300.00","~10.00","~10.00","~100.00","~100.00"};
    private HashMap<String, OldParameter> map = new HashMap<>();
    String[] frOptions;
    private BleService mBluetoothLeService;
    private LocalBroadcastManager localBroadcastManager=LocalBroadcastManager.getInstance(this);
    private BroadcastReceiver receiver=new LocalReceiver();
    private String addressState="0000";
    TableAdapter adapter;
    ListView tableListView;
    List<OldParameter> list = new ArrayList<OldParameter>();
    TableAdapter adapter2;
    List<OldParameter> list2 = new ArrayList<>();
    private Handler mHandler=new Handler();
    private int mPosition=0;//代表列表中的位置
    private boolean reloading = false;
    private String TAG = "SecondMore";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.condition_2);
        initService();
        //设置表格标题的背景颜色
        ViewGroup tableTitle = (ViewGroup) findViewById(R.id.table_title);


        //刷新按钮
        Button btnReload1 = findViewById(R.id.btnR1);
        btnReload1.setOnClickListener(this);
        Button btnReload2 = findViewById(R.id.btnR2);
        btnReload2.setOnClickListener(this);

        frOptions = getResources().getStringArray(R.array.fault_record1_options);
        //初始化普通参数
        String []Name = getResources().getStringArray(R.array.B0_All);
        String []Unit = getResources().getStringArray(R.array.B0_Unit);
        for(int i=0;i<Name.length;i++){
            OldParameter oldParameter = new OldParameter(Name[i],"(null)",Unit[i],sign[i],min[i]);
            list.add(oldParameter);
            //存入地址
            //initMap(i,parameter);
        }
        tableListView = (ListView) findViewById(R.id.list1);
        adapter = new TableAdapter(this, list);
        tableListView.setAdapter(adapter);
        util.setListViewHeightBasedOnChildren(tableListView);

        //初始化状态字
        String []bitsName = getResources().getStringArray(R.array.B005_bits_Name);
        for(int i=0;i<bitsName.length;i++){
            list2.add(new OldParameter(bitsName[i],"null","",false,1));
        }
        ListView tableListView2 = findViewById(R.id.list2);
        adapter2 = new TableAdapter(this,list2);
        tableListView2.setAdapter(adapter2);
        util.setListViewHeightBasedOnChildren(tableListView2);

        // 点击事件, 更新单条
        tableListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id){
                OldParameter oldParameter =list.get(position);
                util.centerToast(SecondMoreActivity.this, oldParameter.getName(),0);
                mPosition=position;
                String address = toAddString(mPosition);
                mBluetoothLeService.readData(address,"0001");

            }
        });



    }

    @Override
    public void onClick(View v) {
            switch (v.getId()){
                case R.id.btnR1:
                    reloading = true;
                    mPosition = 0;
                    mBluetoothLeService.readData("0100","0001");
                    break;
                case R.id.btnR2:
                    mBluetoothLeService.readData("0105","0001");
                    mPosition = 100;
                    break;
            }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode==KeyEvent.KEYCODE_BACK){
            moveTaskToBack(true);
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void initService(){
        //绑定服务
        Intent BLEIntent = new Intent(SecondMoreActivity.this, BleService.class);
        bindService(BLEIntent,new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                mBluetoothLeService = ((BleService.localBinder) service)
                        .getService();
            }
            @Override
            public void onServiceDisconnected(ComponentName name) {
            }
        }, Context.BIND_AUTO_CREATE);
        localBroadcastManager.registerReceiver(receiver, util.makeGattUpdateIntentFilter());
    }

    private class LocalReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if(action.equals(BleService.ACTION_DATA_AVAILABLE)){
                    final byte[] message = intent.getByteArrayExtra(BleService.EXTRA_MESSAGE_DATA);
                    //更新UI
                    runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(reloading)
                                    reloadAll(message);
                                else {
                                    reloadItem(message, mPosition);
                                    util.centerToast(SecondMoreActivity.this,getResources().getString(R.string.reload_completed),0);
                                };

                            }
                    });
                }else if(action.equals(BleService.ACTION_GATT_DISCONNECTED)){
                    util.centerToast(SecondMoreActivity.this,getString(R.string.device_disconnected),0);
                }
        }
    }

    /**
     * 更新所有参数
     * @param message
     */
    private void reloadAll(byte[] message){
            reloadItem(message, mPosition);
            if (mPosition==20){
                mPosition = 0;
                reloading = false;
                util.centerToast(this, getResources().getString(R.string.reload_completed), 1);
                return;
            }
            mPosition++;
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    addressState = toAddString(mPosition);
                    mBluetoothLeService.readData(addressState, "0001");
                }
            }, BleService.reloadGap);

    }

    /**
     * 更新单个条目
     * @param message
     */
    private void reloadItem(byte[] message, int position){
        if(position==100){
            reloadSW(message);
            return;
        }
        OldParameter pp = list.get(position);
        String describe;
        String Hint="";
        boolean special=false;
        //判断是否是特殊的有符号参数
        for(int i=0; i<specialGroup.length; i++){
            if(specialGroup[i]==position) {
                Hint = specialHint[i];
                special=true;
                break;
            }
        }
        if(special)
            describe = util.parseByteData2(message, 3, (float) pp.getMin(), Hint);
        else
            describe = util.parseByteData(message,3, (float)pp.getMin(),pp.getSign());

        if(position==8){
            try{
                describe = frOptions[Integer.valueOf(describe)];
            }catch (Exception e){
                Log.d(TAG,"错误原因:"+describe);
            }
        }
        pp.setDescribe(describe);
        adapter.notifyDataSetChanged();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                util.setListViewHeightBasedOnChildren(tableListView);
            }
        });
    }


    /**
     * 从B组功能码位置转为发送的寄存器地址字符串
     * @param position
     * @return
     */
    private String toAddString(int position){
        String hex;
        hex = "01"+String.format("%02X",position);
        if(position>=5)
            hex = "01"+String.format("%02X",position+1);
        if(position>=7)
            hex = "01"+String.format("%02X",position+2);
        if(position>=14)
            hex = "01"+String.format("%02X",position+3);
        //mAddress=position;

        return hex;
    }

    /**
     * 更新状态字
     * @param message
     */
    private void reloadSW(byte[] message){
        int SW = Integer.valueOf(util.parseByteData(message,3,1,false));
        Log.d("SecondMore",String.format("%x",SW));
        for(int i=1; i<0x10000; i=i<<1){
            int position = (int)(Math.log(i)/Math.log(2));
            OldParameter pp = list2.get(position);
            //Log.d("SecondMore",String.format("%x",i));
            if((SW&i)>0)
                pp.setDescribe("true");
            else
                pp.setDescribe("false");
        }
        adapter2.notifyDataSetChanged();

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

    @Override
    protected void onStart() {
        super.onStart();
        initService();
    }

    @Override
    protected void onStop() {
        super.onStop();
        localBroadcastManager.unregisterReceiver(receiver);
        //unbindService(connection);
    }

}
