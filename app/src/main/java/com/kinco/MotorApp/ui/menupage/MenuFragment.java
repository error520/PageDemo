package com.kinco.MotorApp.ui.menupage;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.kinco.MotorApp.R;
import com.kinco.MotorApp.sys.MyFragment;
import com.kinco.MotorApp.utils.util;
import com.wuhenzhizao.titlebar.widget.CommonTitleBar;


import androidx.annotation.Nullable;

public class MenuFragment extends MyFragment {
    View layout;
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        layout = inflater.inflate(R.layout.fragment_menu, container, false);//得到对应的布局文件
        initUI();
        return layout;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


    }

    @Override
    public void onStart() {
        super.onStart();
        ImageView icon = getActivity().findViewById(R.id.icon_status);
        icon.setActivated(false);
    }

    private void initUI(){
        TextView textView = layout.findViewById(R.id.tv_language);
        textView.setOnClickListener(v->{

        });
//        rightLayout.findViewById(R.id.cli).setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View v) {
//                util.centerToast(getActivity(),"弹出蓝牙搜索",0);
//            }
//        });
    }
}
