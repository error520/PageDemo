package com.kinco.MotorApp.ui.firstpage;


import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.kinco.MotorApp.BluetoothService.BLEService;
import com.kinco.MotorApp.LanguageUtils.LanguageUtil;
import com.kinco.MotorApp.LanguageUtils.PrefUtils;
import com.kinco.MotorApp.MainActivity;
import com.kinco.MotorApp.ParameterItem.CWordAdapter;
import com.kinco.MotorApp.alertdialog.LoadingDialog;
import com.kinco.MotorApp.alertdialog.SetDataDialog;
import com.kinco.MotorApp.ParameterItem.ItemBean;
import com.kinco.MotorApp.ParameterItem.ListViewAdapter;
import com.kinco.MotorApp.ParameterItem.Text;
import com.kinco.MotorApp.ParameterItem.TextAdapter;
import com.kinco.MotorApp.utils.XmlUtil;
import com.kinco.MotorApp.utils.util;
import com.kinco.MotorApp.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;


public class FirstMoreActivity extends AppCompatActivity implements View.OnClickListener {
    private String writeAddressList[] = {"0000","0003","0006","0007","0010","0012","0013","0014","0015","0016"
            ,"0018","0019","001A","001B","001C","001D","001E","001F","0020","0024","0027"};
    private String chooseAddressList[] = {"0001","0002","0004","0005","0008","0009","000A","000B","000C","000D",
            "000E","000F","0011","0021","0022","0023","0025","0026","0028","0029","002A","002B","002C","002D"};
    private List<String> addressList= new ArrayList<>();
    private ListView listView;
    private ListView mListView;
    private ListViewAdapter mAdapter;
    private List<ItemBean> mData;

    private TextAdapter A1CAdapter;
    private List<Text> A1CList = new ArrayList<Text>();
    private ListView A1Clv;
    private ListViewAdapter A1TAdapter;
    private List<ItemBean> A1TList = new ArrayList<>();
    private ListView A1Tlv;

    private TextAdapter A2CAdapter;
    private List<Text> A2CList = new ArrayList<>();
    private ListView A2Clv;
    private ListViewAdapter A2TAdapter;
    private List<ItemBean> A2TList = new ArrayList<>();;
    private ListView A2Tlv;

    private TextAdapter A3CAdapter;
    private List<Text> A3CList = new ArrayList<>();
    private ListView A3Clv;

    private TextAdapter A4CAdapter;
    private List<Text> A4CList = new ArrayList<>();
    private ListView A4Clv;
    private ListViewAdapter A4TAdapter;
    private List<ItemBean> A4TList = new ArrayList<>();
    private ListView A4Tlv;

    private CWordAdapter CWAdapter;

    private TextAdapter[] CAdapters = new TextAdapter[4];
    private ListViewAdapter[] TAdapters = {A1TAdapter,A2TAdapter,A4TAdapter};
    private HashMap<String,Object> map = new HashMap<>();
    private String mAddress;
    private Iterator<String> mIterator;

    private BLEService mBluetoothLeService;
    LocalBroadcastManager localBroadcastManager;
    private BroadcastReceiver receiver=new LocalReceiver();
    private SetDataDialog setDatadialog;
    public static boolean initializing = false;
    private Handler mHandler;
    private LoadingDialog loadingDialog;
    private String TAG = "FirstMoreActivity";


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setTitle("");
        setContentView(R.layout.setting_2);
        mHandler = new Handler();
        initUI();
//        for(Map.Entry<String,Object> entry: map.entrySet())
//            Log.d(TAG, entry.getKey()+":"+entry.getValue().toString());

            //Log.d(TAG,entry.getValue().toString());
    }

    private void initUI(){
        A1Clv = findViewById(R.id.A1Clist);
        A1Tlv = findViewById(R.id.A1Tlist);
        A2Clv = findViewById(R.id.A2Clist);
        A2Tlv = findViewById(R.id.A2Tlist);
        A3Clv = findViewById(R.id.A3Clist);
        A4Clv = findViewById(R.id.A4Clist);
        A4Tlv = findViewById(R.id.A4Tlist);
        ListView CWlv = findViewById(R.id.CWlist);
        Button btnInit = findViewById(R.id.btnInit);
        btnInit.setOnClickListener(this);

        A1CAdapter = new TextAdapter(this,A1CList,R.layout.main_item);
        A2CAdapter = new TextAdapter(this,A2CList,R.layout.main_item);
        A3CAdapter = new TextAdapter(this,A3CList,R.layout.main_item);
        A4CAdapter = new TextAdapter(this,A4CList,R.layout.main_item);

        A1TAdapter = new ListViewAdapter(this,A1TList);
        A2TAdapter = new ListViewAdapter(this,A2TList);
        A4TAdapter = new ListViewAdapter(this,A4TList);

        String[] options = getResources().getStringArray(R.array.CWord_options);
        CWAdapter = new CWordAdapter(options);
        CWlv.setAdapter(CWAdapter);
        util.setListViewHeightBasedOnChildren(CWlv);
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
        for(int i=0; i<num.length; i++){
            String address = "00"+String.format("%02X",num[i]);
            Text text = new Text(Name[i],options[i],address,0);
            list.add(text);
            map.put(address,text);
        }
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
            String address = "00"+String.format("%02X",num[i]);
            ItemBean ib = new ItemBean(Name[i],Unit[i],Hint[i],Min[i],Hint[i],address ,group,i);
            map.put(address,ib);
            TList.add(ib);
        }
        TAdapter.setAddressNoListener(new ListViewAdapter.AddressNoListener() {
            @Override
            public void clickListener(String Address, String Name, String Unit, String Range, float Min, String defaultValue, String currentValue,
                                      int group, int position) {
                showSetDataDialog(Address,Name,Unit,Range,Min,defaultValue,currentValue);
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
                                   final float min, String defaultValue, String currentValue){
        try {
            setDatadialog = new SetDataDialog(this,title,Unit,Range,defaultValue,currentValue);
            setDatadialog.setOnClickBottomListener(new SetDataDialog.OnClickBottomListener(){
                @Override
                public void onPositiveClick() {
                    byte data[] = util.toByteData(setDatadialog.getSetData(),min);
                    mBluetoothLeService.writeData(address,data);
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
            case R.id.btnInit:
                initializing = true;
                initData();
                break;
        }
    }

    public void sendCW(View v){
        byte[] data = CWAdapter.getData();
        mBluetoothLeService.writeData("0017",data);
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
                final byte[] message = intent.getByteArrayExtra(BLEService.EXTRA_MESSAGE_DATA);
                if(initializing){
                    reloadAll(message);
                }else{
                    //确保是设置的回应报文
                    if(message.length==8){
                        String address = util.toHexString(message,2,false);
                        reloadItem(address,message);
                        util.centerToast(FirstMoreActivity.this,getString(R.string.succeed),0);
                    }
                }

            }
            else if(action.equals(BLEService.ACTION_GATT_DISCONNECTED)) {
                util.centerToast(context, getString(R.string.bluetooth_disconnected), Toast.LENGTH_SHORT);
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
            initData();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    //初始化参数
    private void initData(){
        if(initializing){
            Set<String> keys=map.keySet();
            mIterator=keys.iterator();
            mAddress = mIterator.next();
            mBluetoothLeService.readData(mAddress,"0001");
            if(mBluetoothLeService.mConnected){
                loadingDialog = showLoadingDialog();
            }
        }
    }

    private void delayRead(final String address){
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mBluetoothLeService.readData(address, "0001");
            }
        },BLEService.reloadGap);
    }

    LoadingDialog showLoadingDialog(){
        final LoadingDialog loadingDialog = new LoadingDialog(this,"",
                getString(R.string.loading),true);
        loadingDialog.setOnClickCancelListener(new LoadingDialog.OnClickCancelListener() {
            @Override
            public void onNegativeClick() {
                initializing = false;
                loadingDialog.gone();
            }

        });
        return loadingDialog;
    }

    private void reloadAll(byte[] message){
        reloadItem(mAddress,message);
        if(mIterator.hasNext()){
            mAddress= mIterator.next();
            delayRead(mAddress);
        }else{
            if(loadingDialog!=null)
                loadingDialog.gone();
            initializing = false;
        }
    }

    private void reloadItem(String address, byte[] message){
        try {
            if(address.equals("0017"))
                return;
            Object ob = map.get(address);
            //Log.d(TAG,map.get(address)+"");
            if (ob instanceof Text) {
                int idIndex = initializing?4:5;
                ((Text) ob).setId(message[idIndex]);
                A1CAdapter.notifyDataSetChanged();
                A2CAdapter.notifyDataSetChanged();
                A3CAdapter.notifyDataSetChanged();
                A4CAdapter.notifyDataSetChanged();
            } else {
                int offset = 4;
                if (initializing)
                    offset = 3;
                boolean Sign = false;
                //判断是否是有符号数
                if(address.equals("0027"))
                    Sign = true;
                String currentValue = util.parseByteData(message, offset, ((ItemBean) ob).getMin(), Sign);
                //初始化时才把设置默认值
                if (initializing)
                    ((ItemBean) ob).setDefaultValue(currentValue);
                ((ItemBean) ob).setCurrentValue(currentValue);
                A1TAdapter.notifyDataSetChanged();
                A2TAdapter.notifyDataSetChanged();
                A4TAdapter.notifyDataSetChanged();
            }
        }catch (Exception e){
            Log.d(TAG,e.toString());
        }


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
        String appLang = PrefUtils.getLanguage(context);
        return appLang ;
    }


}


