package com.example.pagedemo.ui.secondpage;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.pagedemo.BluetoothService.BLEService;
import com.example.pagedemo.ListTableActivity;
import com.example.pagedemo.Parameter;
import com.example.pagedemo.R;
import com.example.pagedemo.TableAdapter;
import com.example.pagedemo.util;

import java.util.ArrayList;
import java.util.List;

public class SecondpageFragment extends Fragment {
    private View view;//得到碎片对应的布局文件,方便后续使用
    private BLEService mBluetoothLeService;
    private String TAG = "second";
    private String addressState="0000";
    private BroadcastReceiver receiver=new LocalReceiver();
    LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(getContext());
    List<Parameter> list0;
    TableAdapter adapter;
    int count = 0;
    public int[] colors = { Color.WHITE, Color.rgb(219, 238, 244) };//RGB颜色
    String[] Name={"Output frequency","Output voltage","Motor power","VFD","VFD rotation","Main reference","Serial port control",
        "Serial port setting","State"};

    //记住一定要重写onCreateView方法
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_second, container, false);//得到对应的布局文件
        return view;
    }
    @Override

    public void onActivityCreated(Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);

        //设置表格标题的背景颜色
        ViewGroup tableTitle = (ViewGroup)getActivity(). findViewById(R.id.table_title);
        tableTitle.setBackgroundColor(Color.rgb(219, 238, 244));
        //！！！数据每次点击后都应该刷新数据
        list0 = new ArrayList<Parameter>();
        for(int i=0;i<9;i++){
            list0.add(new Parameter( Name[i],"null"));
        }
        ListView tableListView = (ListView) getActivity().findViewById(R.id.list0);
        adapter = new TableAdapter(this.getActivity(),list0);
        tableListView.setAdapter(adapter);
        Button button0 = (Button) getActivity().findViewById(R.id.SecondpageMore);
        button0.setOnClickListener(new SecondpageFragment.ButtonListener());
        Button button1 = (Button) getActivity().findViewById(R.id.SecondpageReload);
        button1.setOnClickListener(new SecondpageFragment.ButtonListener());
    }
    private void initService(){
        //绑定服务
        Intent BLEIntent = new Intent(getActivity(), BLEService.class);
        getActivity().bindService(BLEIntent,new ServiceConnection() {
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

    @Override
    public void onResume() {
        super.onResume();
        initService();
    }

    @Override
    public void onPause() {
        super.onPause();
        localBroadcastManager.unregisterReceiver(receiver);
    }

    private class ButtonListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.SecondpageMore:
                    Intent intent=new Intent(getContext(), ListTableActivity.class);
                    getActivity().startActivity(intent);//当然也可以写成getContext()
                    break;
                case R.id.SecondpageReload:
                    addressState="0100";
                    mBluetoothLeService.readData("0100","0001");
                    break;


            }

        }
    }

    private class LocalReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(action.equals(BLEService.ACTION_DATA_AVAILABLE)) {
                String message = intent.getStringExtra(BLEService.EXTRA_MESSAGE_DATA);
                Log.d(TAG,message);
                try {
                    int info = Integer.valueOf(message.substring(9, 11)) + Integer.valueOf(message.substring(12, 14)) * 256;
                    if (addressState.equals("0100")) {
                        list0.get(0).setDescribe(((float) ((short) info) / 100) + "Hz");
                        addressState = "0101";
                        mBluetoothLeService.readData("0101", "0001");
                        return;
                    }
                    if (addressState.equals("0101")) {
                        list0.get(1).setDescribe(info + "V");
                        addressState = "0103";
                        mBluetoothLeService.readData("0103", "0001");
                        return;
                    }
                    if (addressState.equals("0103")) {
                        list0.get(2).setDescribe((float) info / 10 + "%");
                        addressState = "0110";
                        mBluetoothLeService.readData("0110", "0001");
                        return;
                    }
                    if (addressState.equals("0110")) {
                        //Log.d(TAG,info+"");
                        if ((info & 0x1) > 0)
                            list0.get(3).setDescribe("Running");
                        else
                            list0.get(3).setDescribe("Stop");
                        if ((info & 0x02) > 0)
                            list0.get(4).setDescribe("Reverse");
                        else
                            list0.get(4).setDescribe("Forward");
                        if ((info & 0x04) > 0)
                            list0.get(5).setDescribe("Reach");
                        else
                            list0.get(5).setDescribe("Not Reach");
                        if ((info & 0x08) > 0)
                            list0.get(6).setDescribe("Enable");
                        else
                            list0.get(6).setDescribe("Disable");
                        if ((info & 0x10) > 0)
                            list0.get(7).setDescribe("Enable");
                        else
                            list0.get(7).setDescribe("Disable");
                        if ((info & 0x20) > 0)
                            list0.get(8).setDescribe("Alarm");
                        else {
                            if ((info & 0xff00) > 0)
                                list0.get(8).setDescribe("Fault");
                            else
                                list0.get(8).setDescribe("Normal");
                        }
                    }
                }catch(Exception e){
                        util.saveErrorLog(getContext(),e.toString());
                }

                    adapter.notifyDataSetChanged();
                    addressState="0000";
                    util.centerToast(getContext(),"Reload completed!",0);
                    return;

               }

            else if(action.equals(BLEService.ACTION_GATT_DISCONNECTED)) {
                util.centerToast(getContext(),"Bluetooth disconnected!",0);
                util.saveErrorLog(getContext(),"jieshoucuowu");
            }
            else if(action.equals(BLEService.ACTION_ERROR_CODE)){
                util.centerToast(getContext(),"Read error:"
                                +intent.getStringExtra(BLEService.ACTION_ERROR_CODE),0);
                addressState="0000";

            }
        }
    }
}