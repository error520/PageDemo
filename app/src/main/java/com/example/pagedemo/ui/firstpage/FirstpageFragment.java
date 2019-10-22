package com.example.pagedemo.ui.firstpage;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.pagedemo.R;

public class FirstpageFragment extends Fragment {
    private View view;//得到碎片对应的布局文件,方便后续使用

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

        Button button = (Button) getActivity().findViewById(R.id.FirstpageMore);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(),"点击了OneFragment里面的Button按钮",Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(getContext(), com.example.pagedemo.edittext.ListView_SpinnerActivity.class);
                getActivity().startActivity(intent);//当然也可以写成getContext()
            }
        });
    }
}