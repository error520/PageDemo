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
        tableTitle.setBackgroundColor(Color.rgb(177, 173, 172));
        //！！！数据每次点击后都应该刷新数据
        final List<Parameter> list0 = new ArrayList<Parameter>();
        list0.add(new Parameter( "B0.00", "Output frequency","-300.00~300.00Hz","0.01Hz","NULL"));
        list0.add(new Parameter("B0.01", "Output voltage","0~60000V","1V","NULL"));
        list0.add(new Parameter( "B0.02", "Output current","0.0~3Ie","0.1A","NULL"));
        ListView tableListView = (ListView) getActivity().findViewById(R.id.list0);
        TableAdapter adapter = new TableAdapter(this.getActivity(),list0);
        tableListView.setAdapter(adapter);
        Button button = (Button) getActivity().findViewById(R.id.SecondpageMore);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(),"点击了TwoFragment里面的Button按钮",Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(getContext(), ListTableActivity.class);
                getActivity().startActivity(intent);//当然也可以写成getContext()

            }
        });
    }
}