package com.example.pagedemo.edittext;


import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.pagedemo.R;

import java.util.ArrayList;
import java.util.List;


public class ListView_SpinnerActivity extends Activity {

    /** Called when the activity is first created. */

    String[] Name =  { "Control mode", "Main reference  frequency selector", "Methods of  inputting operating  commands", "Running direction   " ,"Input terminal X1 function selection","Input terminal  X2 function selection" ,
                        "Terminal control  nmode selection","Bi-direction pen-collector output terminal Y1","Output functions  of relay R1","Y1 terminal output","Functions of   terminal AO1","PG type ","Fault masking  selection 1"};
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
    private ListView listView;
    private Button button;
    private ListView mListView;
    private Button mButton;
    private TextView editName;
    private com.example.pagedemo.edittext.ListViewAdapter mAdapter;
    private List<com.example.pagedemo.edittext.ItemBean> mData;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        button = (Button) findViewById(R.id.button1);
        show();
        mListView = (ListView) findViewById(R.id.list_view);
        mData = new ArrayList<ItemBean>();
        for(int i=0;i<10;i++){
//            mData.add(new com.example.spinner.edittext.ItemBean());
            mData.add(new ItemBean( GroupArray[i], "Sth","  "));
        }
        mAdapter = new com.example.pagedemo.edittext.ListViewAdapter(this, mData);
        mListView.setAdapter(mAdapter);
        button.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                ListAdapter listAdapter = listView.getAdapter();
                mAdapter.notifyDataSetChanged();//提交输入的数据
                for(int i=0;i<listAdapter.getCount();i++){
                    Text text = (Text) listAdapter.getItem(i);//提交选择的数据
//                    Toast.makeText(ListView_SpinnerActivity.this,text.getTitle()+" "+text.getContent()[text.getId()], 0).show();
                }
            }
        });
    }
    private void show(){
        List<Text> texts = new ArrayList<Text>();
        for(int i=0;i<13;i++) {//自定义的Text类存数据
            Text text = new Text();
            text.setTitle(Name[i]);//标题数据
            text.setCurrent(String.valueOf("Sth"));
            text.setId(0);//Spinner的默认选择项
            text.setContent(temp[i]);
            texts.add(text);
            TextAdapter textAdapter = new TextAdapter(ListView_SpinnerActivity.this, texts, R.layout.main_item);//向自定义的Adapter中传值
            listView = (ListView) findViewById(R.id.mylist);
            listView.setAdapter(textAdapter);//传值到ListView中
        }}}