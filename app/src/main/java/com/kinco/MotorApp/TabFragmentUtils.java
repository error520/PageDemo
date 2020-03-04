package com.kinco.MotorApp;

import android.view.View;

import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.kinco.MotorApp.sys.MyFragment;

import java.util.List;

public class TabFragmentUtils implements RadioGroup.OnCheckedChangeListener{
    private List<MyFragment> fragments;
    private FragmentManager fragmentManager;
    private int container;
    //当前显示的页面
    private int curShowPosition=0;
    private RadioGroup radioGroup;
    public TabFragmentUtils(RadioGroup radioGroup, int container, List<MyFragment> fragments, FragmentManager fragmentManager) {
        this.container = container;
        this.fragments = fragments;
        this.radioGroup = radioGroup;
        this.fragmentManager = fragmentManager;
        //设置radiobutton的点事件
        radioGroup.setOnCheckedChangeListener(this);
        //默认选择0 页面
        ((RadioButton) radioGroup.getChildAt(0)).setChecked(true);
        fragments.get(0).setShowing(true);

    }

    public void onCheckedChanged(RadioGroup group, int checkedId) {
        for (int i = 0; i < group.getChildCount(); i++) {
            View view = group.getChildAt(i);
            if(view.getId() == checkedId) {
                //隐藏当前页面
                fragments.get(curShowPosition).setShowing(false);
                fragments.get(curShowPosition).onStop();
                //显示点击页面
                if (fragments.get(i).isAdded()) {
                    //点击页面可见
                    fragments.get(i).setShowing(true);
                    fragments.get(i).onStart();
                } else {
                    fragments.get(i).setShowing(true);
                    fragmentManager.beginTransaction().add(container, fragments.get(i)).commit();
                }
                //真正的显示fragment
                showFragment(i);
            }
        }
    }
    //显示页面
    private void showFragment(int index) {
        for (int i = 0; i < fragments.size(); i++) {
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            if(i == index)
            {//就是要显示的页面
                fragmentTransaction.show(fragments.get(i));
            }
            else
            {
                fragmentTransaction.hide(fragments.get(i));
            }
            fragmentTransaction.commit();
        }
        //当前显示的页面为 index
        curShowPosition = index;
    }

    public void showPage(int index){
        ((RadioButton)radioGroup.getChildAt(index)).setChecked(true);
    }
}
