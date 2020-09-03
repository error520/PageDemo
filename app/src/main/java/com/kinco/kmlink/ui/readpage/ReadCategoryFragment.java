package com.kinco.kmlink.ui.readpage;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.kinco.kmlink.BluetoothService.BleService;
import com.kinco.kmlink.EventBusUtils.BleDataEvent;
import com.kinco.kmlink.EventBusUtils.RequestEvent;
import com.kinco.kmlink.ParameterItem.ParameterBean;
import com.kinco.kmlink.R;
import com.kinco.kmlink.sys.MyFragment;
import com.kinco.kmlink.ui.readpage.parameterpage.ReadChildActivity;
import com.kinco.kmlink.utils.ResourceUtil;
import com.kinco.kmlink.utils.util;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ReadCategoryFragment extends MyFragment {
    List<ParameterBean> parameterList;
    private ReadCardAdapter adapter;
    private Timer timer;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        parameterList = ResourceUtil.getStatusWordParameter(getContext());
        timer = new Timer();
        BleService.isConnected.observe(this, flag -> {
            if (flag) {
                sendRequest(200);
            }
        });
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_read_category, container, false);
        for (int i = 0; i < 5; i++) {
            int id = getResources().getIdentifier("button" + i, "id", getContext().getPackageName());
            Button button = layout.findViewById(id);
            int finalI = i;
            button.setOnClickListener(v -> {
                startActivityByIndex(finalI);
            });
        }
        RecyclerView recyclerView = layout.findViewById(R.id.rv_status_word);
        adapter = new ReadCardAdapter(getContext(), parameterList, "status word");
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        return layout;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        sendRequest(200);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNewBleData(BleDataEvent event) {
        byte[] message = event.getBleData();
        if (message.length == 7) {
            util.setStatusWordByMessage(message, parameterList);
            adapter.notifyDataSetChanged();
            sendRequest(2000);
        }
    }

    private void startActivityByIndex(int index) {
        Intent intent = new Intent(getContext(), ReadChildActivity.class);
        intent.putExtra("index", index);
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.slide_left_in, R.anim.slide_left_out);
    }

    private void sendRequest(long delay) {
        if (showing && BleService.isConnected.getValue() && BleService.varified) {
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    String[] request = {"read", "0110", "0001"};
                    EventBus.getDefault().post(new RequestEvent(request));
                }
            }, delay);
        }
    }
}
