package com.kinco.kmlink.ui.firstpage;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.kinco.kmlink.EventBusUtils.BleDataEvent;
import com.kinco.kmlink.EventBusUtils.RequestEvent;
import com.kinco.kmlink.R;
import com.kinco.kmlink.sys.MyFragment;
import com.kinco.kmlink.utils.PrefUtil;
import com.kinco.kmlink.utils.util;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Arrays;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class SettingCategoryFragment extends MyFragment {
    String[][] quickRequest = {{"0017","00C6"}, {"0017","00c7"}, {"0017","0280"}, {"0017","00C5"},
            {"0017","00cf"},{"0017","0080"}};
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
         View layout = inflater.inflate(R.layout.fragment_setting_category, container,false);
         for(int i=0; i<5; i++){
             int id = getResources().getIdentifier("button"+i,"id", getContext().getPackageName());
             Button button = layout.findViewById(id);
             if(i==2){
                 if(PrefUtil.getLanguage(getContext()).equals("zh")){
                     button.setTypeface(Typeface.SANS_SERIF);
                 }
             }
             int finalI = i;
             button.setOnClickListener(v->{
                 startActivityByIndex(finalI);
             });
         }
         for(int i=0; i<6; i++){
             int id = getResources().getIdentifier("quick_btn"+i,"id", getContext().getPackageName());
             Button btnQuick = layout.findViewById(id);
             int finalI = i;
             btnQuick.setOnClickListener(v->{
                 String[] request = Arrays.copyOf(quickRequest[finalI],3);
                 request[2] = "write";
                 EventBus.getDefault().post(new RequestEvent(request));
             });
         }
         return layout;
    }

    private void startActivityByIndex(int index){
        Intent intent = new Intent(getContext(), SetChildActivity.class);
        intent.putExtra("index",index);
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.slide_left_in,R.anim.slide_left_out);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNewBleData(BleDataEvent event){
        if(event.getBleData().length==8){
            util.centerToast(getActivity(),getString(R.string.succeed),0);
        }
    }

}
