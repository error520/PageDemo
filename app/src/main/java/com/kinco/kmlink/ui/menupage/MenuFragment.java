package com.kinco.kmlink.ui.menupage;

import android.os.Bundle;
import android.os.TestLooperManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kinco.kmlink.R;
import com.kinco.kmlink.alertdialog.ContactDialog;
import com.kinco.kmlink.alertdialog.SetLanguageDialog;
import com.kinco.kmlink.sys.MyFragment;


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
//        ImageView icon = getActivity().findViewById(R.id.icon_status);
//        icon.setActivated(false);
    }

    private void initUI(){
        TextView tvLanguage = layout.findViewById(R.id.tv_language);
        tvLanguage.setOnClickListener(v->{
            dialogLanguage = new DialogLanguage(getContext());
            dialogLanguage.showPopupWindow();
//            SetLanguageDialog dialog = new SetLanguageDialog(getContext());
        });
        TextView tvContact = layout.findViewById(R.id.tv_contact);
        tvContact.setOnClickListener(v->{
            dialogContact = new DialogContact(getContext());
            dialogContact.showPopupWindow();
//            ContactDialog dialog = new ContactDialog(getContext());
        });
        TextView tvVersion = layout.findViewById(R.id.tv_version);
        tvVersion.setOnClickListener(v->{
            dialogVersion = new DialogVersion(getContext());
            dialogVersion.showPopupWindow();
        });
    }
}
