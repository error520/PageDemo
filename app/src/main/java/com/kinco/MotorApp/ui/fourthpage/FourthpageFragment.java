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
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.kinco.MotorApp.BluetoothService.BLEService;
import com.kinco.MotorApp.R;
import com.kinco.MotorApp.alertdialog.ErrorDialog;
import com.kinco.MotorApp.util;
import com.kinco.MotorApp.sys.MyFragment;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class FourthpageFragment extends MyFragment implements View.OnClickListener{
    private String TAG = "fourth";

    private View view;//得到碎片对应的布局文件,方便后续使用
    private SurfaceHolder holder;
    private MySurfaceView3 showSurfaceView;
    //按钮
    private Button btnShowBrokenLine;
    private Spinner spinner;

    private Paint paint;

    private int HEIGHT;
    // 要绘制的曲线的水平宽度
    private int WIDTH;
    // 离屏幕左边界的起始距离
    private final int X_OFFSET = 2;
    // 初始化X坐标
    private int cx = X_OFFSET;
    // 实际的Y轴的位置
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

    //记住一定要重写onCreateView方法
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.osc_page, container, false);//得到对应的布局文件
        return view;
        //DragImage dragImage = new DragImage(getContext());
        //return dragImage;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

            // 获得SurfaceView对象
        showSurfaceView = (MySurfaceView3)getActivity().findViewById(R.id.MySV3);
        showSurfaceView.post(new Runnable() {
            @Override
            public void run() {
                WIDTH = showSurfaceView.getWidth();
                HEIGHT = showSurfaceView.getHeight();
                centerY = HEIGHT / 2;
                Log.i(TAG, "showSurfaceView Height is " + HEIGHT + ", Width is " + WIDTH);
            }
        });
        btnShowBrokenLine = (Button) getActivity().findViewById(R.id.btnShow);
        btnShowBrokenLine.setOnClickListener(this);
        spinner = getActivity().findViewById(R.id.OSCspinner);

        InitData();

        // 初始化SurfaceHolder对象,被获取后会被锁住
        //holder = showSurfaceView.getHolder();

        paint = new Paint();
        paint.setColor(Color.GREEN);
        paint.setStrokeWidth(3);

        mHnadler=new Handler();
        mHnadler.postDelayed(new Runnable() {
            @Override
            public void run() {
                showSurfaceView.surfaceCreated(showSurfaceView.getHolder());
            }
        },100);

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

        //util.centerToast(getContext(),"4被开启",0);
//        mHnadler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                showSurfaceView.surfaceCreated(showSurfaceView.getHolder());
//            }
//        },300);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("fourth","真实的高:"+showSurfaceView.getMeasuredHeight());
    }

    private void InitData() {
            Resources resources = this.getResources();
            //DisplayMetrics dm = resources.getDisplayMetrics();
            //获取屏幕的宽度作为示波器的边长
            //Y轴的中心就是高的一半
            //centerY = HEIGHT / 2;

        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btnShow:
                    //showBrokenLine();
//                    if(count>5)
//                        count=1;
//                    else{
//                        count++;
                        testRandomDraw(5);
                    //}
//                    mBluetoothLeService.writeData(addressList[spinner.getSelectedItemPosition()],"0001");
//                    packageCount=0;
//                    packageList.clear();
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


        private void drawBackGround(SurfaceHolder holder) {
            Canvas canvas = holder.lockCanvas();
            // 绘制黑色背景
            canvas.drawColor(Color.BLACK);
            Paint p = new Paint();
            p.setColor(Color.WHITE);
            p.setStrokeWidth(5);

            // 画网格8*8
            Paint mPaint = new Paint();
            mPaint.setTextSize(50);
            mPaint.setColor(Color.GRAY);// 网格为灰色
            mPaint.setStrokeWidth(3);// 设置画笔粗细
            int oldY = 0;
            for (int i = 0; i <= 10; i++) {// 绘画横线
                canvas.drawLine(0, oldY, WIDTH, oldY, mPaint);
                if(spinner.getSelectedItemPosition()==0) {
                    if (i != 4)
                        canvas.drawText(maxData / 4 * (4 - i) + "", 10, oldY, mPaint);
                }else
                    canvas.drawText(maxData / 4 * (4 - i) + "", 10, oldY, mPaint);
                oldY = oldY + WIDTH / 8;

            }
            int oldX = 0;
            for (int i = 0; i <= 8; i++) {// 绘画纵线
                canvas.drawLine(oldX, 0, oldX, HEIGHT, mPaint);
                if(i%2==0)
                    canvas.drawText(oldX+"",oldX+10,centerY+40,mPaint);
                oldX = oldX + WIDTH/8;

            }



            // 绘制坐标轴
            canvas.drawLine(X_OFFSET, centerY, WIDTH, centerY, p);
            canvas.drawLine(X_OFFSET, 40, X_OFFSET, HEIGHT, p);
            holder.unlockCanvasAndPost(canvas);
            holder.lockCanvas(new Rect(0, 0, 0, 0));
            holder.unlockCanvasAndPost(canvas);
        }

    /**
     * 绘制网格和坐标轴
     * @param canvas
     * @param scale
     */
    private void drawBackGround(Canvas canvas,int scale) {
            // 绘制黑色背景
        canvas.drawColor(Color.BLACK);
        Paint p = new Paint();
        p.setColor(Color.WHITE);
        p.setStrokeWidth(5);
        p.setTextSize(50);
        // 画网格8*8
        Paint mPaint = new Paint();
        mPaint.setTextSize(50);
        mPaint.setColor(Color.GRAY);// 网格为灰色
        mPaint.setStrokeWidth(3);// 设置画笔粗细
        int oldY = 0;
        float lineStep = HEIGHT / 10;
        float textStep = (maxData-minData)/9;
        for (int i = 0; i <= 10*scale; i++) {// 绘画横线
            canvas.drawLine(0, oldY, WIDTH*scale, oldY, mPaint);
            if(i!=0&&i!=10) {
                float text = average + (4 - i) * textStep;
                canvas.drawText(String.format("%.2f", text), 10, oldY, p);
            }
            Log.d(TAG,"这是坐标轴"+(average+(4-i)*textStep));
            oldY = oldY + HEIGHT / 10;

        }
        int oldX = 0;
        for (int i = 0; i <= 8*scale; i++) {// 绘画纵线
            canvas.drawLine(oldX, 0, oldX, HEIGHT*scale, mPaint);
            if(i%2==0)
                canvas.drawText(oldX+"",oldX+10,centerY+40,mPaint);
            oldX = oldX + WIDTH/8;

        }

        // 绘制坐标轴
        canvas.drawLine(X_OFFSET, centerY, WIDTH, centerY, p);
        canvas.drawLine(X_OFFSET, 40, X_OFFSET, HEIGHT, p);
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


    /**
     * 画随机数据的
     */
    void testRandomDraw(int scale){
        Bitmap whiteBgBitmap = Bitmap.createBitmap(WIDTH*scale,HEIGHT*scale, Bitmap.Config.ARGB_4444);
        Canvas canvas = new Canvas(whiteBgBitmap);
        //drawBackGround(canvas,scale);
        Paint mpaint = new Paint();
        mpaint.setColor(Color.GREEN);
        mpaint.setStrokeWidth(10);//3*scale/2
        float data[] = createSinData();
        data[1]=0;
        data[2]=5;
        data[3]=0;
        data[4]=5;
        data[5]=0;
        data[6]=5;
        data[7]=0;
//        float oldY=centerY*scale;
//        float oldX=0;
//        float cx = 0;
//        for(int i=0;i<1024;i++) {
//            cx+=scale;
//            float cy = centerY*scale-data[i]*scale;
//            canvas.drawLine(oldX,oldY+10,cx,cy+10,mpaint);
//            oldX=cx;
//            oldY = cy;
//        }
        showSurfaceView.drawWave(data);
    }

    /**
     * 画正式数据的
     */
    void testDraw(){
        final Iterator<Float> data=packageToData(packageList).iterator();//这里面会更新maxData
        Bitmap whiteBgBitmap = Bitmap.createBitmap(WIDTH,HEIGHT, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(whiteBgBitmap);
        drawBackGround(canvas,1);
        Paint mpaint = new Paint();
        mpaint.setColor(Color.GREEN);
        mpaint.setStrokeWidth(3);
        float oldY=centerY;
        float oldX=0;
        float cx = 0;
        float weight = (maxData-minData)/8 * HEIGHT*8/10;
        Log.d(TAG,"最大和最小"+maxData+","+minData);
        while(data.hasNext()){
            float cy = centerY-(data.next()-average)*weight;
            //Log.d(TAG,"这是cy:"+cy+"");
            canvas.drawLine(oldX,oldY,cx,cy,mpaint);
            oldX = cx;
            cx+=1;
            oldY = cy;
        }

        //canvas.drawBitmap(whiteBgBitmap,0,0,null);
        showSurfaceView.setBitmap(whiteBgBitmap);

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
            data[i] = (float) Math.sin(Math.PI * 2 * i / 1024) * 500;
            Log.d(TAG,data[i]+"");
        }
         return data;
    }

    /**
     * 动态绘制曲线的
     */
    private void draw(){
        maxData=0;
        final Iterator<Float> data=packageToData(packageList).iterator();//这里面会更新maxData
        drawBackGround(holder);
            cx = X_OFFSET;
            if (task != null) {
                task.cancel();
            }
            task = new TimerTask() {
                int startX = 0;
                float startY = centerY;
                Bitmap mbmpTest = Bitmap.createBitmap(WIDTH,HEIGHT, Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(mbmpTest);
                @Override
                public void run() {

                    if(!data.hasNext()){
                        task.cancel();
                        task = null;
                        Canvas cv = holder.lockCanvas();
                        cv.drawBitmap(mbmpTest,10,10,null);
                        holder.unlockCanvasAndPost(cv);
                        return;
                    }

                    float cy = 10;//centerY-(data.next()/maxData)*centerY;
                   // Log.d(TAG,cy+"");
//                    Canvas canvas = holder.lockCanvas(new Rect(cx-1, (int)cy - 900,
//                            cx + 1, (int)cy + 900));



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
                   // holder.unlockCanvasAndPost(canvas);
                }
            };
            timer.schedule(task, 0, 5);
    }

    /**
     * 剔除应答报文并将数据包转为可用数据
     * @param packageList
     * @return
     */
    private ArrayList<Float> packageToData(ArrayList<byte[]> packageList){
        ArrayList<Float> data = new ArrayList<>();
        byte[] package1 = new byte[12];
        try {
            System.arraycopy(packageList.get(0), 8, package1, 0, 12);
        }catch(Exception e){
            ErrorDialog ed = new ErrorDialog(getContext(),"");
            ed.show();
            Log.d(TAG,e.toString());
        }

        for(byte i: package1)
            Log.d(TAG,i+"");
        packageList.set(0,package1);
        float current=0;
        int index=0;
        for(byte[] i:packageList){
              for(int j=0; j<i.length; j+=2){
                  switch(spinner.getSelectedItemPosition()){
                      case 0:{
                          current = ((float) (short) (util.byte2ToUnsignedShort(i[j], i[j + 1]))) / 100;
                          data.add(current);
                      }break;
                      case 1:{
                          current=util.byte2ToUnsignedShort(i[j], i[j + 1]);
                          data.add(current);
                      }break;
                      case 2:{
                          current=util.byte2ToUnsignedShort(i[j], i[j + 1])/10;
                          data.add(current);
                      }break;
                  }
                  if (index==0&&j==0){
                      maxData=minData=current;
                  }else {
                      maxData = Math.max(maxData, current);
                      minData = Math.min(minData, current);
                  }
              }
              index++;
        }
        average = (maxData+minData)/2;
        Log.d(TAG,"data长度"+data.size());
//        for(float i: data){
//            Log.d(TAG,i+"");
//        }
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
                    //Log.d(TAG,util.toHexString(message,true)+"\n"+packageCount+"");
                    if(packageCount==102){//102
                        //draw();
                        testDraw();
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

