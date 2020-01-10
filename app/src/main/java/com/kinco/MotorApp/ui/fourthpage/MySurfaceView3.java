package com.kinco.MotorApp.ui.fourthpage;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.graphics.Rect;
import android.graphics.Bitmap;



public class MySurfaceView3 extends SurfaceView implements
        SurfaceHolder.Callback, View.OnTouchListener {

    private static final int NONE = 0;// 原始
    private static final int DRAG = 1;// 拖动
    private static final int ZOOM = 2;// 放大
    private int mStatus = NONE;

    private static final float MAX_ZOOM_SCALE = 4.0f;//4.0
    private static final float MIN_ZOOM_SCALE = 1.0f;//1.0
    private static final float FLOAT_TYPE = 1.0f;
    private float mCurrentMaxScale = MAX_ZOOM_SCALE;
    private float mCurrentScale = 1.0f;

    private Rect mRectSrc = new Rect(); // used for render(提交的) image.
    private Rect mRectDes = new Rect(); // used for store size of monitor.

    private int mCenterX, mCenterY;
    int mSurfaceHeight=0, mSurfaceWidth=0;  //SurfaceView宽高
    int mImageHeight, mImageWidth;      //放入图片的宽高

    private PointF mStartPoint = new PointF();
    private float mStartDistance = 0f;

    private SurfaceHolder mSurHolder = null;
    private Bitmap mBitmap;
    private float data[];
    private int bgCenterY = mSurfaceHeight/2;
    private int bgCenterX = mSurfaceWidth/2;
    private int count=1;

    public MySurfaceView3(Context context, AttributeSet attrs) {
        super(context,attrs);
        mSurHolder = getHolder();
        mSurHolder.addCallback(this);
        this.setOnTouchListener(this);

    }

    private void init() {
        mCurrentMaxScale = Math.max(
                MIN_ZOOM_SCALE,
                4 * Math.min(FLOAT_TYPE * mImageHeight / mSurfaceHeight, 1.0f
                        * mImageWidth / mSurfaceWidth));      //能缩放的尺寸取决于图片/surfaceView的倍数
        mCurrentScale = MIN_ZOOM_SCALE;
        //setMaxZoom(2);
        mCenterX = mImageWidth / 2;
        mCenterY = mImageHeight / 2;
        calcRect();

    }

    private void adjustCenter() {
        int w = mRectSrc.right - mRectSrc.left;
        int h = mRectSrc.bottom - mRectSrc.top;

        if (mCenterX - w / 2 < 0) {
            mCenterX = w / 2;
            mRectSrc.left = 0;
            mRectSrc.right = w;
        } else if (mCenterX + w / 2 >= mImageWidth) {
            mCenterX = mImageWidth - w / 2;
            mRectSrc.right = mImageWidth;
            mRectSrc.left = mRectSrc.right - w;

        } else {
            mRectSrc.left = mCenterX - w / 2;
            mRectSrc.right = mRectSrc.left + w;
        }

        if (mCenterY - h / 2 < 0) {
            mCenterY = h / 2;
            mRectSrc.top = 0;
            mRectSrc.bottom = h;
        } else if (mCenterY + h / 2 >= mImageHeight) {
            mCenterY = mImageHeight - h / 2;
            mRectSrc.bottom = mImageHeight;
            mRectSrc.top = mRectSrc.bottom - h;
        } else {
            mRectSrc.top = mCenterY - h / 2;
            mRectSrc.bottom = mRectSrc.top + h;
        }

    }

    private void calcRect() {
        int w, h;
        float imageRatio, surfaceRatio;
        imageRatio = FLOAT_TYPE * mImageWidth / mImageHeight;
        surfaceRatio = FLOAT_TYPE * mSurfaceWidth / mSurfaceHeight;

        if (imageRatio < surfaceRatio) {
            h = mSurfaceHeight;
            w = (int) (h * imageRatio);
        } else {
            w = mSurfaceWidth;
            h = (int) (w / imageRatio);
        }

        if (mCurrentScale > MIN_ZOOM_SCALE) {
            w = Math.min(mSurfaceWidth, (int) (w * mCurrentScale));
            h = Math.min(mSurfaceHeight, (int) (h * mCurrentScale));
        } else {
            mCurrentScale = MIN_ZOOM_SCALE;
        }

        mRectDes.left = (mSurfaceWidth - w) / 2;
        mRectDes.top = (mSurfaceHeight - h) / 2;
        mRectDes.right = mRectDes.left + w;
        mRectDes.bottom = mRectDes.top + h;

        float curImageRatio = FLOAT_TYPE * w / h;
        int h2, w2;
        if (curImageRatio > imageRatio) {
            h2 = (int) (mImageHeight / mCurrentScale);
            w2 = (int) (h2 * curImageRatio);
        } else {

            w2 = (int) (mImageWidth / mCurrentScale);
            h2 = (int) (w2 / curImageRatio);
        }
        mRectSrc.left = mCenterX - w2 / 2;
        mRectSrc.top = mCenterY - h2 / 2;
        mRectSrc.right = mRectSrc.left + w2;
        mRectSrc.bottom = mRectSrc.top + h2;
    }

    public void setMaxZoom(float value) {
        mCurrentMaxScale = value;
    }

    public void setBitmap(Bitmap b) {
        if (b == null) {
            return;
        }
        synchronized (MySurfaceView3.class) {
            mBitmap = b;
            if (mImageHeight != mBitmap.getHeight()
                    || mImageWidth != mBitmap.getWidth()) {
                mImageHeight = mBitmap.getHeight();
                mImageWidth = mBitmap.getWidth();
                init();
            }
            showBitmap();
        }

    }

    private void showBitmap() {
        synchronized (MySurfaceView3.class) {
            Bitmap bgBitmap = Bitmap.createBitmap(mSurfaceWidth,mSurfaceHeight, Bitmap.Config.ARGB_4444);
            Canvas fc = new Canvas(bgBitmap);
            Paint p = new Paint();
            p.setColor(Color.WHITE);
            p.setTextSize(50);
            drawBackGround(fc,1);
            //fc.drawText(mCenterX+"",mSurfaceWidth/2,mSurfaceHeight/2,p);

            Canvas c = getHolder().lockCanvas();
            if (c != null && mBitmap != null) {
                c.drawColor(Color.GRAY);
                c.drawBitmap(bgBitmap,0, 0,null);
                c.drawBitmap(mBitmap, mRectSrc, mRectDes, null);
                getHolder().unlockCanvasAndPost(c);
            }
        }
    }

    private void dragAction(MotionEvent event) {

        synchronized (MySurfaceView3.class) {
            PointF currentPoint = new PointF();
            currentPoint.set(event.getX(), event.getY());
            int offsetX = (int) currentPoint.x - (int) mStartPoint.x;
            int offsetY = (int) currentPoint.y - (int) mStartPoint.y;
            mStartPoint = currentPoint;

            mCenterX -= offsetX;
            mCenterY -= offsetY;

            adjustCenter();
            showBitmap();
        }
    }

    private void zoomAcition(MotionEvent event) {

        synchronized (MySurfaceView3.class) {

            float newDist = spacing(event);
            final float scale = newDist / mStartDistance;
            mStartDistance = newDist;

            mCurrentScale *= scale;
            mCurrentScale = Math.max(FLOAT_TYPE,
                    Math.min(mCurrentScale, mCurrentMaxScale));//限定上下界范围
//            Log.d("MySV3",scale+"");
//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    int finalScale = Math.max(1,(int)(scale));
//                    testRandomDraw(finalScale);
//                    //原操作
//
//                }
//            }).start();
            calcRect();
            adjustCenter();
            showBitmap();

        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                mStartPoint.set(event.getX(), event.getY());
                mStatus = DRAG;
//                if(count>5)
//                    count=1;
//                else {
//                    count++;
//                    testRandomDraw(count);
//                }

                break;

            case MotionEvent.ACTION_POINTER_DOWN:
                float distance = spacing(event);
                if (distance > 10f) {
                    mStatus = ZOOM;
                    mStartDistance = distance;//获取两手指初始距离
                }

                break;

            case MotionEvent.ACTION_MOVE:
                if (mStatus == DRAG) {
                    dragAction(event);
                } else {

                    if (event.getPointerCount() == 1)
                        return true;
                    zoomAcition(event);
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                mStatus = NONE;
                break;
            default:
                break;
        }

        return true;
    }

    private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        // TODO Auto-generated method stub
    }

    // 初始化
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {

        synchronized (MySurfaceView3.class) {
            mRectDes.set(0, 0, width, height);
            mSurfaceHeight = height;    //获取真实的高宽
            mSurfaceWidth = width;
            init();
            if (mBitmap != null) {
                showBitmap();
            }
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

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
        for (int i = 0; i <= 10; i++) {// 绘画横线
            canvas.drawLine(0, oldY, mSurfaceWidth*scale, oldY, mPaint);
            canvas.drawText(0+"",0, oldY,p);
            oldY += mSurfaceHeight/10;
        }
        int oldX = 0;
        for (int i = 0; i <= 8; i++) {// 绘画纵线
            canvas.drawLine(oldX, 0, oldX, mSurfaceHeight*scale, mPaint);
            if(i%2==0)
                canvas.drawText(oldX+"",oldX+10,mSurfaceHeight/2+40,mPaint);
            oldX = oldX + mSurfaceWidth/8;

        }
        // 绘制坐标轴
        canvas.drawLine(2, mSurfaceHeight/2, mSurfaceWidth*scale, mSurfaceHeight/2, p);//横轴
        canvas.drawLine(3, 0, 3, mSurfaceHeight*scale, p);//竖轴
    }

    /**
     * 生成并设置bitmap
     */
    void createBitmap(int scale){
        //Log.d("MySV3","宽高为"+mImageWidth+","+mImageHeight);
        Bitmap fgBitmap = Bitmap.createBitmap(mSurfaceWidth*scale,mSurfaceHeight*scale/4, Bitmap.Config.ARGB_4444);
        Canvas canvas = new Canvas(fgBitmap);
        Paint mpaint = new Paint();
        mpaint.setColor(Color.GREEN);
        mpaint.setStrokeWidth(3*(float)Math.log(scale));
        float centerY = mSurfaceHeight*scale/8;   //局部变量，只存在这张图里
        float oldY = centerY;
        float oldX = 0;
        float cx = 0;
        for(int i=0;i<1024;i++) {
            cx += scale;
            float cy = centerY-data[i]*scale/4;
            canvas.drawLine(oldX,oldY,cx,cy,mpaint);
            oldX = cx;
            oldY = cy;
        }
        setBitmap(fgBitmap);
    }

    public void drawWave(float data[]){
        this.data=data;
        createBitmap(5);
    }

}