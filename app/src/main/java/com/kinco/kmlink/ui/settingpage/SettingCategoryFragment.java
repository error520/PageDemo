package com.kinco.kmlink.ui.settingpage;

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
import com.kinco.kmlink.ui.settingpage.parameterpage.SetChildActivity;
import com.kinco.kmlink.utils.PrefUtil;
import com.kinco.kmlink.utils.util;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class SettingCategoryFragment extends MyFragment {
    private String[][] quickRequest = {{"", "0017", "00C6"}, {"", "0017", "00c7"}, {"", "0017", "0280"},
            {"", "0017", "00C5"}, {"", "0017", "00cf"}, {"", "0017", "0080"}};

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_setting_category, container, false);
        for (int i = 0; i < 5; i++) {
            int id = getResources().getIdentifier("button" + i, "id", getContext().getPackageName());
            Button button = layout.findViewById(id);
            if (i == 2) {
                if (PrefUtil.getLanguage(getContext()).equals("zh")) {
                    button.setTypeface(Typeface.SANS_SERIF);
                }
            }
            int finalI = i;
            button.setOnClickListener(v -> {
                startActivityByIndex(finalI);
            });
        }
        for (int i = 0; i < 6; i++) {
            int id = getResources().getIdentifier("quick_btn" + i, "id", getContext().getPackageName());
            Button btnQuick = layout.findViewById(id);
            int finalI = i;
            btnQuick.setOnClickListener(v -> {
                String[] request = quickRequest[finalI];
                request[0] = "write";
                EventBus.getDefault().post(new RequestEvent(request));
            });
        }
        return layout;
    }

    private void startActivityByIndex(int index) {
        Intent intent = new Intent(getContext(), SetChildActivity.class);
        intent.putExtra("index", index);
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.slide_left_in, R.anim.slide_left_out);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNewBleData(BleDataEvent event) {
        byte[] message = event.getBleData();
        if (message.length == 8&&showing) {
            byte[] crc = util.CRC16_Check(message,message.length-2);
            if(crc[0]==message[6]&&crc[1]==message[7]){         //crc校验确认是设置成功的返回信息
                util.centerToast(getActivity(), getString(R.string.successful), 0);
            }
        }
    }
}
