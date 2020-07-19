package com.kinco.kmlink.ui.secondpage;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.kinco.kmlink.BluetoothService.BleService;
import com.kinco.kmlink.MainViewModel;
import com.kinco.kmlink.R;
import com.kinco.kmlink.sys.MyFragment;

import java.util.Timer;
import java.util.TimerTask;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;

public class ReadParentFragment extends MyFragment {
    ViewPager viewPager;
    ReadPagerAdapter adapter;
    MainViewModel viewModel;
    FloatingActionButton fab;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(getActivity()).get(MainViewModel.class);
        LiveData<Boolean> refreshingLiveData = viewModel.getRefreshingFlag();
        refreshingLiveData.observe(this, refreshing -> {
            fab.setActivated(refreshing);
        });
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_read_parent,container,false);
        viewPager = layout.findViewById(R.id.view2);
        adapter = new ReadPagerAdapter(getContext(),getActivity().getSupportFragmentManager(),1);
        viewPager.setAdapter(adapter);
        TabLayout tabs = layout.findViewById(R.id.tab2);
        tabs.setupWithViewPager(viewPager);
        fab = layout.findViewById(R.id.fab);
        fab.setOnClickListener(v->{
            adapter.currentFragment.startFirstRequest();
            viewPager.setCurrentItem(2,false);
        });
        Timer timer = new Timer();
        timer.schedule(new task(), BleService.reloadGap);
        return layout;
    }

    @Override
    public void onStart() {
        super.onStart();
//        Log.d("child","read onStart()");
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopAllChildFragments();
        viewModel.stopRefreshThread();
    }

    class task extends TimerTask{
        @Override
        public void run() {

        }
    }
}
