package com.kinco.kmlink.ui.menupage;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kinco.kmlink.R;
import com.kinco.kmlink.sys.MyFragment;
import com.kinco.kmlink.utils.PrefUtil;
import com.kinco.kmlink.utils.util;


import androidx.annotation.Nullable;

public class MenuFragment extends MyFragment {
    private View layout;
    private DialogLanguage dialogLanguage;
    private DialogContact dialogContact;
    DialogVersion dialogVersion;
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
    }

    private void initUI(){
        TextView tvLanguage = layout.findViewById(R.id.tv_language);
        tvLanguage.setOnClickListener(v->{
            dialogLanguage = new DialogLanguage(getContext());
            dialogLanguage.showPopupWindow();
        });
        TextView tvContact = layout.findViewById(R.id.tv_contact);
        tvContact.setOnClickListener(v->{
            dialogContact = new DialogContact(getContext());
            dialogContact.showPopupWindow();
        });
        TextView tvDebug = layout.findViewById(R.id.tv_debug);
        tvDebug.setOnClickListener(v->{
            startSecondaryActivity(MasterSettingActivity.class);
        });
        TextView tvLog = layout.findViewById(R.id.tv_log);
        tvLog.setOnClickListener(v->{
            startSecondaryActivity(SerialActivity.class);
        });
        TextView tvVersion = layout.findViewById(R.id.tv_version);
        tvVersion.setOnClickListener(v->{
            dialogVersion = new DialogVersion(getContext());
            dialogVersion.showPopupWindow();
        });
    }

    private void startSecondaryActivity(Class activity){
        Intent intent = new Intent(getActivity(), activity);
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.slide_left_in,R.anim.slide_left_out);
    }
}
