package com.kinco.kmlink.ui.secondpage;

import android.content.Context;
import android.view.ViewGroup;

import com.kinco.kmlink.R;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class ReadPagerAdapter extends FragmentPagerAdapter {
    public static final int[] TAB_TITLES = {R.string.title_general,R.string.title_terminal,R.string.title_fault,R.string.title_status,R.string.title_others};
    private final Context context;
    List<Fragment> fragments = new ArrayList<>();
    ReadChildFragment currentFragment;
    public ReadPagerAdapter(Context context, FragmentManager fm, int behavior) {
        super(fm,behavior);
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {
        ReadChildFragment fragment = ReadChildFragment.newInstance(position);
        fragments.add(fragment);
        return fragment;
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

    @Override
    public void setPrimaryItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        currentFragment = (ReadChildFragment)object;
        super.setPrimaryItem(container, position, object);
    }

    void stopAllChildFragments(){
        for(Fragment fragment:fragments){
            fragment.onStop();
        }
    }
}
