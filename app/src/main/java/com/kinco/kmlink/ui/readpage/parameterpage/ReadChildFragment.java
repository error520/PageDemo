package com.kinco.kmlink.ui.readpage.parameterpage;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kinco.kmlink.ParameterItem.ParameterBean;
import com.kinco.kmlink.R;
import com.kinco.kmlink.ui.readpage.ReadCardAdapter;
import com.kinco.kmlink.utils.PrefUtil;
import com.kinco.kmlink.utils.ResourceUtil;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ReadChildFragment extends Fragment {
    private int index = 1;
    MutableLiveData<List<ParameterBean>> parameterLiveData;
    ReadChildVM viewModel;
    ReadCardAdapter adapter;
    private Boolean isShowing = false;   //当前页是否显示的标志位

    public ReadChildFragment(){
        super();
    }

    public static ReadChildFragment newInstance(int index){
        ReadChildFragment childFragment = new ReadChildFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("Which",index);
        childFragment.setArguments(bundle);
        return childFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments()!=null){
            this.index = getArguments().getInt("Which");
            List<ParameterBean> parameters= ResourceUtil.getBParameters(getContext(),ResourceUtil.readParameters[index]);
            parameterLiveData = new MutableLiveData<>();
            parameterLiveData.setValue(parameters);
        }
        Timer timer = new Timer();
        parameterLiveData.observe(this, list->{
            adapter.notifyDataSetChanged();
            Log.d("child",index+"页更新一次信息");
            if(isShowing){
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        viewModel.addToQueue(parameterLiveData);
                    }
                }, PrefUtil.readGap);
            }
        });
        viewModel = new ViewModelProvider(getActivity()).get(ReadChildVM.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_read_child,container,false);
        RecyclerView recyclerView = layout.findViewById(R.id.read_recyclerview);
        adapter = new ReadCardAdapter(getContext(),parameterLiveData.getValue(),"card");
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        return layout;
    }


    @Override
    public void onStart() {
        super.onStart();
    }

    //当前被显示才调用
    @Override
    public void onResume() {
        super.onResume();
        isShowing = true;
        if(viewModel.refreshing){  //如果之前已经开启了刷新状态
            viewModel.addToQueue(parameterLiveData);
        }
        adapter.notifyDataSetChanged();
    }

    //切换到其他父片段会调用, 取消订阅并停止定时更新
    @Override
    public void onStop() {
        super.onStop();
        isShowing = false;
//        Log.d("child",getIndex()+"onStop");
    }

    //在切换到其他父fragment时不会触发当前的onPause(说明还是在一个显示状态), 按下home键才会
    @Override
    public void onPause() {
        super.onPause();
        isShowing = false;
//        Log.d("child",getIndex()+"onPause()");
    }

    public int getIndex(){
        return index;
    }

    public List<ParameterBean> getParameterList(){
        return parameterLiveData.getValue();
    }

    public void startFirstRequest(){
        viewModel.addToQueue(parameterLiveData);    //发送当前页的参数列表到请求队列
    }
}
