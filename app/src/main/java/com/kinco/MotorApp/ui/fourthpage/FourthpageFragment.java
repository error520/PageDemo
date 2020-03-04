package com.kinco.MotorApp.ui.fourthpage;

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
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.kinco.MotorApp.BluetoothService.BLEService;
import com.kinco.MotorApp.R;
import com.kinco.MotorApp.alertdialog.LoadingDialog;
import com.kinco.MotorApp.alertdialog.SaveFileDialog;
import com.kinco.MotorApp.ui.functionpage.FileActivity;
import com.kinco.MotorApp.utils.util;
import com.kinco.MotorApp.sys.MyFragment;

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

public class FourthpageFragment extends MyFragment implements View.OnClickListener{
    private String TAG = "fourth";

    private View view;//得到碎片对应的布局文件,方便后续使用
    private SurfaceHolder holder;
    private MySurfaceView showSurfaceView;
    //按钮
    private Button btnShowBrokenLine;
    private Spinner spinner;
    private LoadingDialog loadingDialog;

    private float centerY ;
    private Timer timer = new Timer();
    private TimerTask task = null;
    private int packageCount=0;
    private BLEService mBluetoothLeService;
    private LocalBroadcastManager localBroadcastManager;
    private BroadcastReceiver receiver=new LocalReceiver();
    private boolean mDrawing=false;
    private Handler mHnadler;
    private int data[] = new int[1024];
    private String[] addressList = {"0204","0202","0203"};
    private ArrayList<byte[]> packageList = new ArrayList();
    private float maxData = 0;
    private float minData=0;
    private float average = 0;
    private int count=1;
    private TextView fileName;

    //记住一定要重写onCreateView方法
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_fourth, container, false);//得到对应的布局文件
        return view;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

            // 获得SurfaceView对象
        showSurfaceView = (MySurfaceView)getActivity().findViewById(R.id.MySV3);
        showSurfaceView.post(new Runnable() {
            @Override
            public void run() {
//                WIDTH = showSurfaceView.getWidth();
//                HEIGHT = showSurfaceView.getHeight();
//                //获取SurfaceView真实的宽高
                showSurfaceView.surfaceCreated(showSurfaceView.getHolder());

            }
        });
        btnShowBrokenLine = (Button) getActivity().findViewById(R.id.btnShow);
        Button btnSave = getActivity().findViewById(R.id.btnSave);
        Button btnOpen = getActivity().findViewById(R.id.btnOpen);
        fileName = getActivity().findViewById(R.id.fileName_tv);
        btnShowBrokenLine.setOnClickListener(this);
        btnSave.setOnClickListener(this);
        btnOpen.setOnClickListener(this);
        spinner = getActivity().findViewById(R.id.OSCspinner);
        mHnadler=new Handler();
    }

    @Override
    public void onStop() {
        super.onStop();
        if(!(task==null)){
            task.cancel();
            task=null;
        }
        localBroadcastManager.unregisterReceiver(receiver);
    }


    @Override
    public void onStart() {
        super.onStart();
        if(Showing)
            initService();
    }

    @Override
    public void onResume() {
        super.onResume();
        //Log.d("fourth","真实的高:"+showSurfaceView.getMeasuredHeight());
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

    @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btnShow:
                    //testRandomDraw(count);
                    mBluetoothLeService.writeData(addressList[spinner.getSelectedItemPosition()],"0001");
                    packageCount=0;
                    packageList.clear();
                    mDrawing=true;
                    mHnadler.postDelayed(mRunnable,10000);
                    loadingDialog = showLoadingDialog();
                    break;
                case R.id.btnSave:
                    if(!saveWaveFile())
                        util.centerToast(getContext(),"当前没有波形!",0);
                    break;
                case R.id.btnOpen:
                    Intent intent = new Intent(getContext(), FileActivity.class);
                    startActivityForResult(intent,1);
                    //readWave("/storage/emulated/0/KincoLog/02-17|07-20-43wave.wave");
                    break;
            }

        }


    public FourthpageFragment newInstance(int i) {
        Bundle args = new Bundle();
        args.putInt("int", i);
        FourthpageFragment fragment = new FourthpageFragment();
//        fragment.setArguments(args);
        return fragment;
    }

    /**
     * 初始化服务和广播
     */
    private void initService(){
        //绑定服务
        Intent BLEIntent = new Intent(getActivity(), BLEService.class);
        getActivity().bindService(BLEIntent,connection, Context.BIND_AUTO_CREATE);
        localBroadcastManager = LocalBroadcastManager.getInstance(getContext());
        localBroadcastManager.registerReceiver(receiver, util.makeGattUpdateIntentFilter());
    }

    private void showWave(){
        if(loadingDialog!=null)
            loadingDialog.gone();
        mHnadler.removeCallbacks(mRunnable);
        showSurfaceView.drawWave(packageToData(packageList));
        reloadFileName(getString(R.string.device));
    }

    /**
     * 画随机数据的
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
//        byte[] package1 = new byte[12];
//        try {
//            System.arraycopy(packageList.get(0), 8, package1, 0, 12);
//        }catch(Exception e){
//            ErrorDialog ed = new ErrorDialog(getContext(),"");
//            ed.show();
//            Log.d(TAG,e.toString());
//        }
//
//        for(byte i: package1)
//            Log.d(TAG,i+"");
//        packageList.set(0,package1);
        float current=0;
        int index=0;
        try {
            for (byte[] i : packageList) {
                for (int j = 0; j < i.length; j += 2) {
                    switch (spinner.getSelectedItemPosition()) {
                        case 0: {
                            //有符号short数
                            //current = ((float) (short) (util.byte2ToUnsignedShort(i[j], i[j + 1]))) / 100;
                            byte[] bytes = {i[j], i[j + 1]};
                            current = Float.valueOf(util.parseByteData2(bytes,0,0.01f,"~300"));
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
                    if (index == 0 && j == 0) {
                        maxData = minData = current;
                    } else {
                        maxData = Math.max(maxData, current);
                        minData = Math.min(minData, current);
                    }
                }
                index++;
            }
            average = (maxData+minData)/2;
            for(int i=0; i<4; i++)
                data.remove(0);
            Log.d(TAG,"data长度"+data.size());
            for(int i=0;i<data.size();i++){
                Log.d(TAG,data.get(i)+"");
            }
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
            if(action.equals(BLEService.ACTION_DATA_AVAILABLE)) {
                if (mDrawing) {
                    byte[] message = intent.getByteArrayExtra(BLEService.EXTRA_MESSAGE_DATA);
                    packageList.add(message);
                    Log.d(TAG,util.toHexString(message,true)+"\n"+packageCount+"");
                    if(packageCount==103){//102
                        showWave();
                        //testDraw();
                        return;
                    }

                    packageCount++;

                }
            }
            else if(action.equals(BLEService.ACTION_GATT_DISCONNECTED)) {
                util.centerToast(getContext(),"Bluetooth disconnected!",0);
                if(loadingDialog!=null)
                    loadingDialog.gone();
            }
            else if(action.equals(BLEService.ACTION_ERROR_CODE)){
                String errorCode = intent.getStringExtra(BLEService.ACTION_ERROR_CODE);
                Toast toast = Toast.makeText(getContext(),"error code:"+errorCode,Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER,0,0);
                toast.show();

            }
        }
    }

    /**
     * 得到服务实例
     */
    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mBluetoothLeService = ((BLEService.localBinder) service)
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
//        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        Date curDate = new Date(System.currentTimeMillis());//获取当前时间
//        String str = formatter.format(curDate);
//        FileUtil.saveFile(0,"wave"+str+".wave",sb.toString());
//        util.centerToast(getContext(),"保存成功!",0);
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
            float[] finalData = new float[1024];
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
            InputStream in = getContext().getContentResolver().openInputStream(uri);
            StringBuilder sb = new StringBuilder();
            int n;
            byte[] bytes = new byte[1024];
            while((n=in.read(bytes))!=-1)
                sb.append(new String(bytes,0,n));
            String[] data = sb.toString().split(",");
            float[] finalData = new float[1024];
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
            util.centerToast(getContext(),"超时未响应!",1);
        }
    };



    LoadingDialog showLoadingDialog(){
        final LoadingDialog loadingDialog = new LoadingDialog(getContext(),"",
                getString(R.string.loading),false);
        return loadingDialog;
    }

    private void reloadFileName(String fileName){
        this.fileName.setText(getString(R.string.from)+fileName);
    }


}

