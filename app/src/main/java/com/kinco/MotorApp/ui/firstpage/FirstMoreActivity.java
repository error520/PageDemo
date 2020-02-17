package com.kinco.MotorApp.ui.firstpage;


import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.util.Xml;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.kinco.MotorApp.BluetoothService.BLEService;
import com.kinco.MotorApp.MainActivity;
import com.kinco.MotorApp.alertdialog.LoadingDialog;
import com.kinco.MotorApp.alertdialog.SetDataDialog;
import com.kinco.MotorApp.edittext.ItemBean;
import com.kinco.MotorApp.edittext.ListViewAdapter;
import com.kinco.MotorApp.edittext.Text;
import com.kinco.MotorApp.edittext.TextAdapter;
import com.kinco.MotorApp.utils.XmlUtil;
import com.kinco.MotorApp.utils.util;
import com.kinco.MotorApp.R;

import java.util.ArrayList;
import java.util.List;


public class FirstMoreActivity extends AppCompatActivity implements View.OnClickListener {
    private String writeAddressList[] = {"0000","0003","0006","0007","0010","0012","0013","0014","0015","0016"
            ,"0018","0019","001A","001B","001C","001D","001E","001F","0020","0024","0027"};
    private String chooseAddressList[] = {"0001","0002","0004","0005","0008","0009","000A","000B","000C","000D",
            "000E","000F","0011","0021","0022","0023","0025","0026","0028","0029","002A","002B","002C","002D"};
    private ListView listView;
    private ListView mListView;
    private ListViewAdapter mAdapter;
    private List<ItemBean> mData;

    private TextAdapter A1CAdapter;
    private List<Text> A1CList;
    private ListView A1Clv;
    private ListViewAdapter A1TAdapter;
    private List<ItemBean> A1TList;
    private ListView A1Tlv;

    private TextAdapter A2CAdapter;
    private List<Text> A2CList;
    private ListView A2Clv;
    private ListViewAdapter A2TAdapter;
    private List<ItemBean> A2TList;
    private ListView A2Tlv;

    private TextAdapter A3CAdapter;
    private List<Text> A3CList;
    private ListView A3Clv;

    private TextAdapter A4CAdapter;
    private List<Text> A4CList;
    private ListView A4Clv;
    private ListViewAdapter A4TAdapter;
    private List<ItemBean> A4TList;
    private ListView A4Tlv;


    private BLEService mBluetoothLeService;
    LocalBroadcastManager localBroadcastManager;
    private BroadcastReceiver receiver=new LocalReceiver();
    private SetDataDialog setDatadialog;
    boolean initialized = false;
    private int count=0;
    private Handler mHandler;
    private LoadingDialog loadingDialog;
    private String TAG = "FirstMoreActivity";
    private int mGroup=1;   //当前位置
    private int mPosition=0;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_2);
        mHandler = new Handler();
        initUI();

//        //输入型
//        mListView = (ListView) findViewById(R.id.A1Tlist);
//        mData = new ArrayList<ItemBean>();
//        for(int i=0;i<Name2.length;i++){
//            mData.add(new ItemBean( Name2[i], Unit[i],Hint[i],Min[i],Hint[i],writeAddressList[i]));
//        }
//        mAdapter = new ListViewAdapter(this, mData);
//        mAdapter.setAddressNoListener(new ListViewAdapter.AddressNoListener() {
//            @Override
//            public void clickListener(String Address, String Name,String Unit,
//                                      String Range,float Min,String defaultValue,String currentValue) {
//                showSetDataDialog(Address,Name,Unit,Range,Min,defaultValue,currentValue);
//            }
//        });
//
//        mListView.setAdapter(mAdapter);
//        util.setListViewHeightBasedOnChildren(mListView);

//        Button button2 = (Button) findViewById(R.id.Control_111B);
//        button2.setOnClickListener(this);
//        Button button3 = (Button) findViewById(R.id.Control_110B);
//        button3.setOnClickListener(this);
//        Button button4 = (Button) findViewById(R.id.Control_101B);
//        button4.setOnClickListener(this);
//        Button button5 = (Button) findViewById(R.id.Control_100B);
//        button5.setOnClickListener(this);
//        Button button6 = (Button) findViewById(R.id.Control_011B);
//        button6.setOnClickListener(this);
//        Button button7 = (Button) findViewById(R.id.Control_bit3_0);
//        button7.setOnClickListener(this);
//        Button button8 = (Button) findViewById(R.id.Control_bit3_1);
//        button8.setOnClickListener(this);
//        Button button9 = (Button) findViewById(R.id.Control_bit4_0);
//        button9.setOnClickListener(this);
//        Button button10 = (Button) findViewById(R.id.Control_bit4_1);
//        button10.setOnClickListener(this);
//        Button button11 = (Button) findViewById(R.id.Control_bit5_0);
//        button11.setOnClickListener(this);
//        Button button12 = (Button) findViewById(R.id.Control_bit5_1);
//        button12.setOnClickListener(this);
//        Button button13 = (Button) findViewById(R.id.Control_bit6_0);
//        button13.setOnClickListener(this);
//        Button button14 = (Button) findViewById(R.id.Control_bit6_1);
//        button14.setOnClickListener(this);
//        Button button15 = (Button) findViewById(R.id.Control_bit7_0);
//        button15.setOnClickListener(this);
//        Button button16 = (Button) findViewById(R.id.Control_bit7_1);
//        button16.setOnClickListener(this);
//        Button button17 = (Button) findViewById(R.id.Control_bit8_0);
//        button17.setOnClickListener(this);
//        Button button18 = (Button) findViewById(R.id.Control_bit8_1);
//        button18.setOnClickListener(this);
//        Button button19 = (Button) findViewById(R.id.Control_bit9_0);
//        button19.setOnClickListener(this);
//        Button button20 = (Button) findViewById(R.id.Control_bit9_1);
//        button20.setOnClickListener(this);

        Log.d(TAG,Float.parseFloat(getResources().getString(R.string.A0_03M))+"ddd");
    }

    private void initUI(){
        A1Clv = findViewById(R.id.A1Clist);
        A1Tlv = findViewById(R.id.A1Tlist);
        A2Clv = findViewById(R.id.A2Clist);
        A2Tlv = findViewById(R.id.A2Tlist);
        A3Clv = findViewById(R.id.A3Clist);
        A4Clv = findViewById(R.id.A4Clist);
        A4Tlv = findViewById(R.id.A4Tlist);

        A1TList = new ArrayList<>();
        A2TList = new ArrayList<>();
        A4TList = new ArrayList<>();
        A1TAdapter = new ListViewAdapter(this,A1TList);
        A2TAdapter = new ListViewAdapter(this,A2TList);
        A4TAdapter = new ListViewAdapter(this,A4TList);
        int cNum1[] = {37};
        int cNum2[] = {46};
        int cNum3[] = {10,8,9,40,41,42,43,14,44,12,45,11,13};
        int cNum4[] = {15,17,35,2,38};
        setCListView(cNum1,A1CAdapter,A1CList,A1Clv);
        setCListView(cNum2,A2CAdapter,A2CList,A2Clv);
        setCListView(cNum3,A3CAdapter,A3CList,A3Clv);
        setCListView(cNum4,A4CAdapter,A4CList,A4Clv);
        int tNum1[] = {18,24,25,26,27,19,20,28,29,30,21};
        int tNum2[] = {32,47,48,49,50,51,52,53};
        int tNum4[] = {6,7,31,36,16,22,3,39};
        setTListView(tNum1,A1TAdapter,A1TList,A1Tlv,1);
        setTListView(tNum2,A2TAdapter,A2TList,A2Tlv,2);
        setTListView(tNum4,A4TAdapter,A4TList,A4Tlv,4);

    }

    /**
     * 设置选择参数
     * @param num
     * @param textAdapter
     * @param list
     * @param listView
     */
    private void setCListView(int num[], TextAdapter textAdapter, List<Text> list, ListView listView){
        String Name[] = XmlUtil.getName(this,num);
        String options[][] = XmlUtil.getOptions(this,num);
        list = new ArrayList<Text>();
        for(int i=0; i<num.length; i++){
            String address = "00"+String.format("%x",num[i]);
            if(address.length()<4)
                address = "000"+String.format("%x",num[i]);
            Text text = new Text(Name[i],options[i],address,0);
            list.add(text);
        }
        textAdapter = new TextAdapter(this, list, R.layout.main_item);//向自定义的Adapter中传值
        textAdapter.setAddressNoListener(new TextAdapter.AddressNoListener() {
            //操作
            @Override
            public void titleNo(String address, String value) {
                mBluetoothLeService.writeData(address,value);
            }
            public void addressNo(int addressNo) {
            }
        });
        listView.setAdapter(textAdapter);//传值到ListView中
        util.setListViewHeightBasedOnChildren(listView);

    }

    private void setTListView(int num[], ListViewAdapter TAdapter, List<ItemBean> TList, ListView listView, int group){
        String Name[] = XmlUtil.getName(this,num);
        String Unit[] = XmlUtil.getUnit(this,num);
        String Hint[] = XmlUtil.getHint(this,num);
        float Min[] = XmlUtil.getMin(this,num);
        for(int i=0; i<num.length; i++){
            String address = "00"+String.format("%x",num[i]);
            if(address.length()<4)
                address = "000"+String.format("%x",num[i]);
            ItemBean ib = new ItemBean(Name[i],Unit[i],Hint[i],Min[i],Hint[i],address ,group,i);
            TList.add(ib);
        }
        TAdapter.setAddressNoListener(new ListViewAdapter.AddressNoListener() {
            @Override
            public void clickListener(String Address, String Name, String Unit, String Range, float Min, String defaultValue, String currentValue,
                                      int group, int position) {
                showSetDataDialog(Address,Name,Unit,Range,Min,defaultValue,currentValue,group,position);
            }
        });
        listView.setAdapter(TAdapter);
        util.setListViewHeightBasedOnChildren(listView);

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
        unbindService(connection);
    }


    private void showSetDataDialog(final String address, String title, String Unit, String Range,
                                   final float min, String defaultValue, String currentValue,
                                   final int group, final int position){
        try {
            setDatadialog = new SetDataDialog(this,title,Unit,Range,defaultValue,currentValue);
            setDatadialog.setOnClickBottomListener(new SetDataDialog.OnClickBottomListener(){
                @Override
                public void onPositiveClick() {
                    byte data[] = util.toByteData(setDatadialog.getSetData(),min);
                    mBluetoothLeService.writeData(address,data);
                    mGroup = group;
                    mPosition = position;
                }
                @Override
                public void onNegativeClick() {
                    setDatadialog.gone();

                }
            });
        }catch(Exception e){
            Log.d("FirstMoreActivity","SetDataDialog error");
        }

    }

    private void initService(){
        //绑定服务
        Intent BLEIntent = new Intent(this, BLEService.class);
        bindService(BLEIntent,connection, Context.BIND_AUTO_CREATE);
        localBroadcastManager = LocalBroadcastManager.getInstance(this);
        localBroadcastManager.registerReceiver(receiver,util.makeGattUpdateIntentFilter());
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
//            case R.id.Control_111B:
//                //mBluetoothLeService.writeData("0017","00CF");
//                break;
//            case R.id.Control_110B:
//                Toast.makeText(this,"方式0停车",Toast.LENGTH_SHORT).show();
//                break;
//            case R.id.Control_101B:
//                Toast.makeText(this,"方式1停车",Toast.LENGTH_SHORT).show();
//                break;
//            case R.id.Control_100B:
//                Toast.makeText(this,"外部故障停车",Toast.LENGTH_SHORT).show();
//                break;
//            case R.id.Control_011B:
//                Toast.makeText(this,"方式2停车",Toast.LENGTH_SHORT).show();
//                break;
//            case R.id.Control_bit3_0:
//                mBluetoothLeService.writeData("0017","00C7");
//                break;
//            case R.id.Control_bit3_1:
//                Toast.makeText(this,"反转",Toast.LENGTH_SHORT).show();
//                break;
//            case R.id.Control_bit4_0:
//                Toast.makeText(this,"点动正转无效",Toast.LENGTH_SHORT).show();
//                break;
//            case R.id.Control_bit4_1:
//                Toast.makeText(this,"点动正转",Toast.LENGTH_SHORT).show();
//                break;
//            case R.id.Control_bit5_0:
//                Toast.makeText(this,"点动反转无效",Toast.LENGTH_SHORT).show();
//                break;
//            case R.id.Control_bit5_1:
//                Toast.makeText(this,"点动反转",Toast.LENGTH_SHORT).show();
//                break;
//            case R.id.Control_bit6_0:
//                Toast.makeText(this,"允许加减速",Toast.LENGTH_SHORT).show();
//                break;
//            case R.id.Control_bit6_1:
//                Toast.makeText(this,"禁止加减速",Toast.LENGTH_SHORT).show();
//                break;
//            case R.id.Control_bit7_0:
//                Toast.makeText(this,"上位机控制字1有效",Toast.LENGTH_SHORT).show();
//                break;
//            case R.id.Control_bit7_1:
//                Toast.makeText(this,"上位机控制字1无效",Toast.LENGTH_SHORT).show();
//                break;
//            case R.id.Control_bit8_0:
//                Toast.makeText(this,"主给定有效",Toast.LENGTH_SHORT).show();
//                break;
//            case R.id.Control_bit8_1:
//                Toast.makeText(this,"主给定无效",Toast.LENGTH_SHORT).show();
//                break;
//            case R.id.Control_bit9_0:
//                Toast.makeText(this,"故障复位有效",Toast.LENGTH_SHORT).show();
//                break;
//            case R.id.Control_bit9_1:
//                Toast.makeText(this,"故障复位无效",Toast.LENGTH_SHORT).show();
//                break;
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

    //每个活动中对广播的响应不相同
    private class LocalReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(action.equals(BLEService.ACTION_DATA_AVAILABLE)) {
                byte[] message = intent.getByteArrayExtra(BLEService.EXTRA_MESSAGE_DATA);
                util.centerToast(FirstMoreActivity.this,"suceed!",0);
                ItemBean ib = A1TList.get(0);
                if(mGroup==1)
                   ib = A1TList.get(mPosition);
                else if(mGroup==2)
                    ib = A2TList.get(mPosition);
                else if(mGroup==4)
                    ib = A4TList.get(mPosition);
                String currentValue = util.parseByteData(message,4,ib.getMin(),false);
                ib.setCurrentValue(currentValue);
                A1TAdapter.notifyDataSetChanged();
                A2TAdapter.notifyDataSetChanged();
                A4TAdapter.notifyDataSetChanged();
//                if(!initialized){
//                    ItemBean ib = mData.get(count);
//                    String currentValue = util.parseByteData(message,3,ib.getMin(),false);
//                    ib.setDefaultValue(currentValue);
//                    ib.setCurrentValue(currentValue);
//                    mAdapter.notifyDataSetChanged();
//                    if(count<9){
//                        delayRead(writeAddressList[count+1]);
//                        count++;
//                    }else{
//                        count=0;
//                        initialized = true;
//                    }
//
//                }else{
//                    //setDatadialog.gone();
//                    String address = util.toHexString(message,2,false);
//                    Log.d("FirstMore",address);
//                    int index=0;
//                    for(int i=0;i<writeAddressList.length;i++){
//                        if(address.equals(writeAddressList[i])){
//                            index=i;
//                            break;
//                        }
//                    }
//                    ItemBean ib = mData.get(index);
//                    mData.get(index).setCurrentValue(util.parseByteData(message,4,ib.getMin(),false));
//                    mAdapter.notifyDataSetChanged();
//                }

            }
            else if(action.equals(BLEService.ACTION_GATT_DISCONNECTED)) {
                Toast.makeText(context, "Bluetooth disconnected!", Toast.LENGTH_SHORT).show();
            }
            else if(action.equals(BLEService.ACTION_ERROR_CODE)){
                String errorCode = intent.getStringExtra(BLEService.ACTION_ERROR_CODE);
                Toast toast = Toast.makeText(FirstMoreActivity.this,"error code:"+errorCode,Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER,0,0);
                toast.show();

            }
        }
    }

    /**
    得到服务实例
     */
    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mBluetoothLeService = ((BLEService.localBinder) service)
                    .getService();
//            if(!initialized) {
//                mBluetoothLeService.readData("0000", "0001");
//
//            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    private void delayRead(final String address){
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mBluetoothLeService.readData(address, "0001");
            }
        },500);
    }

    LoadingDialog showLoadingDialog(Context context){
        final LoadingDialog loadingDialog = new LoadingDialog(context,"",
                "loading...",true);
        loadingDialog.setOnClickCancelListener(new LoadingDialog.OnClickCancelListener() {
            @Override
            public void onNegativeClick() {
                initialized = true;
                loadingDialog.gone();
            }

        });
        return loadingDialog;
    }
    private String[][] getTwoDimensionalArray(String[] array) {
        Log.d(TAG,array.length+"");
        String[][] twoDimensionalArray = null;
        for (int i = 0; i < array.length; i++) {
            String[] tempArray = array[i].split(",");
            if (twoDimensionalArray == null) {
                twoDimensionalArray = new String[array.length][tempArray.length];
            }
            for (int j = 0; j < tempArray.length; j++) {
                twoDimensionalArray[i][j] = tempArray[j];
            }
        }
        return twoDimensionalArray;
    }


}


