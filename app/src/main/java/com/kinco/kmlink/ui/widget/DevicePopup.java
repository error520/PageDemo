package com.kinco.kmlink.ui.widget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.google.android.material.button.MaterialButton;
import com.kinco.kmlink.MainViewModel;
import com.kinco.kmlink.R;

import java.util.ArrayList;
import java.util.List;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import razerdp.basepopup.BasePopupWindow;

/**
 * 连接设备下拉菜单
 */
public class DevicePopup extends BasePopupWindow {
    List<String> list;
    int position;
    ArrayAdapter adapter;
    ListView listView;
    ProgressBar pbScanning;
    Button btnDisconnect;
    MaterialButton btnScan;
    Context context;
    MainViewModel viewModel;
    MutableLiveData<List<String>> deviceLiveData;
    MutableLiveData<Boolean> scanningLiveData;
    LiveData<Boolean> connectedLiveData;

    //先调用
    @Override
    public View onCreateContentView() {
        LayoutInflater inflater = LayoutInflater.from(this.getContext());
        View layout = inflater.inflate(R.layout.popup_device,null);
        btnScan = layout.findViewById(R.id.btn_scan);
        btnDisconnect = layout.findViewById(R.id.btn_disconnect);
        pbScanning = layout.findViewById(R.id.pb_scanning);
        listView = layout.findViewById(R.id.lv_new_device);
        list = new ArrayList<>();
        adapter = new ArrayAdapter(getContext(),R.layout.list_item, list);
        listView.setAdapter(adapter);
        return layout;
    }

    public DevicePopup(Context context, MainViewModel viewModel) {
        super(context); //在这里面执行onCreateContentView
        this.context = context;
        this.viewModel = viewModel;
        this.deviceLiveData = (MutableLiveData<List<String>>) viewModel.getDeviceLiveData();
        //获取设备信息
        deviceLiveData.observe((LifecycleOwner) context, deviceList->{
            if(deviceList.isEmpty()){
                list.clear();
                adapter.notifyDataSetChanged();
            }else if(deviceList.size()<list.size()){    //移除已连接的设备
                for(int i=0; i<list.size(); i++){
                    if(!deviceList.contains(list.get(i))){
                        list.remove(i);
                        adapter.notifyDataSetChanged();
                        break;
                    }
                }
            }else{
                for(String device:deviceList){      //全部遍历才可以初始化列表
                    if(!list.contains(device)) {    //避免重复添加
                        list.add(device);
                        adapter.notifyDataSetChanged();
                    }
                }
            }
        });
        scanningLiveData = (MutableLiveData<Boolean>) viewModel.getScanningFlag();
        //根据是否在扫描中改变界面
        scanningLiveData.observe((LifecycleOwner) context, flag->{
            if(flag == false){
                btnScan.setText(context.getString(R.string.SCAN));
                btnScan.setIcon(context.getDrawable(R.drawable.ic_search));
                pbScanning.setVisibility(View.GONE);
            }else {
                btnScan.setText(context.getString(R.string.stop_scan));
                btnScan.setIcon(null);
                pbScanning.setVisibility(View.VISIBLE);
            }
        });
        connectedLiveData = viewModel.getConnectedFlag();
        //连接上了
        connectedLiveData.observe((LifecycleOwner)context, flag->{
            if(flag){
                btnDisconnect.setEnabled(true);
                scanningLiveData.setValue(false);
            }else{
                btnDisconnect.setEnabled(false);
            }
        });
        initClickFunction();
        setDismissFunction();

    }

    /**
     * 添加所有点击事件
     */
    private void initClickFunction(){
        btnDisconnect.setOnClickListener(v -> {
            btnDisconnect.setEnabled(false);
            viewModel.disconnectCurrent();
        });
        btnScan.setOnClickListener(v -> {
            if(!scanningLiveData.getValue()){
                viewModel.startScanning();
            }else{
                viewModel.stopScanning();
            }
        });
        listView.setOnItemClickListener((parent, view, position,id)->{
            String address = list.get(position).split("\n")[1];
            viewModel.connectDevice(address,position);
            this.position = position;
        });
    }

    /**
     * 设置退出时的收尾操作
     */
    private void setDismissFunction() {
        setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss() {
                if(connectedLiveData.getValue()==true){
                    List<String> list = deviceLiveData.getValue();
                    list.clear();
                    deviceLiveData.setValue(list);
                }
                viewModel.set();
            }

            @Override
            public boolean onBeforeDismiss() {
                return true;
            }
        });
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



    void fakeScan(List list, ArrayAdapter adapter){
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
    }

}
