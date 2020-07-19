package com.kinco.kmlink.ui.firstpage;

import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kinco.kmlink.EventBusUtils.BleDataEvent;
import com.kinco.kmlink.MainViewModel;
import com.kinco.kmlink.ParameterItem.ParameterBean;
import com.kinco.kmlink.R;
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
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class SettingChildFragment extends Fragment {
    private String TAG = "ChildFragment";
    private int index = 1;
    private List<ParameterBean> parameterList = new ArrayList<>();
    private MutableLiveData<List<ParameterBean>> parametersLiveData = new MutableLiveData<>();
    private RecyclerView recyclerView;
    private SettingCardAdapter adapter;
    MainViewModel viewModel;
    LiveData<String> bleMessage;
    private int position=-1;   //当前要更新的位置
    Boolean initialized = true;

    public static SettingChildFragment newInstance(int index){
        SettingChildFragment childFragment = new SettingChildFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("Which",index);
        childFragment.setArguments(bundle);
        return childFragment;
    }

    public SettingChildFragment(){
        super();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments()!=null){
            this.index = getArguments().getInt("Which");
            parameterList = ResourceUtil.getParameters(getContext(),ResourceUtil.settingParameters[index]); //获取参数信息
            parametersLiveData.setValue(parameterList);
            if(index==0){   //第一页添加自定义常用项
                List<ParameterBean> extraParameterList = PrefUtil.getCustomItems(getContext());
                if(extraParameterList!=null){
                    parameterList.addAll(extraParameterList);
                }
            }
        }
        viewModel = new ViewModelProvider(getActivity()).get(MainViewModel.class);
        viewModel.setContext(getContext());
//        bleMessage = viewModel.getBleMessage();
//        bleMessage.observe(this, message -> {
//            util.centerToast(getContext(),getContext().getString(R.string.succeed),0);
//            adapter.notifyDataSetChanged();
//        });
        parametersLiveData.observe(this,list->{
            adapter.notifyDataSetChanged();
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_set_child,container,false);
        recyclerView = layout.findViewById(R.id.recyclerview);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        adapter = new SettingCardAdapter(getActivity(),index,parameterList);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        adapter.setOnSetButtonListener(position -> {    //添加每个item的点击事件
            this.position = position;
            ParameterBean bean = parameterList.get(position);
            if(bean.getType()<=1){  //选择型
                SetValueWindow window = new SetValueWindow(getContext(),bean);
                window.showPopupWindow();
            }else{
                viewModel.showSetDataDialog(bean);
            }
        });
//        recyclerView.setNestedScrollingEnabled(false);

        return layout;
    }


    @Override
    public void onStart() {
        super.onStart();
//        util.centerToast(getContext(),index+"onStart",0);
    }

    //当前被显示才调用
    @Override
    public void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
//        if(initialized==false){ //还未初始化
//            viewModel.addToQueue(parametersLiveData);
//            viewModel.startRefreshThread(0);
//            initialized = true;
//        }
        Log.d("child",index+":"+initialized);
//        adapter.notifyDataSetChanged();
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

    public List<ParameterBean> getParameterList(){
        return parameterList;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNewBleData(BleDataEvent event){
        adapter.notifyDataSetChanged();
    }
}
