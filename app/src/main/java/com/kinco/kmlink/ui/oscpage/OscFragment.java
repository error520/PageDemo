package com.kinco.kmlink.ui.oscpage;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.kinco.kmlink.BluetoothService.BleService;
import com.kinco.kmlink.R;
import com.kinco.kmlink.alertdialog.LoadingDialog;
import com.kinco.kmlink.alertdialog.SaveFileDialog;
import com.kinco.kmlink.sys.SysApplication;
import com.kinco.kmlink.ui.oscpage.filepage.FileActivity;
import com.kinco.kmlink.utils.util;
import com.kinco.kmlink.sys.MyFragment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import static android.app.Activity.RESULT_OK;

public class OscFragment extends MyFragment implements View.OnClickListener{
    private String TAG = "fourth";

    private View view;//得到碎片对应的布局文件,方便后续使用
    private MySurfaceView showSurfaceView;

    private Button btnShow;
    private Spinner spinner;
    private LoadingDialog loadingDialog;
    private ProgressBar progressBar;
    private ImageView errorSign;

    private Timer timer = new Timer();
    private TimerTask task = null;
    private int packageCount=0;
    private BleService mBluetoothLeService;
    private LocalBroadcastManager localBroadcastManager;
    private BroadcastReceiver receiver=new LocalReceiver();
    private Handler mHandler;
    private String[] addressList = {"0204","0202","0203"};
    private ArrayList<byte[]> packageList = new ArrayList();
    private boolean mReceiving = false;
    private TextView fileName;

    //记住一定要重写onCreateView方法
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_osc, container, false);//得到对应的布局文件
        return view;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // 获得SurfaceView对象
        showSurfaceView = (MySurfaceView)getActivity().findViewById(R.id.MySV3);
        showSurfaceView.post(() -> {
//                WIDTH = showSurfaceView.getWidth();
//                HEIGHT = showSurfaceView.getHeight();
//                //获取SurfaceView真实的宽高
            showSurfaceView.surfaceCreated(showSurfaceView.getHolder());

        });
        btnShow = getActivity().findViewById(R.id.btnShow);
        progressBar = getActivity().findViewById(R.id.my_progressbar);
        progressBar.setMax(103);
        errorSign = getActivity().findViewById(R.id.error_sign);
        Button btnSave = getActivity().findViewById(R.id.btnSave);
        Button btnOpen = getActivity().findViewById(R.id.btnOpen);
        fileName = getActivity().findViewById(R.id.fileName_tv);
        btnShow.setOnClickListener(this);
        btnSave.setOnClickListener(this);
        btnOpen.setOnClickListener(this);
        spinner = getActivity().findViewById(R.id.OSCspinner);
        mHandler=new Handler();
    }

    @Override
    public void onStop() {
        super.onStop();
        if(!(task==null)){
            task.cancel();
            task=null;
        }
        mReceiving = false;
        receivingUI(false);
        mHandler.removeCallbacks(mRunnable);
        localBroadcastManager.unregisterReceiver(receiver);
    }


    @Override
    public void onStart() {
        super.onStart();
        if(showing)
            initService();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK){
            String path = data.getStringExtra("path");
            readWave(path);
            String name = path.substring(path.lastIndexOf("/")+1);
            reloadFileName(name);
        }
    }

    /**
     * 按钮回调函数
     * @param view
     */
    @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btnShow:
                    //testRandomDraw(count);
                    if(!mReceiving){
                        receivingUI(true);
                        mReceiving = true;
                        sendRequest();
                        //loadingDialog = showLoadingDialog();
                    }else{
                        receivingUI(false);
                        mReceiving = false;
                        mHandler.removeCallbacks(mRunnable);
                    }
                    break;
                case R.id.btnSave:
                    if(!saveWaveFile())
                        util.centerToast(getContext(),getString(R.string.current_no_wave),0);
                    break;
                case R.id.btnOpen:
                    Intent intent = new Intent(getContext(), FileActivity.class);
                    startActivityForResult(intent,1);
                    getActivity().overridePendingTransition(R.anim.slide_left_in, R.anim.slide_left_out);
                    break;
            }

        }


    public OscFragment newInstance(int i) {
        Bundle args = new Bundle();
        args.putInt("int", i);
        OscFragment fragment = new OscFragment();
//        fragment.setArguments(args);
        return fragment;
    }

    /**
     * 初始化服务和广播
     */
    private void initService(){
        //绑定服务
        Intent BLEIntent = new Intent(getActivity(), BleService.class);
        getActivity().bindService(BLEIntent,connection, Context.BIND_AUTO_CREATE);
        localBroadcastManager = LocalBroadcastManager.getInstance(getContext());
        localBroadcastManager.registerReceiver(receiver, util.makeGattUpdateIntentFilter());
    }

    private void showWave(){
        if(loadingDialog!=null)
            loadingDialog.gone();
        mHandler.removeCallbacks(mRunnable);
        showSurfaceView.drawWave(packageToData(packageList));
        reloadFileName(getString(R.string.device));
    }

    /**
     * 测试用
     */
    void testRandomDraw(int scale){
        float data[] = createSinData();
//        for(int i=0;i<100;i++){
//            data[i]=0;
//        }
//        for(int i=400;i<500;i++){
//            data[i]=325;
//        }
//        for(int i=500;i<600;i++){
//            data[i]=200;
//        }
        showSurfaceView.drawWave(data,scale);
    }



    /**
     * 生成随机数据数组用于测试
     * @return
     */
    int[] createRandomData(){
        int []data = new int[1024];
        Random random = new Random();
        for(int i=0;i<1024;i++)
            data[i] = random.nextInt(500);
        return data;
    }

    /**
     * 生成正弦波数据
     */
    float[] createSinData(){
        float []data = new float[1024];
        for(int i=0; i<1024; i++) {
            data[i] = (float) Math.sin(Math.PI * 2 * i / 1024) * 100;
            Log.d(TAG,data[i]+"");
        }
         return data;
    }



    /**
     * 剔除应答报文并将数据包转为可用数据
     * @param packageList
     * @return
     */
    private ArrayList<Float> packageToData(ArrayList<byte[]> packageList){
        ArrayList<Float> data = new ArrayList<>();
        float current=0;
        try {
            for (byte[] i : packageList) {
                for (int j = 0; j < i.length; j += 2) {
                    switch (spinner.getSelectedItemPosition()) {
                        case 0: {
                            //有符号short数
                            //current = ((float) (short) (util.byte2ToUnsignedShort(i[j], i[j + 1]))) / 100;
                            byte[] bytes = {i[j], i[j + 1]};
                            current = Float.valueOf(util.parseByteData2(bytes,0,0.01f,"~60000"));
                            data.add(current);
                        }
                        break;
                        case 1: {
                            current = util.byte2ToUnsignedShort(i[j], i[j + 1]);
                            data.add(current);
                        }
                        break;
                        case 2: {
                            current = util.byte2ToUnsignedShort(i[j], i[j + 1]) / 10;
                            data.add(current);
                        }
                        break;
                    }

                }
            }
            for(int i=0; i<4; i++)
                data.remove(0);
            Log.d(TAG,"data长度"+data.size());
//            for(int i=0;i<data.size();i++){
//                Log.d(TAG,data.get(i)+"");
//            }
        }catch (Exception e){
            data.clear();
            data.add(0f);
            util.centerToast(getContext(),"接收数据有误!",1);
            Log.d(TAG,"数据有误!");
        }

        return data;

    }

    /**
     * 接收到广播后的行为
      */
    private class LocalReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(action.equals(BleService.ACTION_DATA_AVAILABLE)) {
                if (mReceiving) {
                    byte[] message = intent.getByteArrayExtra(BleService.EXTRA_MESSAGE_DATA);
                    packageList.add(message);
                    //Log.d(TAG,util.toHexString(message,true)+"\n"+packageCount+"");
                    updateProcess();
                    if(packageCount>50&&message.length==8){//正常情况是103
                        errorUI(packageCount!=103);//判断数量对不对,是否显示错误标志
                        showWave();
                        sendRequest();  //接收完一帧1024个数据,请求下一帧
                        //testDraw();
                        return;
                    }

                    packageCount++;

                }
            }
            else if(action.equals(BleService.ACTION_GATT_DISCONNECTED)) {
                util.centerToast(getContext(),getString(R.string.device_disconnected),0);
                receivingUI(false);
                mHandler.removeCallbacks(mRunnable);
                if(loadingDialog!=null)
                    loadingDialog.gone();
            }
            else if(action.equals(BleService.ACTION_ERROR_CODE)){
                String errorCode = intent.getStringExtra(BleService.ACTION_ERROR_CODE);
                Toast toast = Toast.makeText(getContext(),"error code:"+errorCode,Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER,0,0);
                toast.show();

            }
        }
    }

    private void updateProcess() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressBar.setProgress(packageCount,true);
            }
        });
    }

    /**
     * 得到服务实例
     */
    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mBluetoothLeService = ((BleService.localBinder) service)
                    .getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    private boolean saveWaveFile(){
        if(showSurfaceView.data==null)
            return false;
        StringBuilder sb = new StringBuilder();
        for(float i:showSurfaceView.data)
            sb.append(i+",");
        sb.deleteCharAt(sb.length()-1);
        SaveFileDialog saveFileDialog = new SaveFileDialog(getContext(),sb);
        return true;
    }

    public boolean readWave(String path){
        FileInputStream in = null;
        BufferedReader reader= null;
        try{
            File fs = new File(path);
            in = new FileInputStream(fs);
            byte[] bytes= new byte[1024];
            int n=0;
            //循环读取  
            StringBuilder sb = new StringBuilder();
            while((n=in.read(bytes))!=-1){
                sb.append(new String(bytes,0,n));
            }
            //Log.d(TAG,sb.toString());
            String[] data = sb.toString().split(",");
            float[] finalData = new float[data.length];
            for(int i=0; i<data.length;i++){
                finalData[i] = Float.valueOf(data[i]);
                //Log.d(TAG,data[i]);
            }
            try {
                showSurfaceView.drawWave(finalData, 1);
            }catch(Exception e){
                Log.d(TAG,e.toString());
            }

        } catch (IOException e) {
            Log.d(TAG,e.toString());
        }
        finally {
            try{
                if(reader!=null)
                    reader.close();
            }catch(Exception e){
                e.printStackTrace();
            }
            Log.d("BLEService","读取成功!");
        }
        return true;
    }

    public void readWave(Uri uri){
        try{
            InputStream in = SysApplication.getContext().getContentResolver().openInputStream(uri);
            StringBuilder sb = new StringBuilder();
            int n;
            byte[] bytes = new byte[1024];
            while((n=in.read(bytes))!=-1)
                sb.append(new String(bytes,0,n));
            String[] data = sb.toString().split(",");
            float[] finalData = new float[data.length];
            for(int i=0; i<data.length;i++){
                finalData[i] = Float.valueOf(data[i]);
            }
            showSurfaceView.drawWave(finalData,1);
        }catch (FileNotFoundException e){
            util.centerToast(getContext(),"读取文件失败",0);
        }catch (IOException e){
            util.centerToast(getContext(),"读取中断",0);
        }

    }

    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            if(loadingDialog!=null)
                loadingDialog.gone();
            util.centerToast(getContext(),getString(R.string.timeout),1);
        }
    };



    LoadingDialog showLoadingDialog(){
        final LoadingDialog loadingDialog = new LoadingDialog(getContext(),"",
                getString(R.string.loading),false);
        return loadingDialog;
    }

    private void sendRequest(){
        packageCount=0;
        packageList.clear();
        mHandler.postDelayed(mRunnable,5000);
        mBluetoothLeService.writeData(addressList[spinner.getSelectedItemPosition()],"0001");
    }

    private void reloadFileName(String fileName){
        this.fileName.setText(getString(R.string.from)+fileName);
    }

    private void receivingUI(boolean receiving){
        if(receiving){
            btnShow.setText(getString(R.string.stop));
            progressBar.setVisibility(View.VISIBLE);
        }else{
            btnShow.setText(getString(R.string.Show));
            progressBar.setVisibility(View.INVISIBLE);
            progressBar.setProgress(0);
            errorUI(false);
        }

    }

    private void errorUI(boolean error){
        if(error)
            errorSign.setVisibility(View.VISIBLE);
        else
            errorSign.setVisibility(View.INVISIBLE);
    }


}

