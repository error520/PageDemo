package com.kinco.MotorApp.ui.fourthpage;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.kinco.MotorApp.BluetoothService.BLEService;
import com.kinco.MotorApp.R;
import com.kinco.MotorApp.alertdialog.ErrorDialog;
import com.kinco.MotorApp.ui.firstpage.FirstpageFragment;
import com.kinco.MotorApp.util;
import com.kinco.MotorApp.ui.thirdpage.ThirdpageFragment;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class FourthpageFragment extends Fragment implements View.OnClickListener{
    private  String TAG="ff";
    private View view;//得到碎片对应的布局文件,方便后续使用
    private SurfaceHolder holder;
    private SurfaceView showSurfaceView;
    //按钮
    private Button btnShowBrokenLine;
    private Spinner spinner;

    private Paint paint;

    private int HEIGHT;
    // 要绘制的曲线的水平宽度
    private int WIDTH;
    // 离屏幕左边界的起始距离
    private final int X_OFFSET = 5;
    // 初始化X坐标
    private int cx = X_OFFSET;
    // 实际的Y轴的位置
    private int maxData = 0;
    private int centerY ;
    private Timer timer = new Timer();
    private TimerTask task = null;
    private int packageCount=0;
    private BLEService mBluetoothLeService;
    private LocalBroadcastManager localBroadcastManager;
    private BroadcastReceiver receiver=new LocalReceiver();
    private boolean mDrawing=false;
    private Handler mHnadler;
    private int data[] = new int[1024];
    private String[] addressList = {"0202","0203","0204"};
    private ArrayList<byte[]> packageList = new ArrayList();
    //记住一定要重写onCreateView方法
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.osc_page, container, false);//得到对应的布局文件
        return view;
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

            // 获得SurfaceView对象
        showSurfaceView = (SurfaceView) getActivity().findViewById(R.id.showSurfaceView);
        btnShowBrokenLine = (Button) getActivity().findViewById(R.id.btnShowBrokenLine);
        btnShowBrokenLine.setOnClickListener(this);
        spinner = getActivity().findViewById(R.id.OSCspinner);

        InitData();

        // 初始化SurfaceHolder对象
        holder = showSurfaceView.getHolder();
        paint = new Paint();
        paint.setColor(Color.GREEN);
        paint.setStrokeWidth(3);

        mHnadler=new Handler();
        mHnadler.postDelayed(new Runnable() {
            @Override
            public void run() {
                drawBackGround(holder);
            }
        },300);


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
        initService();
    }

    private void InitData() {
            Resources resources = this.getResources();
            DisplayMetrics dm = resources.getDisplayMetrics();
            //获取屏幕的宽度作为示波器的边长
            HEIGHT = dm.widthPixels;
            WIDTH = dm.widthPixels;
            Log.d("ff",HEIGHT+" "+WIDTH);
            //Y轴的中心就是高的一半
            centerY = HEIGHT / 2;

        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btnShowBrokenLine:
                    //showBrokenLine();
                    mBluetoothLeService.writeData(addressList[spinner.getSelectedItemPosition()],"0001");
                    packageCount=0;
                    packageList.clear();
                    mDrawing=true;
                    break;
            }

        }



        /**
         * 折线曲线
         */
        private void showBrokenLine(){

            drawBackGround(holder);
            cx = X_OFFSET;
            if (task != null) {
                task.cancel();
            }
            task = new TimerTask() {
                int startX = 0;
                int startY = 200;
                Random random = new Random();
                @Override
                public void run() {

                    int cy = random.nextInt(100)+200;

                    Canvas canvas = holder.lockCanvas(new Rect(cx-10, cy - 900,
                            cx + 10, cy + 900));

                    // 根据Ｘ，Ｙ坐标画线
                    canvas.drawLine(startX, startY ,cx, cy, paint);

                    //结束点作为下一次折线的起始点
                    startX = cx;
                    startY = cy;

                    cx+=10;
                    // 超过指定宽度，线程取消，停止画曲线
                    if (cx > WIDTH) {
                        task.cancel();
                        task = null;
                    }
                    // 提交修改
                    holder.unlockCanvasAndPost(canvas);
                }
            };
            timer.schedule(task, 0, 300);
        }

//        /**
//         * 正余弦曲线函数
//         */
//        private void showSineCord(final View view){
//            drawBackGround(holder);
//            cx = X_OFFSET;
//            if (task != null) {
//                task.cancel();
//            }
//            task = new TimerTask() {
//
//                @Override
//                public void run() {
//                    // 根据是正玄还是余玄和X坐标确定Y坐标
//                    int cy = view.getId()==R.id.btnShowSin?
//                            centerY- (int) (100 * Math.sin((cx - 5) * 2 * Math.PI/ 150))
//                            :centerY- (int) (100 * Math.cos((cx - 5) * 2 * Math.PI/ 150));
//
//                    Canvas canvas = holder.lockCanvas(new Rect(cx, cy - 2,
//                            cx + 2, cy + 2));
//                    // 根据Ｘ，Ｙ坐标画点
//                    canvas.drawPoint(cx, cy, paint);
//                    cx++;
//                    // 超过指定宽度，线程取消，停止画曲线
//                    if (cx > WIDTH) {
//                        task.cancel();
//                        task = null;
//                    }
//                    // 提交修改
//                    holder.unlockCanvasAndPost(canvas);
//                }
//            };
//            timer.schedule(task, 0, 30);
//        }

        private void drawBackGround(SurfaceHolder holder) {
            Canvas canvas = holder.lockCanvas();
            // 绘制黑色背景
            canvas.drawColor(Color.BLACK);
            Paint p = new Paint();
            p.setColor(Color.WHITE);
            p.setStrokeWidth(2);

            // 画网格8*8
            Paint mPaint = new Paint();
            mPaint.setTextSize(50);
            mPaint.setColor(Color.GRAY);// 网格为灰色
            mPaint.setStrokeWidth(1);// 设置画笔粗细
            int oldY = 0;
            for (int i = 0; i <= 8; i++) {// 绘画横线
                canvas.drawLine(0, oldY, WIDTH, oldY, mPaint);
                canvas.drawText(maxData+"",10,oldY,mPaint);
                oldY = oldY + WIDTH/8;

            }
            int oldX = 0;
            for (int i = 0; i <= 8; i++) {// 绘画纵线
                canvas.drawLine(oldX, 0, oldX, HEIGHT, mPaint);
                canvas.drawText(oldX+"",oldX+10,centerY+40,mPaint);
                oldX = oldX + HEIGHT/8;

            }



            // 绘制坐标轴
            canvas.drawLine(X_OFFSET, centerY, WIDTH, centerY, p);
            canvas.drawLine(X_OFFSET, 40, X_OFFSET, HEIGHT, p);
            holder.unlockCanvasAndPost(canvas);
            holder.lockCanvas(new Rect(0, 0, 0, 0));
            holder.unlockCanvasAndPost(canvas);
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


    private void draw(){
        maxData=0;
        final Iterator<Integer> data=packageToData(packageList).iterator();
        drawBackGround(holder);
            cx = X_OFFSET;
            if (task != null) {
                task.cancel();
            }
            task = new TimerTask() {
                int startX = 0;
                int startY = centerY;
                @Override
                public void run() {
                    if(!data.hasNext()){
                        task.cancel();
                        task = null;
                        return;
                    }

                    int cy = centerY-data.next();
                   // Log.d(TAG,cy+"");
                    Canvas canvas = holder.lockCanvas(new Rect(cx-1, cy - 900,
                            cx + 1, cy + 900));

                    // 根据Ｘ，Ｙ坐标画线
                    canvas.drawLine(startX, startY ,cx, cy, paint);

                    //结束点作为下一次折线的起始点
                    startX = cx;
                    startY = cy;

                    cx+=1;
                    // 超过指定宽度，线程取消，停止画曲线
                    if (cx > WIDTH) {
                        task.cancel();
                        task = null;
                    }
                    // 提交修改
                    holder.unlockCanvasAndPost(canvas);
                }
            };
            timer.schedule(task, 0, 5);
    }

    private ArrayList<Integer> packageToData(ArrayList<byte[]> packageList){
        ArrayList<Integer> data = new ArrayList<>();
        byte[] package1 = new byte[12];
        try {
            System.arraycopy(packageList.get(0), 8, package1, 0, 12);
        }catch(Exception e){
            ErrorDialog ed = new ErrorDialog(getContext(),"");
            ed.show();
            Log.d("ff",e.toString());
        }

        for(byte i: package1)
            Log.d(TAG,i+"");
        packageList.set(0,package1);
        int current;
        for(byte[] i:packageList){
              for(int j=0; j<i.length; j+=2){
                  current=util.byte2ToUnsignedShort(i[j],i[j+1]);
                  maxData=current>maxData?current:maxData;
                  data.add(current);
              }
        }
        Log.d("ff","data长度"+data.size());
        for(int i: data){
            Log.d(TAG,i+"");
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
                    Log.d("ff",util.toHexString(message,true)+"\n"+packageCount+"");
                    if(packageCount==102){//102
                        draw();
                    }
                    packageCount++;

                }
            }
            else if(action.equals(BLEService.ACTION_GATT_DISCONNECTED)) {
                util.centerToast(getContext(),"Bluetooth disconnected!",0);
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

}

