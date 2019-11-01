package com.example.pagedemo.ui.secondpage;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.pagedemo.ListTableActivity;
import com.example.pagedemo.Parameter;
import com.example.pagedemo.R;
import com.example.pagedemo.TableAdapter;

import java.util.ArrayList;
import java.util.List;

public class SecondpageFragment extends Fragment {
    private View view;//得到碎片对应的布局文件,方便后续使用
    public int[] colors = { Color.WHITE, Color.rgb(219, 238, 244) };//RGB颜色
    String[] Name={"Output frequency","Output voltage","Motor power","变频器是否运行","变频器是否反转","是否达到主设定","是否允许串口控制",
        "是否允许串口给定","报警/故障/正常","故障/报警代码"};

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
        final List<Parameter> list0 = new ArrayList<Parameter>();
        for(int i=0;i<10;i++){
            list0.add(new Parameter(  Name[i],"z"+i));
        }
        ListView tableListView = (ListView) getActivity().findViewById(R.id.list0);
        TableAdapter adapter = new TableAdapter(this.getActivity(),list0);
        tableListView.setAdapter(adapter);
        Button button0 = (Button) getActivity().findViewById(R.id.SecondpageMore);
        button0.setOnClickListener(new SecondpageFragment.ButtonListener());
        Button button1 = (Button) getActivity().findViewById(R.id.SecondpageReload);
        button1.setOnClickListener(new SecondpageFragment.ButtonListener());
    }
    private class ButtonListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.SecondpageMore:
                    Toast.makeText(getContext(),"点击了TwoFragment里面的 更多 按钮",Toast.LENGTH_SHORT).show();
                    Intent intent=new Intent(getContext(), ListTableActivity.class);
                    getActivity().startActivity(intent);//当然也可以写成getContext()
                case R.id.SecondpageReload:
                    Toast.makeText(getContext(),"点击了TwoFragment里面的 刷新 按钮",Toast.LENGTH_SHORT).show();

            }

        }
    }
}