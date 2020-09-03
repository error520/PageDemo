package com.kinco.kmlink.ui.settingpage.parameterpage;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kinco.kmlink.BluetoothService.BleService;
import com.kinco.kmlink.EventBusUtils.BleDataEvent;
import com.kinco.kmlink.EventBusUtils.MessageEvent;
import com.kinco.kmlink.ParameterItem.ParameterBean;
import com.kinco.kmlink.R;
import com.kinco.kmlink.alertdialog.DialogInitialize;
import com.kinco.kmlink.utils.PrefUtil;
import com.kinco.kmlink.utils.ResourceUtil;
import com.kinco.kmlink.utils.util;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * 负责每个页面具体的数据加载, 更新
 */
public class SettingChildFragment extends Fragment {
    private String TAG = "ChildFragment";
    private int index = 1;
    private List<ParameterBean> parameterList = new ArrayList<>();
    private MutableLiveData<List<ParameterBean>> parametersLiveData = new MutableLiveData<>();
    private RecyclerView recyclerView;
    private SettingCardAdapter adapter;
    Boolean initialized = false;
    private WindowSetValue window;

    public static SettingChildFragment newInstance(int index) {
        SettingChildFragment childFragment = new SettingChildFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("Which", index);
        childFragment.setArguments(bundle);
        return childFragment;
    }

    public SettingChildFragment() {
        super();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            this.index = getArguments().getInt("Which");
            parameterList = ResourceUtil.getParameters(getContext(), ResourceUtil.settingParameters[index]); //获取参数信息
            parametersLiveData.setValue(parameterList);
            if (index == 0) {   //第一页添加自定义常用项
                List<ParameterBean> extraParameterList = PrefUtil.getCustomItems(getContext());
                if (extraParameterList != null) {
                    parameterList.addAll(extraParameterList);
                }
            }
        }
        parametersLiveData.observe(this, list -> {
            adapter.notifyDataSetChanged();
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_set_child, container, false);
        recyclerView = layout.findViewById(R.id.recyclerview);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        adapter = new SettingCardAdapter(getActivity(), index, parameterList);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        adapter.setOnSetButtonListener(position -> {    //添加每个item的点击事件
            ParameterBean bean = parameterList.get(position);
            if (bean.getType() <= 1) {  //选择型
                window = new WindowSetValue(getContext(), bean);
                window.showPopupWindow();
            } else {  //数值型
                window = new WindowKeyboard(getContext(), bean);
                window.showPopupWindow();
            }
        });
        return layout;
    }


    @Override
    public void onStart() {
        super.onStart();
//        Log.d("july18",index+"页onStart");
    }

    //当前被显示才调用
    @Override
    public void onResume() {
        super.onResume();
//        Log.d("july18",index+"页onResume");
        EventBus.getDefault().register(this);
//        Log.d("july18", "片段" + index + "initialized: " + initialized);
        if (!initialized && BleService.isConnected.getValue()) {
            DialogInitialize dialog = new DialogInitialize(getContext());
            dialog.showPopupWindow();
            InitialThread initialThread = new InitialThread(parameterList,
                    timeout -> {
                        getActivity().runOnUiThread(() -> {
                            dialog.dismiss();
                            if (timeout) {
                                util.centerToast(getContext(),getString(R.string.timeout),0);
                            } else {
                                initialized = true;
                            }
                        });
                    });
            initialThread.start();
        }
//        Log.d("child",index+":"+initialized);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    //在切换到其他父fragment时不会触发当前的onPause(说明还是在一个显示状态), 按下home键才会
    @Override
    public void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }

    public List<ParameterBean> getParameterList() {
        return parameterList;
    }

    //更新数据
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNewBleData(BleDataEvent event) {
        adapter.notifyDataSetChanged();
    }

    //提示连接状态
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNewMessage(MessageEvent event) {
        if (event.getMessage().equals("device disconnected")) {
            util.centerToast(getContext(), getString(R.string.device_disconnected), 1);
        }
    }
}
