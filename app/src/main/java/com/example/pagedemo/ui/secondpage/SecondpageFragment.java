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
    public int[] colors = { Color.WHITE, Color.rgb(219, 238, 244) };//RGB颜色
    String[] Name={"Output frequency","Output voltage","Motor power","VFD","VFD rotation","Main reference","Serial port control",
        "Serial port setting","Fault/alarm code"};

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
                case R.id.SecondpageReload:
                    addressState="0110";
                    mBluetoothLeService.readData("0110","0001");


            }

        }
    }

    private class LocalReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(action.equals(BLEService.ACTION_DATA_AVAILABLE)) {
                String message = intent.getStringExtra(BLEService.EXTRA_MESSAGE_DATA);
                int info = Integer.valueOf(message.substring(9,11))+Integer.valueOf(message.substring(12,14))*256;
                if(addressState.equals("0110")){
                      Log.d(TAG,info+"");
                    if((info&0x1)>0)
                        list0.set(3,new Parameter(Name[3],"Running"));
                    else
                        list0.set(3,new Parameter(Name[3],"Stop"));
                    if((info&0x02)>0)
                        list0.set(4,new Parameter(Name[4],"Reverse"));
                    else
                        list0.set(4,new Parameter(Name[4],"Forward"));
                    if((info&0x04)>0)
                        list0.set(5,new Parameter(Name[5],"Reach"));
                    else
                        list0.set(5,new Parameter(Name[5],"Not Reach"));
                    if((info&0x08)>0)
                        list0.set(6,new Parameter(Name[6],"Enable"));
                    else
                        list0.set(6,new Parameter(Name[6],"Disable"));
                    if((info&0x10)>0)
                        list0.set(7,new Parameter(Name[7],"Enable"));
                    else
                        list0.set(7,new Parameter(Name[7],"Disable"));
                    if((info&0x20)>0)
                        list0.set(8,new Parameter(Name[8],"Alarm"));
                    else
                        list0.set(8,new Parameter(Name[8],"Fault of normal"));
                    adapter.notifyDataSetChanged();
//
               }

            }
            else if(action.equals(BLEService.ACTION_GATT_DISCONNECTED)) {
                util.centerToast(getContext(),"Bluetooth disconnected!",0);
            }
        }
    }
}