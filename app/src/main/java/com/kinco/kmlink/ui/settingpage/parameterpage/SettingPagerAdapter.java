package com.kinco.kmlink.ui.settingpage.parameterpage;

import android.content.Context;

import com.kinco.kmlink.ParameterItem.ParameterBean;
import com.kinco.kmlink.R;
import com.kinco.kmlink.ui.settingpage.parameterpage.SettingChildFragment;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class SettingPagerAdapter extends FragmentPagerAdapter {
    public static final int[] TAB_TITLES = {R.string.title_general,R.string.title_motor,R.string.title_vf,R.string.title_terminal,R.string.title_control};
    static List<SettingChildFragment> fragments = new ArrayList<>();
    private final Context context;
    public SettingPagerAdapter(Context context, FragmentManager fm, int behavior) {
        super(fm, behavior);
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {
        SettingChildFragment childFragment = SettingChildFragment.newInstance(position);
        fragments.add(childFragment);
        return childFragment;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return context.getResources().getString(TAB_TITLES[position]);
    }

    @Override
    public int getCount() {
        return 5;
    }

    //停止所有子片段
    public void stopAllChildren(){
        for(Fragment child:fragments){
            child.onStop();
        }
    }

    //把子片段初始化状态设为false
    public void enableChildInitial(){
        for(SettingChildFragment child:fragments){
            child.initialized = false;
        }
    }

    public static List<SettingChildFragment> getFragments(){
        return fragments;
    }

    public List<ParameterBean> getParameterBeanLists(){
        List<ParameterBean> list = new ArrayList<>();
        for(SettingChildFragment child:fragments){
            list.addAll(child.getParameterList());
        }
        return list;
    }
}
