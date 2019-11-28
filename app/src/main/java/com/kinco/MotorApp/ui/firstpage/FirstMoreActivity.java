package com.kinco.MotorApp.ui.firstpage;


import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.kinco.MotorApp.BluetoothService.BLEService;
import com.kinco.MotorApp.edittext.ItemBean;
import com.kinco.MotorApp.edittext.ListViewAdapter;
import com.kinco.MotorApp.edittext.Text;
import com.kinco.MotorApp.edittext.TextAdapter;
import com.kinco.MotorApp.util;
import com.kinco.MotorApp.R;

import java.util.ArrayList;
import java.util.List;


public class FirstMoreActivity extends Activity implements View.OnClickListener {

    /** Called when the activity is first created. */

    String[] Name =  { "Control mode", "Main reference  frequency selector", "Methods of  inputting operating  commands", "Running direction   " ,"Input terminal X1 function selection","Input terminal  X2 function selection" ,
                        "Terminal control mode selection","Bi-direction pen-collector output terminal Y1","Output functions  of relay R1","Y1 terminal output","Functions of   terminal AO1","PG type ","Fault masking  selection 1"};
    String[][] temp = {{"0：Vector control without PG","1: Vector control with PG","2:V/F control"},
                        {"0:Digital setting Keyboard UP/DN or terminal UP/DN ","1:AI1","2:AI2","3:AI3","4:Set via DI terminal(PULSE","5:Reserved"},
            {"0:Panel control","1:Terminal control","2:Communication control"},
            {"0:Forward","1:Reverse"},
            {"0:No function"," 1:Forward","2:Reverse","3:Forward jog operation","4:Reverse jog operation","5:3-wire operation control","6:External RESET signal input","7:External fault signal input","8:External interrupt signal input",
             "9:Drive operation prohibit","10:External stop command","11:DC injection braking command","12:Coast to stop","13:Frequency ramp up (UP)","14:Frequency ramp down (DN)","15:Switch to panel control","16:Switch to terminal control",
            "17:Switch to communication control mode","18:Main reference frequency via AI1 ","19:Main reference frequency via AI2"," 20:Main reference frequency via AI3 ","21:Main reference frequency via DI ","22:Auxiliary reference frequency invalid" ,
                    "23:Auxiliary reference frequency via AI1 (Reserved)" ,
                    "24:Auxiliary reference frequency via AI2 (Reserved)" ,
                    "25:Auxiliary reference frequency via AI3 (Reserved)" ,
                    "26:Auxiliary reference frequency via DI(Reserved)" ,
                    "27:Preset frequency 1" ,
                    "28:Preset frequency 2" ,
                    "29:Preset frequency 3" ,
                    "30:Preset frequency 4" ,
                    "31:Acc/Dec time 1" ,
                    "32:Acc/Dec time 2" ,
                    "33:Multiple close-loop reference selection 1" ,
                    "34:Multiple close-loop reference selection 2 " ,
                    "35:Multiple close-loop reference selection 3 " ,
                    "36:Multiple close-loop reference selection 4 " ,
                    "37:Forward prohibit " ,
                    "38:Reverse prohibit " ,
                    "39:Acc/Dec prohibit " ,
                    "40:Process close-loop prohibit " ,
                    "41:Speed and torque control switching terminal" ,
                    "42:Main frequency switch to digital setting" ,
                    "43:PLC pause ",
                    "44:PLC prohibit" ,
                    "45:PLC stop memory clear " ,
                    "46:Swing input" ,
                    "47:Swing reset" ,
                    "48~50：Reserved" ,
                    "51：Timer1 start","52：Timer2 start","53：Counter start","54：Counter reset","Others.Reserved"},
            {"0:No function" ,
                    "1:Forward " ,
                    "2:Reverse" ,
                    "3:Forward jog operation " ,
                    "4:Reverse jog operation " ,
                    "5:3-wire operation control" ,
                    "6:External RESET signal input ",
                    "7:External fault signal input",
                    "8:External interrupt signal input ",
                    "9:Drive operation prohibit ",
                    "10:External stop command" ,
                    "11:DC injection braking command ",
                    "12:Coast to stop" ,
                    "13:Frequency ramp up (UP) ",
                    "14:Frequency ramp down (DN) " ,
                    "15:Switch to panel control ",
                    "16:Switch to terminal control" ,
                    "17:Switch to communication control mode" ,
                    "18:Main reference frequency via AI1 ",
                    "19:Main reference frequency via AI2 ",
                    "20:Main reference frequency via AI3 ",
                    "21:Main reference frequency via DI",
                    "22:Auxiliary reference frequency invalid" ,
                    "23:Auxiliary reference frequency via AI1 (Reserved)" ,
                    "24:Auxiliary reference frequency via AI2 (Reserved)" ,
                    "25:Auxiliary reference frequency via AI3 (Reserved)" ,
                    "26:Auxiliary reference frequency via DI(Reserved)" ,
                    "27:Preset frequency 1" ,
                    "28:Preset frequency 2" ,
                    "29:Preset frequency 3" ,
                    "30:Preset frequency 4" ,
                    "31:Acc/Dec time 1" ,
                    "32:Acc/Dec time 2",
                    "33:Multiple close-loop reference selection 1" ,
                    "34:Multiple close-loop reference selection 2  " ,
                    "35:Multiple close-loop reference selection 3 " ,
                    "36:Multiple close-loop reference selection 4 " ,
                    "37:Forward prohibit " ,
                    "38:Reverse prohibit " ,
                    "39:Acc/Dec prohibit " ,
                    "40:Process close-loop prohibit " ,
                    "41:Speed and torque control switching terminal" ,
                    "42:Main frequency switch to digital setting" ,
                    "43:PLC pause 44:PLC prohibit" ,
                    "45:PLC stop memory clear " ,
                    "46:Swing input" ,
                    "47:Swing reset" ,
                    "48~50：Reserved" ,
                    "51：Timer1 start" ,
                    "52：Timer2 start" ,
                    "53：Counter start" ,
                    "54：Counter reset" ,
                    "Others.Reserved"},
            {"0: 2-wire operating mode 1",
                    "1:2-wire operating mode 2 ",
                    "2: 3-wire operating mode 1",
                    "3: 3-wire operation mode 2",
                    "4: 2-wires operation mode 3"},
            {"0:Running signal(RUN)" ,
                    "1:Frequency arriving signal(FAR)" ,
                    "2:Frequency detection threshold (FDT1)" ,
                    "3:Frequency detection threshold (FDT2)" ,
                    "4:Overload detection signal(OL) " ,
                    "5:Low voltage signal(LU) " ,
                    "6:External fault stop signal(EXT) ",
                    "7:Frequency high limit(FHL) ",
                    "8:Frequency low limit(FLL) ",
                    "9:Zero-speed running ",
                    "10:Terminal X1(Reserved) ",
                    "11:Terminal X2(Reserved)",
                    "12:PLC running step complete signal 13:PLC running cycle complete signal",
                    "14:Limit of swing frequency upper/lower limit",
                    "15:Drive ready (RDY) ",
                    "16:Drive fault ",
                    "17:Switching signal of host 18:Reserved",
                    "19:Torque limiting",
                    "20:Drive running forward/reverse",
                    "21:Timer 1 full",
                    "22: Timer 2 full",
                    "23:Setting counter full",
                    "24: Intermediate counter",
                    "full",
                    " Others.Reserved"},
            {"0:Running signal(RUN)",
                    "1:Frequency arriving signal(FAR)",
                    "2:Frequency detection threshold (FDT1)",
                    "3:Frequency detection threshold (FDT2)",
                    "4:Overload detection signal(OL) ",
                    "5:Low voltage signal(LU)",
                    " 6:External fault stop signal(EXT)",
                    " 7:Frequency high limit(FHL)",
                    " 8:Frequency low limit(FLL)",
                    " 9:Zero-speed running ",
                    "10:Terminal X1(Reserved) ",
                    "11:Terminal X2(Reserved)",
                    "12:PLC running step complete signal",
                    "13:PLC running cycle complete signal",
                    "14:Limit of swing frequency upper/lower limit",
                    "15:Drive ready (RDY) ",
                    "16:Drive fault ",
                    "17:Switching signal of host 18:Reserved",
                    "19:Torque limiting",
                    "20:Drive running forward/reverse",
                    "21:Timer 1 full",
                    "22: Timer 2 full",
                    "23:Setting counter full",
                    "24: Intermediate counter full",
                    " Others.Reserved"},
            {"0:Running signal(RUN)",
                    "1:frequency arriving signal(FAR)",
                    "2:frequency detection threshold (FDT1)",
                    "3:frequency detection threshold (FDT2)",
                    "4:overload signal(OL) ",
                    "5:low voltage signal(LU) ",
                    "6:external fault signal(EXT",
                    "7:frequency high limit(FHL)",
                    " 8:frequency low limit(FLL) ",
                    "9:zero-speed running ",
                    "10:Terminal X1(Reserved) ",
                    "11:Terminal X2(Reserved)",
                    "12:PLC running step complete signal",
                    "13:PLC running cycle completesignal",
                    "14:Limit of swing frequency upper/lower limit",
                    "15:Drive ready (RDY) ",
                    "16:Drive fault",
                    " 17:Switching signal of host ",
                    "18:Reserved",
                    "19:Torque limiting",
                    "20:Drive running forward/reverse ",
                    "21~50:Reserved",
                    "51:Output frequency (0~ Max. output frequency)",
                    "52:Preset frequency (0~ Max. output frequency)",
                    "53:Preset frequency (After Acc/Dec)(0~ Max. output frequency)",
                    "54:Motor speed(0~ Max. speed) ",
                    "55:Output current(0~2*Iei)",
                    "56:Output current(0~2*Iem) ",
                    "57:Output torque(0~3*Tem) ",
                    "58:Output power(0~2*Pe)",
                    "59:Output voltage(0~1.2*Ve)",
                    "60:Bus voltage(0~800V) ",
                    "61:AI1",
                    "62:AI2",
                    "63: Keyboard potentiometer",
                    "64:DI",
                    "65:Percentage of host(0~4095)",
                    "66~88:Reserved"},
            {"0:No function",
                    "1:Output frequency(0~ Max. output frequency)",
                    "2:Preset frequency(0~ Max. output frequency) ",
                    "3:Preset frequency(After Acc/Dec)(0~ Max. output frequency) ",
                    "4:Motor speed(0~ Max. speed) ",
                    "5:Output current(0~2*Iei)",
                    "6:Output current(0~2*Iem)",
                    "7:Output torque(0~3*Tem)",
                    "8:Output power(0~2*Pe) ",
                    "9:Output voltage(0~1.2*Ve)",
                    "10:Bus voltage(0~800V) ",
                    "11:AI1",
                    "12:AI2",
                    "13:Keyboard potentiometer",
                    "14:DI",
                    "15:Percentage of host(0~4095) ",
                    "16~36:Reserved"},
            {"0:ABZ incremental type ",
                    "1:UVW incremental type ",
                    "2:Cosine type",
                    "3: Single pulse"},
            {"0:Disable.Stop when fault happen","1:Disable.Continue operating when fault happen","2:Enable"}};
    public String[] GroupArray = new String[]{"User password", "Digital reference frequency", "Acc time 1","Dec time 1",
            "Number of pulses per revolution of PG", "Rated power of AC motor 1", "Number of polarities 0f AC motor 1","Rated power of PMSM motor 1","Number of polarities 0f PMSM motor 1",
            "Parameter initialization"};
    private String writeAddressList[] = {"0000","0003","0006","0007","0010","0012","0013","0014","0015","0016"};
    private String chooseAddressList[] = {"0001","0002","0004","0005","0008","0009","000A","000B","000C","000D",
            "000E","000F","0011"};
    private String Unit[]={"1","0.01HZ","0.1S","0.1S","","0.1KW","","0.1KW","1","1"};
    private ListView listView;
    private Button button;
    private ListView mListView;
    private Button mButton;
    private TextView editName;
    private ListViewAdapter mAdapter;
    private List<ItemBean> mData;
    private BLEService mBluetoothLeService;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_2);
        initService();
        show();
        mListView = (ListView) findViewById(R.id.list_view);
        mData = new ArrayList<ItemBean>();
        //输入型
        for(int i=0;i<10;i++){
            mData.add(new ItemBean( GroupArray[i], Unit[i],"","",writeAddressList[i]));
        }
        mAdapter = new ListViewAdapter(this, mData);
        mAdapter.setAddressNoListener(new ListViewAdapter.AddressNoListener() {
            @Override
            public void clickListener(String address, String value) {
                mBluetoothLeService.writeData(address,value);
            }
        });
        mListView.setAdapter(mAdapter);
        util.setListViewHeightBasedOnChildren(mListView);
        Button button2 = (Button) findViewById(R.id.Control_111B);
        button2.setOnClickListener(this);
        Button button3 = (Button) findViewById(R.id.Control_110B);
        button3.setOnClickListener(this);
        Button button4 = (Button) findViewById(R.id.Control_101B);
        button4.setOnClickListener(this);
        Button button5 = (Button) findViewById(R.id.Control_100B);
        button5.setOnClickListener(this);
        Button button6 = (Button) findViewById(R.id.Control_011B);
        button6.setOnClickListener(this);
        Button button7 = (Button) findViewById(R.id.Control_bit3_0);
        button7.setOnClickListener(this);
        Button button8 = (Button) findViewById(R.id.Control_bit3_1);
        button8.setOnClickListener(this);
        Button button9 = (Button) findViewById(R.id.Control_bit4_0);
        button9.setOnClickListener(this);
        Button button10 = (Button) findViewById(R.id.Control_bit4_1);
        button10.setOnClickListener(this);
        Button button11 = (Button) findViewById(R.id.Control_bit5_0);
        button11.setOnClickListener(this);
        Button button12 = (Button) findViewById(R.id.Control_bit5_1);
        button12.setOnClickListener(this);
        Button button13 = (Button) findViewById(R.id.Control_bit6_0);
        button13.setOnClickListener(this);
        Button button14 = (Button) findViewById(R.id.Control_bit6_1);
        button14.setOnClickListener(this);
        Button button15 = (Button) findViewById(R.id.Control_bit7_0);
        button15.setOnClickListener(this);
        Button button16 = (Button) findViewById(R.id.Control_bit7_1);
        button16.setOnClickListener(this);
        Button button17 = (Button) findViewById(R.id.Control_bit8_0);
        button17.setOnClickListener(this);
        Button button18 = (Button) findViewById(R.id.Control_bit8_1);
        button18.setOnClickListener(this);
        Button button19 = (Button) findViewById(R.id.Control_bit9_0);
        button19.setOnClickListener(this);
        Button button20 = (Button) findViewById(R.id.Control_bit9_1);
        button20.setOnClickListener(this);
//
    }
    private void initService(){
        //绑定服务
        Intent BLEIntent = new Intent(this, BLEService.class);
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
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(this);
        localBroadcastManager.registerReceiver(new LocalReceiver(),util.makeGattUpdateIntentFilter());
    }

    private void show() {
        List<Text> texts = new ArrayList<Text>();
        for(int i=0;i<13;i++) {//自定义的Text类存数据
            Text text = new Text();
            text.setTitle(Name[i]);//标题数据
            text.setAddress(chooseAddressList[i]);
            text.setId(0);//Spinner的默认选择项
            text.setContent(temp[i]);
            texts.add(text);
            TextAdapter textAdapter = new TextAdapter(FirstMoreActivity.this, texts, R.layout.main_item);//向自定义的Adapter中传值
            textAdapter.setAddressNoListener(new TextAdapter.AddressNoListener() {
                //操作
                @Override
                public void titleNo(String address, String value) {
                    mBluetoothLeService.writeData(address,value);
                }
                public void addressNo(int addressNo) {
                }
            });
            listView = (ListView) findViewById(R.id.mylist);
            listView.setAdapter(textAdapter);//传值到ListView中
        }
//        util.setListViewHeightBasedOnChildren(listView);


    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.Control_111B:
                //mBluetoothLeService.writeData("0017","00CF");
                break;
            case R.id.Control_110B:
                Toast.makeText(this,"方式0停车",Toast.LENGTH_SHORT).show();
                break;
            case R.id.Control_101B:
                Toast.makeText(this,"方式1停车",Toast.LENGTH_SHORT).show();
                break;
            case R.id.Control_100B:
                Toast.makeText(this,"外部故障停车",Toast.LENGTH_SHORT).show();
                break;
            case R.id.Control_011B:
                Toast.makeText(this,"方式2停车",Toast.LENGTH_SHORT).show();
                break;
            case R.id.Control_bit3_0:
                mBluetoothLeService.writeData("0017","00C7");
                break;
            case R.id.Control_bit3_1:
                Toast.makeText(this,"反转",Toast.LENGTH_SHORT).show();
                break;
            case R.id.Control_bit4_0:
                Toast.makeText(this,"点动正转无效",Toast.LENGTH_SHORT).show();
                break;
            case R.id.Control_bit4_1:
                Toast.makeText(this,"点动正转",Toast.LENGTH_SHORT).show();
                break;
            case R.id.Control_bit5_0:
                Toast.makeText(this,"点动反转无效",Toast.LENGTH_SHORT).show();
                break;
            case R.id.Control_bit5_1:
                Toast.makeText(this,"点动反转",Toast.LENGTH_SHORT).show();
                break;
            case R.id.Control_bit6_0:
                Toast.makeText(this,"允许加减速",Toast.LENGTH_SHORT).show();
                break;
            case R.id.Control_bit6_1:
                Toast.makeText(this,"禁止加减速",Toast.LENGTH_SHORT).show();
                break;
            case R.id.Control_bit7_0:
                Toast.makeText(this,"上位机控制字1有效",Toast.LENGTH_SHORT).show();
                break;
            case R.id.Control_bit7_1:
                Toast.makeText(this,"上位机控制字1无效",Toast.LENGTH_SHORT).show();
                break;
            case R.id.Control_bit8_0:
                Toast.makeText(this,"主给定有效",Toast.LENGTH_SHORT).show();
                break;
            case R.id.Control_bit8_1:
                Toast.makeText(this,"主给定无效",Toast.LENGTH_SHORT).show();
                break;
            case R.id.Control_bit9_0:
                Toast.makeText(this,"故障复位有效",Toast.LENGTH_SHORT).show();
                break;
            case R.id.Control_bit9_1:
                Toast.makeText(this,"故障复位无效",Toast.LENGTH_SHORT).show();
                break;
        }
    }

    //每个活动中对广播的响应不相同
    private class LocalReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(action.equals(BLEService.ACTION_DATA_AVAILABLE)) {
                String message = intent.getStringExtra(BLEService.EXTRA_MESSAGE_DATA);
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


}


