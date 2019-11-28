package com.kinco.MotorApp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;

public class DemoFragment {
    public static DemoFragment newInstance(int i) {

        Bundle args = new Bundle();
        args.putInt("int", i);
        DemoFragment fragment = new DemoFragment();
//        fragment.setArguments(args);
        return fragment;
    }

    @Nullable

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_demo, container, false);
//        int anInt = getArguments().getInt("int");
        TextView textView = (TextView) view.findViewById(R.id.text);
        textView.setText("这是第" + 1 + "张");
        return view;

    }
}
