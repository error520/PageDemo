package com.example.pagedemo.ui.fourthpage;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
}