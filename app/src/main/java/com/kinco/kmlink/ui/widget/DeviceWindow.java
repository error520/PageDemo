package com.kinco.kmlink.ui.widget;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.google.android.material.button.MaterialButton;
import com.kinco.kmlink.BluetoothService.BleService;
import com.kinco.kmlink.EventBusUtils.BleDataEvent;
import com.kinco.kmlink.EventBusUtils.MessageEvent;
import com.kinco.kmlink.EventBusUtils.RequestEvent;
import com.kinco.kmlink.MainViewModel;
import com.kinco.kmlink.R;
import com.kinco.kmlink.alertdialog.PasswordDialog;
import com.kinco.kmlink.utils.util;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import razerdp.basepopup.BasePopupWindow;

/**
 * 连接设备下拉菜单
 */
public class DeviceWindow extends BasePopupWindow {
    List<String> list;
    DeviceAdapter adapter;
    ListView listView;
    ProgressBar pbScanning;
    Button btnDisconnect;
    MaterialButton btnScan;
    private Boolean scanning = false;
    private String inputPassword = "";
    private PasswordDialog dialog;

    //先调用
    @Override
    public View onCreateContentView() {
        LayoutInflater inflater = LayoutInflater.from(this.getContext());
        View layout = inflater.inflate(R.layout.popup_device, null);
        btnScan = layout.findViewById(R.id.btn_scan);
        btnDisconnect = layout.findViewById(R.id.btn_disconnect);
        pbScanning = layout.findViewById(R.id.pb_scanning);
        listView = layout.findViewById(R.id.lv_new_device);
        list = new ArrayList<>();
        adapter = new DeviceAdapter(list);
        listView.setAdapter(adapter);
        return layout;
    }

    public DeviceWindow(Context context, MainViewModel viewModel) {
        super(context); //在这里面执行onCreateContentView
        BleService.deviceName.observe((LifecycleOwner) context, name -> {
            if(!name.equals(getContext().getString(R.string.unconnected)) && BleService.isConnected.getValue()){
                //从列表中移除已连接设备
                for (String device : list) {
                    if (device.contains(name)) {
                        list.remove(device);
                        break;
                    }
                }
                if (isShowing()) {
                    adapter.showConnecting(-1);
                }
                btnDisconnect.setEnabled(true);
                showPasswordDialog();
            }else{
                btnDisconnect.setEnabled(false);
                adapter.showConnecting(-1);
            }
        });
        initClickFunction();
    }

    /**
     * 添加所有点击事件
     */
    private void initClickFunction() {
        btnDisconnect.setOnClickListener(v -> {
            String[] request = {"disconnect"};
            EventBus.getDefault().post(new RequestEvent(request));
        });
        btnScan.setOnClickListener(v -> {
            if (!scanning) {
                scanDevice(true);
                adapter.showConnecting(-1);
            } else {
                scanDevice(false);
            }
        });
        listView.setOnItemClickListener((parent, view, position, id) -> {
            String[] info = list.get(position).split("\n");
            if(Pattern.matches(BleService.namePattern,info[0])){
                String[] request = {"connect", info[1]};
                EventBus.getDefault().post(new RequestEvent(request));
                adapter.showConnecting(position);
                scanDevice(false);
            }else{
                util.centerToast(getContext(),getContext().getString(R.string.not_specific_device),0);
            }
        });
    }

    @Override
    public void onShowing() {
        super.onShowing();
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    @Override
    public void onDismiss() {
        super.onDismiss();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
        list.clear();
        adapter.notifyDataSetChanged();
    }

    @Override
    protected Animation onCreateShowAnimation() {
        return AnimationUtils.loadAnimation(getContext(), R.anim.drop_down);
    }

    @Override
    protected Animation onCreateDismissAnimation() {
        return AnimationUtils.loadAnimation(getContext(), R.anim.pull_up);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNewMessage(MessageEvent event) {
        String message = event.getMessage();
        if (message.startsWith("d:")) {
            String device = message.split("d:")[1];
            if (!list.contains(device) && isShowing()) {
                list.add(device);
                adapter.notifyDataSetChanged();
            }
        }else if (message.equals("search completed")) {
            scanDevice(false);
        }else if(message.equals("connect failed")){
            adapter.showConnecting(-1);
            util.centerToast(getContext(),getContext().getString(R.string.connection_failed),0);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNewBleData(BleDataEvent event){
        byte[] message = event.getBleData();
        byte[] bytesPassword = new byte[]{message[3], message[4]};
        String password = util.toHexString(bytesPassword, false);
        if(this.inputPassword.equals(password)&&dialog!=null){
            dialog.gone();
            dialog = null;
            adapter.showConnecting(-1);
            BleService.varified = true;
            util.centerToast(getContext(),getContext().getString(R.string.password_correct),0);
        }else{
            util.centerToast(getContext(),getContext().getString(R.string.password_wrong),0);
        }
    }

    /**
     * 是否扫描设备
     */
    private void scanDevice(Boolean scanning) {
        this.scanning = scanning;
        if (scanning) {
            String[] request = {"scan", "5000"};
            EventBus.getDefault().post(new RequestEvent(request));
            btnScan.setText(getContext().getString(R.string.stop_scan));
            btnScan.setIcon(null);
            pbScanning.setVisibility(View.VISIBLE);
        } else {
            String[] request = {"stop scan"};
            EventBus.getDefault().post(new RequestEvent(request));
            btnScan.setText(getContext().getString(R.string.SCAN));
            btnScan.setIcon(getContext().getDrawable(R.drawable.ic_search));
            pbScanning.setVisibility(View.GONE);
        }
    }

    private void showPasswordDialog(){
        if(!BleService.varified){
            dialog = new PasswordDialog(getContext());
            dialog.setOnClickBottomListener(new PasswordDialog.OnClickBottomListener(){
                @Override
                public void onPositiveClick() {
                    inputPassword = dialog.getPassword();
                    String[] request = {"read","0000","0001"};
                    EventBus.getDefault().post(new RequestEvent(request));
                }
                @Override
                public void onNegativeClick() {
                    EventBus.getDefault().post(new RequestEvent(new String[]{"disconnect"}));
                    dialog.gone();
                    dialog = null;
                }
            });
        }
    }

    void fakeScan(List list, ArrayAdapter adapter) {
        new Thread(() -> {
            int i = 0;
            while (i < 5) {
                i++;
                try {
                    Thread.sleep(1000);
                    list.add("设备" + i);
                    getContext().runOnUiThread(() -> {
                        adapter.notifyDataSetChanged();
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            getContext().runOnUiThread(() -> {
                pbScanning.setVisibility(View.GONE);
            });
        }).start();
    }

}
