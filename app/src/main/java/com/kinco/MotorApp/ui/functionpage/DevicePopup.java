package com.kinco.MotorApp.ui.functionpage;

import android.animation.Animator;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;

import com.google.android.material.button.MaterialButton;
import com.kinco.MotorApp.MainActivity;
import com.kinco.MotorApp.R;
import com.kinco.MotorApp.alertdialog.PasswordDialog;
import com.kinco.MotorApp.utils.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import razerdp.basepopup.BasePopupWindow;

public class DevicePopup extends BasePopupWindow {
    ProgressBar pbScanning;
    Button btnDisconnect;

    public DevicePopup(Context context) {
        super(context);
    }

    @Override
    public View onCreateContentView() {
        LayoutInflater inflater = LayoutInflater.from(this.getContext());
        View layout = inflater.inflate(R.layout.popup_device,null);
        MaterialButton btnScan = layout.findViewById(R.id.btn_scan);
        pbScanning = layout.findViewById(R.id.pb_scanning);
        ListView listView = layout.findViewById(R.id.lv_new_device);
        final List list = new ArrayList<>();
        final ArrayAdapter adapter = new ArrayAdapter<String>(getContext(),R.layout.list_item, list);
        listView.setAdapter(adapter);
        btnDisconnect = layout.findViewById(R.id.btn_disconnect);
        btnDisconnect.setOnClickListener(v -> {
            btnDisconnect.setEnabled(!btnDisconnect.isEnabled());
        });
        btnScan.setOnClickListener(v -> {
            btnDisconnect.setEnabled(!btnDisconnect.isEnabled());
            pbScanning.setVisibility(View.VISIBLE);
            util.centerToast(getContext(),"扫描中",0);
            new Thread(() -> {
                int i = 0;
                while(i<5){
                    i++;
                    try {
                        Thread.sleep(1000);
                        list.add("设备"+i);
                        getContext().runOnUiThread(()->{
                            adapter.notifyDataSetChanged();
                        });
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                getContext().runOnUiThread(()->{
                    pbScanning.setVisibility(View.GONE);
                });
            }).start();
        });
        return layout;
    }

    @Override
    protected Animation onCreateShowAnimation() {
        Animation animation = AnimationUtils.loadAnimation(getContext(),R.anim.drop_down);
        return animation;
    }

    @Override
    protected Animation onCreateDismissAnimation() {
        Animation animation = AnimationUtils.loadAnimation(getContext(),R.anim.pull_up);
        return animation;
    }

    @Override
    public void onDismiss() {
        super.onDismiss();
        util.centerToast(getContext(),"are u sure?",0);
    }
}
