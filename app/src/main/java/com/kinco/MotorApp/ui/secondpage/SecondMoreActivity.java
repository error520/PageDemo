package com.kinco.MotorApp.ui.secondpage;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
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
import java.util.List;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.kinco.MotorApp.BluetoothService.BLEService;
import com.kinco.MotorApp.MainActivity;
import com.kinco.MotorApp.edittext.Parameter;
import com.kinco.MotorApp.R;
import com.kinco.MotorApp.edittext.TableAdapter;
import com.kinco.MotorApp.utils.util;


public class SecondMoreActivity extends Activity implements View.OnClickListener {
    public int[] colors = { Color.WHITE, Color.rgb(219, 238, 244) };//RGB颜色
//    String[] Name={"Output frequency","Output voltage","Output current","Motor power","Motor actual frequency",
//    "Inverter running status","Input terminals status","Output terminals status","AI1 input voltage","Temperature of heatsink 1",
//    "Fault record 1","Bus voltage of the latest failure","Actual current of the latest failure","Operation frequency of the latest failure",
//    "Custom-made version number","Software date"};
//    String[] Unit={"Hz","V","A","%","Hz","","","V","℃","","V","A","Hz","","","V","V","V","%","%","%","%"};
    //加入了两个状态字
    boolean[] sign={true, false, false, false, true, false, false, false, true, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false};
    double[] min={0.01, 1, 0.1, 0.1, 0.01, 1, 1, 1, 0.01, 0.1, 1, 1, 0.1, 0.01, 1, 1, 1, 1, 0.01, 0.01, 0.1, 0.1, 0.01, 0.01};
    int[] specialGroup={18,19,22,23};
    String[] specialHint={"~10.00","~10.00","~100.00","~100.00"};
    String[] frOptions;
    private BLEService mBluetoothLeService;
    private LocalBroadcastManager localBroadcastManager=LocalBroadcastManager.getInstance(this);
    private BroadcastReceiver receiver=new LocalReceiver();
    private String addressState="0000";
    TableAdapter adapter;
    List<Parameter> list = new ArrayList<Parameter>();
    TableAdapter adapter2;
    List<Parameter> list2 = new ArrayList<>();
    private Handler mHandler=new Handler();
    private int mAddress=0;//代表列表中的名字
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
            list.add(new Parameter(Name[i],"(null)",Unit[i],sign[i],min[i]));
        }
        ListView tableListView = (ListView) findViewById(R.id.list1);
        adapter = new TableAdapter(this, list);
        tableListView.setAdapter(adapter);
        util.setListViewHeightBasedOnChildren(tableListView);

        //初始化状态字
        String []bitsName = getResources().getStringArray(R.array.B005_bits_Name);
        for(int i=0;i<bitsName.length;i++){
            list2.add(new Parameter(bitsName[i],"null","",false,1));
        }
        ListView tableListView2 = findViewById(R.id.list2);
        adapter2 = new TableAdapter(this,list2);
        tableListView2.setAdapter(adapter2);
        util.setListViewHeightBasedOnChildren(tableListView2);

        // 点击事件
        tableListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id){
                Parameter parameter=list.get(position);
                util.centerToast(SecondMoreActivity.this,parameter.getName(),0);
                String hex = Integer.toHexString(position);
                if(hex.length()<2)
                    hex = "0"+hex;
                addressState="01"+hex;
                mAddress=position;
                mBluetoothLeService.readData(addressState,"0001");

            }
        });



    }

    @Override
    public void onClick(View v) {
            switch (v.getId()){
                case R.id.btnR1:
                    reloading = true;
                    mBluetoothLeService.readData("0100","0001");
                    break;
                case R.id.btnR2:
                    mBluetoothLeService.readData("0105","0001");
                    mAddress = 5;
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
        Intent BLEIntent = new Intent(SecondMoreActivity.this, BLEService.class);
        bindService(BLEIntent,new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                mBluetoothLeService = ((BLEService.localBinder) service)
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
                if(action.equals(BLEService.ACTION_DATA_AVAILABLE)){
                    final byte[] message = intent.getByteArrayExtra(BLEService.EXTRA_MESSAGE_DATA);
                    //更新UI
                    runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(reloading)
                                    reloadAll(message);
                                else {
                                    reloadItem(message, mAddress);
                                    util.centerToast(SecondMoreActivity.this,getResources().getString(R.string.reload_completed),0);
                                };

                            }
                    });
                }
        }
    }

    /**
     * 更新所有参数
     * @param message
     */
    private void reloadAll(byte[] message){
            reloadItem(message, mAddress);
            if (mAddress==23){
                mAddress = 0;
                reloading = false;
                util.centerToast(this, getResources().getString(R.string.reload_completed), 1);
                return;
            }
            mAddress++;
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    addressState = toAddString(mAddress);
                    mBluetoothLeService.readData(addressState, "0001");
                }
            }, 1000);

    }

    /**
     * 更新单个条目
     * @param message
     */
    private void reloadItem(byte[] message, int position){
        Parameter pp = list.get(position);
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

        if(position==5)//如果是5, 还要额外更新状态字
            reloadSW(message);
        if(position==10){
            describe = frOptions[Integer.valueOf(describe)];
        }
        pp.setDescribe(describe);
        adapter.notifyDataSetChanged();
    }

    /**
     * 从B组功能码位置转为发送的寄存器地址字符串
     * @param position
     * @return
     */
    private String toAddString(int position){
        String hex = Integer.toHexString(position);
        if(hex.length()<2)
            hex = "0"+hex;
        String addressString="01"+hex;
        //mAddress=position;

        return addressString;
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
            Parameter pp = list2.get(position);
            //Log.d("SecondMore",String.format("%x",i));
            if((SW&i)>0)
                pp.setDescribe("true");
            else
                pp.setDescribe("false");
        }
        adapter2.notifyDataSetChanged();

    }

}
