package com.kinco.kmlink.ui.firstpage;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.kinco.kmlink.sys.MyFragment;
import com.kinco.kmlink.alertdialog.LoadingDialog;
import com.kinco.kmlink.alertdialog.SetDataDialog;
import com.kinco.kmlink.ParameterItem.ItemBean;
import com.kinco.kmlink.ParameterItem.ValueListViewAdapter;
import com.kinco.kmlink.ParameterItem.Text;
import com.kinco.kmlink.ParameterItem.SpinnerListViewAdapter;
import com.kinco.kmlink.utils.ResourceUtil;
import com.kinco.kmlink.utils.util;
import com.kinco.kmlink.R;
import com.kinco.kmlink.BluetoothService.BleService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class FirstpageFragment extends MyFragment implements View.OnClickListener {
    String chooseAddressList[] = {"0001","0002","0004","0005","0021","0022"};
    private HashMap<Integer,Integer> map = new HashMap<>();
    private String TAG = this.getClass().getName();
    Text text;
    SpinnerListViewAdapter spinnerListViewAdapter;
    private View view;//得到碎片对应的布局文件,方便后续使用
    private ListView listView;
    private ListView mListView;
    private ValueListViewAdapter mAdapter;
    private List<ItemBean> mData;
    private LoadingDialog loadingDialog;
    //记住一定要重写onCreateView方法
    private BleService mBluetoothLeService;
    private LocalBroadcastManager localBroadcastManager;
    private BroadcastReceiver receiver=new LocalReceiver();
    private Handler mHandler = new Handler();
    private SetDataDialog setDatadialog;
    private String editSetData="";
    public static boolean initializing=false;
    private String addressState="0000";
    private int position=0;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_first, container, false);//得到对应的布局文件
        return view;
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        int nameItem[] = {1,2,4,5,33,34};
        String Name[] = ResourceUtil.getName(getContext(),nameItem);
        for(int i=0;i<nameItem.length;i++)
            map.put(nameItem[i],i);
        //选择型
        show(Name);
        //输入型
        mListView = (ListView) getActivity().findViewById(R.id.list_view0);
        mData = new ArrayList<ItemBean>();
        mData.add(new ItemBean(getString(R.string.A0_03),"HZ","0.0~300.00",0.01f,"0.0~300.00","0003",1,0));
        mAdapter = new ValueListViewAdapter(this.getActivity(), mData);
        mAdapter.setAddressNoListener(new ValueListViewAdapter.AddressNoListener() {
            @Override
            public void clickListener(String address, String name,String Unit,String Hint, float min, String defalutValue,String currentValue
            ,int group, int position) {
                showSetDataDialog(address,defalutValue,currentValue);
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
        Intent BLEIntent = new Intent(getActivity(), BleService.class);
        getActivity().bindService(BLEIntent,connection, Context.BIND_AUTO_CREATE);
        localBroadcastManager = LocalBroadcastManager.getInstance(getContext());
        //if(!util.isRegister(localBroadcastManager,BLEService.ACTION_DATA_AVAILABLE))
            localBroadcastManager.registerReceiver(receiver,util.makeGattUpdateIntentFilter());
    }

    /**
     * 选择型
     */
    private void show(String Name[]) {
        List<Text> texts = new ArrayList<Text>();
//        String options[][]={getResources().getStringArray(R.array.A0_options)[0].split(","),
//                getResources().getStringArray(R.array.A0_options)[1].split(",")};
        int num[] = {0,1,2,3,13,14};
        String options[][] = ResourceUtil.getOptions(getResources(),num);
        for (int i = 0; i < 6; i++) {//自定义的Text类存数据
            text = new Text(Name[i],options[i],chooseAddressList[i],0);
            text.setCurrent(""+i);
            texts.add(text);
            spinnerListViewAdapter = new SpinnerListViewAdapter(this.getActivity(), texts, R.layout.item_spinner);//向自定义的Adapter中传值
            spinnerListViewAdapter.setAddressNoListener(new SpinnerListViewAdapter.AddressNoListener() {
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
            listView.setAdapter(spinnerListViewAdapter);//传值到ListView中

        }util.setListViewHeightBasedOnChildren(listView);
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

    private void showSetDataDialog(final String address,String defaultValue, String currentValue){
        try {
            setDatadialog = new SetDataDialog(this.getActivity(),getString(R.string.A0_03),
                    "HZ","0.0~300.00",defaultValue,currentValue);
            setDatadialog.setOnClickBottomListener(new SetDataDialog.OnClickBottomListener(){
                @Override
                public void onPositiveClick() {
                    editSetData = setDatadialog.getSetData();
                    try {
                        mBluetoothLeService.writeData(address, util.toByteData(editSetData, 0.01));
                        addressState = address;
                    }catch (Exception e){
                        util.centerToast(getActivity(),"Please input correct data",0);
                    }

                }
                @Override
                public void onNegativeClick() {
                    setDatadialog.gone();

                }
            });
        }catch(Exception e){
            Log.d(TAG,"SetDataDialog error");
        }
    }

    /**
     * 得到服务实例
     */
    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mBluetoothLeService = ((BleService.localBinder) service)
                    .getService();

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    private void initData(){
        loadingDialog = showLoadingDialog(getContext());
        position=0;
        mBluetoothLeService.readData("0001","0001");
    }

    @Override
    public void onStart() {
        super.onStart();
        if(Showing) {
//            ImageView icon = getActivity().findViewById(R.id.icon_status);
//            icon.setActivated(true);
//            initService();
            if(initializing){
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        initData();
                    }
                },500);
            }

        }

    }

    @Override
    public void onStop() {
        super.onStop();
        if(!(localBroadcastManager==null))
            localBroadcastManager.unregisterReceiver(receiver);
    }

    @Override
    public void onPause() {
        super.onPause();
        if(!(localBroadcastManager==null))
            localBroadcastManager.unregisterReceiver(receiver);
//        if(Showing) {
//            util.centerToast(getActivity(), "1的广播被停了", 0);
//        }
    }

    public FirstpageFragment newInstance(int i) {
        Bundle args = new Bundle();
        args.putInt("int", i);
        FirstpageFragment fragment = new FirstpageFragment();
//        fragment.setArguments(args);
        return fragment;
    }

    /**
     * 接收广播后具体行为
     */
    private class LocalReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.d(TAG,action);
            //有数据来
            if(action.equals(BleService.ACTION_DATA_AVAILABLE)) {
                byte[] message = intent.getByteArrayExtra(BleService.EXTRA_MESSAGE_DATA);
                if(initializing) {
                    reloadAll(message, position);
                }else {
                    util.centerToast(getContext(), getString(R.string.succeed), 0);
                    if(addressState=="0003")
                        mData.get(0).setCurrentValue(util.parseByteData(message,4,0.01f,false));
                        mAdapter.notifyDataSetChanged();
                        addressState="9999";
                    if (!(setDatadialog == null))
                        setDatadialog.gone();
                }
            }
            //蓝牙未连接
            else if(action.equals(BleService.ACTION_GATT_DISCONNECTED)) {
                util.centerToast(getContext(),"Bluetooth disconnected!",0);
            }
            //错误码
            else if(action.equals(BleService.ACTION_ERROR_CODE)){
                String errorCode = intent.getStringExtra(BleService.ACTION_ERROR_CODE);
                Toast toast = Toast.makeText(getContext(),"error code:"+errorCode,Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER,0,0);
                toast.show();

            }
        }
    }

    LoadingDialog showLoadingDialog(Context context){
        final LoadingDialog loadingDialog1 = new LoadingDialog(context,"",
                getString(R.string.loading),true);
        loadingDialog1.setOnClickCancelListener(new LoadingDialog.OnClickCancelListener() {
            @Override
            public void onNegativeClick() {
                initializing = false;
                loadingDialog1.gone();
            }

        });
        return loadingDialog1;
    }

    private void reloadAll(byte[] message, int position){
        if(position==6){
            String defaultValue = util.parseByteData(message,3,0.01f,false);
            mData.get(0).setCurrentValue(defaultValue);     //外面显示的值
            mData.get(0).setDefaultValue(defaultValue);
            mAdapter.notifyDataSetChanged();
            initializing = false;
            loadingDialog.gone();
            return;
        }
        reloadItem(message, position);
        this.position++;
        if(this.position<6)
            delayRead(chooseAddressList[this.position]);
        else
            delayRead("0003");
    }

    private void reloadItem(byte[] message, int positon){
        //message[4]最多不会超过8
        spinnerListViewAdapter.getItem(positon).setId(message[4]);
        spinnerListViewAdapter.notifyDataSetChanged();
    }

    private void delayRead(final String address){
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mBluetoothLeService.readData(address, "0001");
            }
        }, BleService.reloadGap);
    }






}