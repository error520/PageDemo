package com.example.pagedemo.ui.fourthpage;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.pagedemo.R;
public class FourthpageFragment extends Fragment {
    private View view;//得到碎片对应的布局文件,方便后续使用
    //记住一定要重写onCreateView方法
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_fourth, container, false);//得到对应的布局文件
        return view;
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Button button = (Button) getActivity().findViewById(R.id.FourthpageMore);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getContext(), com.example.pagedemo.OSCActivity.class);
                getActivity().startActivity(intent);//当然也可以写成getContext()
            }
        });
    }
}