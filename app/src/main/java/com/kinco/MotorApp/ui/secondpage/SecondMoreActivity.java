package com.kinco.MotorApp.ui.secondpage;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;


import java.util.ArrayList;
import java.util.List;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.kinco.MotorApp.BluetoothService.BLEService;
import com.kinco.MotorApp.edittext.Parameter;
import com.kinco.MotorApp.R;
import com.kinco.MotorApp.edittext.TableAdapter;
import com.kinco.MotorApp.util;


//import com.example.tabledemo.R;

public class SecondMoreActivity extends Activity {
    public int[] colors = { Color.WHITE, Color.rgb(219, 238, 244) };//RGB颜色
//    String[] Name={"Output frequency","Output voltage","Output current","Motor power","Motor actual frequency",
//    "Inverter running status","Input terminals status","Output terminals status","AI1 input voltage","Temperature of heatsink 1",
//    "Fault record 1","Bus voltage of the latest failure","Actual current of the latest failure","Operation frequency of the latest failure",
//    "Custom-made version number","Software date"};
    String[] Unit={"Hz","V","A","%","Hz","","","","V","℃","","V","A","Hz","","","V","V","V","%","%","%","%"};
    boolean[] sign={true, false, false, false, true, false, false, false, true, false, false, false, false, false, false, false, false, false, false, false, false, false, false};
    double[] min={0.01, 1, 0.1, 0.1, 0.01, 1, 1, 1, 0.01, 0.1, 1, 1, 0.1, 0.01, 1, 1, 1, 0.01, 0.01, 0.1, 0.1, 0.01, 0.01};
    private BLEService mBluetoothLeService;
    private LocalBroadcastManager localBroadcastManager=LocalBroadcastManager.getInstance(this);
    private BroadcastReceiver receiver=new LocalReceiver();
    private String addressState="0000";
    TableAdapter adapter;
    List<Parameter> list = new ArrayList<Parameter>();
    private int mAddress=100;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.condition_2);
        initService();
        //设置表格标题的背景颜色
        ViewGroup tableTitle = (ViewGroup) findViewById(R.id.table_title);
//        tableTitle.setBackgroundColor(Color.rgb(219, 238, 244));

        //！！！数据每次点击后都应该刷新数据
        String []Name = getResources().getStringArray(R.array.B0_All);
        for(int i=0;i<Name.length;i++){
            list.add(new Parameter(Name[i],"(null)",Unit[i],sign[i],min[i]));
        }
        ListView tableListView = (ListView) findViewById(R.id.list1);

        adapter = new TableAdapter(this, list);

        tableListView.setAdapter(adapter);



        // 点击事件
        tableListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id){
                Parameter parameter=list.get(position);
                util.centerToast(SecondMoreActivity.this,parameter.getName(),0);
                addressState="010"+Integer.toHexString(position);
                mAddress=position;
                mBluetoothLeService.readData(addressState,"0001");

            }
        });



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
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(mAddress<15) {
                                Parameter pp = list.get(mAddress);
                                if(pp.getSign())
                                    pp.setDescribe((short)(util.byte2ToUnsignedShort(message, 3))*pp.getMin()+"");
                                else
                                    pp.setDescribe(util.byte2ToUnsignedShort(message, 3)*pp.getMin()+"");
                            }
                            mAddress=100;
                            adapter.notifyDataSetChanged();
                        }
                    });



                }
        }
    }

}
