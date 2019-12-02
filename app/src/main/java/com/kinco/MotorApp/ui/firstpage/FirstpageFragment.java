package com.kinco.MotorApp.ui.firstpage;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.kinco.MotorApp.DemoFragment;
import com.kinco.MotorApp.edittext.ItemBean;
import com.kinco.MotorApp.edittext.ListViewAdapter;
import com.kinco.MotorApp.edittext.Text;
import com.kinco.MotorApp.edittext.TextAdapter;
import com.kinco.MotorApp.util;
import com.kinco.MotorApp.R;
import com.kinco.MotorApp.BluetoothService.BLEService;

import java.util.ArrayList;
import java.util.List;


public class FirstpageFragment extends Fragment implements View.OnClickListener {
    String[] Name =  { "Control mode", "Main reference  frequency selector"};
    String[][] temp = {{"0：Vector control without PG","1: Vector control with PG","2:V/F control"},
            {"0:Digital setting Keyboard UP/DN or terminal UP/DN ","1:AI1","2:AI2","3:AI3","4:Set via DI terminal(PULSE)","5:Reserved"}};
    private String TAG = "FirstpageFragment";
    Text text;
    private View view;//得到碎片对应的布局文件,方便后续使用
    private ListView listView;
    private ListView mListView;
    private ListViewAdapter mAdapter;
    private List<ItemBean> mData;
    //记住一定要重写onCreateView方法
    private BLEService mBluetoothLeService;
    private LocalBroadcastManager localBroadcastManager;
    private BroadcastReceiver receiver=new LocalReceiver();
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_first, container, false);//得到对应的布局文件
        return view;
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initService();
        show();
        //输入型
        mListView = (ListView) getActivity().findViewById(R.id.list_view0);
        mData = new ArrayList<ItemBean>();
        mData.add(new ItemBean( "Digital reference frequency","HZ","","0.0~300.00","0003"));
        mAdapter = new ListViewAdapter(this.getActivity(), mData);
        mAdapter.setAddressNoListener(new ListViewAdapter.AddressNoListener() {
            @Override
            public void clickListener(String address, String value) {
                mBluetoothLeService.writeData(address,value);
            }
        });
        mListView.setAdapter(mAdapter);
        util.setListViewHeightBasedOnChildren(mListView);
        Button button0 = (Button) getActivity().findViewById(R.id.FirstpageMore);
        button0.setOnClickListener(this);
        Button button3 = (Button) getActivity().findViewById(R.id.control_110B);
        button3.setOnClickListener(this);
        Button button4 = (Button) getActivity().findViewById(R.id.control_101B);
        button4.setOnClickListener(this);
        Button button7 = (Button) getActivity().findViewById(R.id.control_bit3_0);
        button7.setOnClickListener(this);
        Button button8 = (Button) getActivity().findViewById(R.id.control_bit3_1);
        button8.setOnClickListener(this);
        Button button19 = (Button) getActivity().findViewById(R.id.control_bit9_0);
        button19.setOnClickListener(this);
        Button button20 = (Button) getActivity().findViewById(R.id.control_bit9_1);
        button20.setOnClickListener(this);

    }


    /**
     * 初始化服务和广播
     */
    private void initService(){
        //绑定服务
        Intent BLEIntent = new Intent(getActivity(),BLEService.class);
        getActivity().bindService(BLEIntent,connection, Context.BIND_AUTO_CREATE);
        localBroadcastManager = LocalBroadcastManager.getInstance(getContext());
        localBroadcastManager.registerReceiver(receiver,util.makeGattUpdateIntentFilter());
    }
    private void show() {
        List<Text> texts = new ArrayList<Text>();
        for (int i = 0; i < 2; i++) {//自定义的Text类存数据
            final int j = i;
            text = new Text();
            text.setTitle(Name[i]);//标题数据
            text.setCurrent(""+i);
            text.setId(0);//Spinner的默认选择项
            text.setContent(temp[i]);
            text.setAddress("000" + (i + 1));
            texts.add(text);
            TextAdapter textAdapter = new TextAdapter(this.getActivity(), texts, R.layout.main_item);//向自定义的Adapter中传值
            textAdapter.setAddressNoListener(new TextAdapter.AddressNoListener() {
                //操作
                @Override
                public void titleNo(String title, String value) {
                    mBluetoothLeService.writeData(title,value);
                }
                public void addressNo(int addressNo) {
                    //Toast.makeText(getContext()," "+addressNo, Toast.LENGTH_SHORT).show();
                }
            });
            listView = (ListView) getActivity().findViewById(R.id.mylist0);
            listView.setAdapter(textAdapter);//传值到ListView中
            util.setListViewHeightBasedOnChildren(listView);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.FirstpageMore:
                Intent intent=new Intent(getContext(), FirstMoreActivity.class);
                getActivity().startActivity(intent);//当然也可以写成getContext()
                break;
            case R.id.control_110B:
                //方式0停车
                mBluetoothLeService.writeData("0017","00C6");
                break;
            case R.id.control_101B:
                //方式1停车
                mBluetoothLeService.writeData("0017","00C5");
                break;
            case R.id.control_bit3_0:
                //正转
                mBluetoothLeService.writeData("0017","00c7");
                break;
            case R.id.control_bit3_1:
                //反转
                mBluetoothLeService.writeData("0017","00cf");
                break;
            case R.id.control_bit9_0:
                //故障复位有效
                mBluetoothLeService.writeData("0017","0280");
                break;
            case R.id.control_bit9_1:
                //故障复位无效
                mBluetoothLeService.writeData("0017","0080");
                break;

        }


    }

    /**
     * 得到服务实例
     */
    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mBluetoothLeService = ((BLEService.localBinder) service)
                    .getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    public void onPause() {
        super.onPause();
        localBroadcastManager.unregisterReceiver(receiver);
    }

    public FirstpageFragment newInstance(int i) {
        Bundle args = new Bundle();
        args.putInt("int", i);
        FirstpageFragment fragment = new FirstpageFragment();
//        fragment.setArguments(args);
        return fragment;
    }

    private class LocalReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.d(TAG,action);
            if(action.equals(BLEService.ACTION_DATA_AVAILABLE)) {
                String message = intent.getStringExtra(BLEService.EXTRA_MESSAGE_DATA);
                util.centerToast(getContext(),"succeed!",0);
            }
            else if(action.equals(BLEService.ACTION_GATT_DISCONNECTED)) {
                util.centerToast(getContext(),"Bluetooth disconnected!",0);
            }
            else if(action.equals(BLEService.ACTION_ERROR_CODE)){
                String errorCode = intent.getStringExtra(BLEService.ACTION_ERROR_CODE);
                Toast toast = Toast.makeText(getContext(),"error code:"+errorCode,Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER,0,0);
                toast.show();

            }
        }
    }





}