package com.example.pagedemo.ui.firstpage;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.pagedemo.R;
import com.example.pagedemo.edittext.ItemBean;
import com.example.pagedemo.edittext.Text;
import com.example.pagedemo.edittext.TextAdapter;

import java.util.ArrayList;
import java.util.List;

public class FirstpageFragment extends Fragment implements View.OnClickListener {
    String[] Name =  { "Control mode", "Main reference  frequency selector"};
    String[][] temp = {{"0：Vector control without PG","1: Vector control with PG","2:V/F control"},
            {"0:Digital setting Keyboard UP/DN or terminal UP/DN ","1:AI1","2:AI2","3:AI3","4:Set via DI terminal(PULSE","5:Reserved"}};
    private View view;//得到碎片对应的布局文件,方便后续使用
    private ListView listView;
    private Button button;
    private ListView mListView;
    private Button mButton;
    private TextView editName;
    private com.example.pagedemo.edittext.ListViewAdapter mAdapter;
    private List<com.example.pagedemo.edittext.ItemBean> mData;
    //记住一定要重写onCreateView方法
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_first, container, false);//得到对应的布局文件
        return view;
    }
    @Override

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        show();
        mListView = (ListView) getActivity().findViewById(R.id.list_view0);
        mData = new ArrayList<ItemBean>();
        mData.add(new ItemBean( "Digital reference frequency", "","  "));
        mAdapter = new com.example.pagedemo.edittext.ListViewAdapter(this.getActivity(), mData);
        mListView.setAdapter(mAdapter);
        Button button0 = (Button) getActivity().findViewById(R.id.FirstpageMore);
        button0.setOnClickListener(this);
        Button button1 = (Button) getActivity().findViewById(R.id.FirstpageSubmit);
        button1.setOnClickListener(this);
        Button button2 = (Button) getActivity().findViewById(R.id.control_111B);
        button2.setOnClickListener(this);
        Button button3 = (Button) getActivity().findViewById(R.id.control_110B);
        button3.setOnClickListener(this);
        Button button4 = (Button) getActivity().findViewById(R.id.control_101B);
        button4.setOnClickListener(this);
        Button button5 = (Button) getActivity().findViewById(R.id.control_100B);
        button5.setOnClickListener(this);
        Button button6 = (Button) getActivity().findViewById(R.id.control_011B);
        button6.setOnClickListener(this);
        Button button7 = (Button) getActivity().findViewById(R.id.control_bit3_0);
        button7.setOnClickListener(this);
        Button button8 = (Button) getActivity().findViewById(R.id.control_bit3_1);
        button8.setOnClickListener(this);
        Button button9 = (Button) getActivity().findViewById(R.id.control_bit4_0);
        button9.setOnClickListener(this);
        Button button10 = (Button) getActivity().findViewById(R.id.control_bit4_1);
        button10.setOnClickListener(this);
        Button button11 = (Button) getActivity().findViewById(R.id.control_bit5_0);
        button11.setOnClickListener(this);
        Button button12 = (Button) getActivity().findViewById(R.id.control_bit5_1);
        button12.setOnClickListener(this);
        Button button13 = (Button) getActivity().findViewById(R.id.control_bit6_0);
        button13.setOnClickListener(this);
        Button button14 = (Button) getActivity().findViewById(R.id.control_bit6_1);
        button14.setOnClickListener(this);
        Button button15 = (Button) getActivity().findViewById(R.id.control_bit7_0);
        button15.setOnClickListener(this);
        Button button16 = (Button) getActivity().findViewById(R.id.control_bit7_1);
        button16.setOnClickListener(this);
        Button button17 = (Button) getActivity().findViewById(R.id.control_bit8_0);
        button17.setOnClickListener(this);
        Button button18 = (Button) getActivity().findViewById(R.id.control_bit8_1);
        button18.setOnClickListener(this);
        Button button19 = (Button) getActivity().findViewById(R.id.control_bit9_0);
        button19.setOnClickListener(this);
        Button button20 = (Button) getActivity().findViewById(R.id.control_bit9_1);
        button20.setOnClickListener(this);

    }
    private void show(){
        List<Text> texts = new ArrayList<Text>();
        for(int i=0;i<2;i++) {//自定义的Text类存数据
            Text text = new Text();
            text.setTitle(Name[i]);//标题数据
            text.setCurrent(String.valueOf(""));
            text.setId(0);//Spinner的默认选择项
            text.setContent(temp[i]);
            texts.add(text);
            TextAdapter textAdapter = new TextAdapter(this.getActivity(), texts, R.layout.main_item);//向自定义的Adapter中传值
            listView = (ListView) getActivity().findViewById(R.id.mylist0);
            listView.setAdapter(textAdapter);//传值到ListView中
        }}

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.FirstpageMore:
                Toast.makeText(getContext(),"点击了OneFragment里面的 更多 按钮",Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(getContext(), com.example.pagedemo.edittext.ListView_SpinnerActivity.class);
                getActivity().startActivity(intent);//当然也可以写成getContext()
                break;
            case R.id.FirstpageSubmit:
                Toast.makeText(getContext(),"点击了OneFragment里面的 提交 按钮",Toast.LENGTH_SHORT).show();
                mAdapter.notifyDataSetChanged();//提交输入的数据
                break;
            case R.id.control_111B:
                Toast.makeText(getContext(),"运行命令",Toast.LENGTH_SHORT).show();
                break;
            case R.id.control_110B:
                Toast.makeText(getContext(),"方式0停车",Toast.LENGTH_SHORT).show();
                break;
            case R.id.control_101B:
                Toast.makeText(getContext(),"方式1停车",Toast.LENGTH_SHORT).show();
                break;
            case R.id.control_100B:
                Toast.makeText(getContext(),"外部故障停车",Toast.LENGTH_SHORT).show();
                break;
            case R.id.control_011B:
                Toast.makeText(getContext(),"方式2停车",Toast.LENGTH_SHORT).show();
                break;
            case R.id.control_bit3_0:
                Toast.makeText(getContext(),"正转",Toast.LENGTH_SHORT).show();
                break;
            case R.id.control_bit3_1:
                Toast.makeText(getContext(),"反转",Toast.LENGTH_SHORT).show();
                break;
            case R.id.control_bit4_0:
                Toast.makeText(getContext(),"点动正转无效",Toast.LENGTH_SHORT).show();
                break;
            case R.id.control_bit4_1:
                Toast.makeText(getContext(),"点动正转",Toast.LENGTH_SHORT).show();
                break;
            case R.id.control_bit5_0:
                Toast.makeText(getContext(),"点动反转无效",Toast.LENGTH_SHORT).show();
                break;
            case R.id.control_bit5_1:
                Toast.makeText(getContext(),"点动反转",Toast.LENGTH_SHORT).show();
                break;
            case R.id.control_bit6_0:
                Toast.makeText(getContext(),"允许加减速",Toast.LENGTH_SHORT).show();
                break;
            case R.id.control_bit6_1:
                Toast.makeText(getContext(),"禁止加减速",Toast.LENGTH_SHORT).show();
                break;
            case R.id.control_bit7_0:
                Toast.makeText(getContext(),"上位机控制字1有效",Toast.LENGTH_SHORT).show();
                break;
            case R.id.control_bit7_1:
                Toast.makeText(getContext(),"上位机控制字1无效",Toast.LENGTH_SHORT).show();
                break;
            case R.id.control_bit8_0:
                Toast.makeText(getContext(),"主给定有效",Toast.LENGTH_SHORT).show();
                break;
            case R.id.control_bit8_1:
                Toast.makeText(getContext(),"主给定无效",Toast.LENGTH_SHORT).show();
                break;
            case R.id.control_bit9_0:
                Toast.makeText(getContext(),"故障复位有效",Toast.LENGTH_SHORT).show();
                break;
            case R.id.control_bit9_1:
                Toast.makeText(getContext(),"故障复位无效",Toast.LENGTH_SHORT).show();
                break;

        }


}
}