package com.kinco.MotorApp.ui.fourthpage;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Bitmap;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import com.kinco.MotorApp.R;
import com.kinco.MotorApp.utils.util;


class DragImage extends SurfaceView implements SurfaceHolder.Callback, View.OnTouchListener, ScaleGestureDetector.OnScaleGestureListener {
        private Context context;
        private SurfaceHolder holder;
        private Bitmap icon;
        private Paint paint;
        private boolean running=true;
        private float scale;
        private float preScale = 1;// 默认前一次缩放比例为1
        ScaleGestureDetector dd;

        public DragImage(Context context) {
            super(context);
            this.context=context;
            holder = this.getHolder();//获取holder
            holder.addCallback(this);
            this.setOnTouchListener(this);
            dd = new ScaleGestureDetector(context,this);
        }

        @Override
        public void surfaceCreated(SurfaceHolder holder) {

            icon = BitmapFactory.decodeResource(context.getResources(),R.mipmap.ic_launcher_foreground);
            paint=new Paint();
            running=true;
           // new Thread(this).start();

        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width,
                                   int height) {
        }


        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            running=false;
        }

    @Override
    public boolean onScale(ScaleGestureDetector detector) {

        float previousSpan = detector.getPreviousSpan();
        float currentSpan = detector.getCurrentSpan();
        if (currentSpan < previousSpan) {
            // 缩小
             //scale = preScale-detector.getScaleFactor();
            scale = preScale - (previousSpan - currentSpan) / 500;
        } else {
            // 放大
             scale = preScale+detector.getScaleFactor()/3;
            //scale = preScale + (currentSpan - previousSpan) / 1000;
        }



            return false;
    }

    @Override
    public boolean onScaleBegin(ScaleGestureDetector detector) {
        return true;
    }

    @Override
    public void onScaleEnd(ScaleGestureDetector detector) {
        Matrix mMatrix = new Matrix();
        mMatrix.setScale(scale, scale);
            // 锁定整个SurfaceView
        Canvas mCanvas = holder.lockCanvas();
        // 清屏
        mCanvas.drawColor(Color.BLACK);
        // 画缩放后的图
        mCanvas.drawBitmap(icon, mMatrix, null);
        // 绘制完成，提交修改
        holder.unlockCanvasAndPost(mCanvas);
        // 重新锁一次
        holder.lockCanvas(new Rect(0, 0, 0, 0));
        holder.unlockCanvasAndPost(mCanvas);
        util.centerToast(context,scale+"",0);
            preScale = scale;
    }


    //@Override
        public void run() {
            int SLEEP_TIME=100;
            while (running) {
                //开始画的时间    long start=System.currentTimeMillis();
//                Canvas canvas = holder.lockCanvas();//获取画布
//                canvas.drawColor(Color.GREEN);
//                canvas.drawBitmap(icon, rect.left,rect.top,null);
//                holder.unlockCanvasAndPost(canvas);// 解锁画布，提交画好的图像
                //结束的时间   long end=System.currentTimeMillis();
            }
        }

        //		Region region=new Region();
        private Point point=new Point();//点击点
        private Rect rect=new Rect(0,0,400,1000);//图片的rect
        private boolean canDrag=false;//判断是否点击在图片上，否则拖动无效
        private int offsetX=0,offsetY=0;//点击点离图片左上角的距离
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            dd.onTouchEvent(event);
            return true;
            // TODO Auto-generated method stub
//            switch (event.getAction()) {
//
//                //手按下的时候
//                case MotionEvent.ACTION_DOWN:
//                    point.x=(int)event.getX();
//                    point.y=(int)event.getY();
//                    if(rect.contains(point.x, point.y)){
//                        canDrag=true;
//                        offsetX=point.x-rect.left;
//                        offsetY=point.y-rect.top;
//                    }
//
//                    break;
//
//                //移动的时候
//                case MotionEvent.ACTION_MOVE:
//                    if(canDrag){
//                        rect.left=(int)event.getX()-offsetX;
//                        rect.top=(int)event.getY()-offsetY;
//                        rect.right=rect.left+icon.getWidth();
//                        rect.bottom=rect.top+icon.getHeight();
//                        if (rect.left < 0) {
//                            rect.left = 0;
//                            rect.right =  rect.left+icon.getWidth();
//                        }
//                        if (rect.right >  getMeasuredWidth()) {
//                            rect.right =  getMeasuredWidth();
//                            rect.left = rect.right-icon.getWidth();
//                        }
//                        if (rect.top < 0) {
//                            rect.top = 0;
//                            rect.bottom = rect.top+icon.getHeight();
//                        }
//                        if (rect.bottom > getMeasuredHeight()) {
//                            rect.bottom = getMeasuredHeight();
//                            rect.top = rect.bottom-icon.getHeight();
//                        }
//                    }
//                    break;
//                case MotionEvent.ACTION_UP:
//                    canDrag=false;
//                    break;
//
//                default:
//                    break;
//            }
//            return true;
        }

    }

