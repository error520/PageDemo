package com.kinco.MotorApp.ui.fourthpage;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.PathEffect;
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
    private float fgCenterY = 0;
    private float fgCenterX = 0;
    private int count=1;
    private boolean change=false;
    private float oldScale=1;
    private float average=0;
    private float step;
    private String TAG = "MySV3";

    public MySurfaceView3(Context context, AttributeSet attrs) {
        super(context,attrs);
        mSurHolder = getHolder();
        mSurHolder.addCallback(this);
        this.setOnTouchListener(this);
    }

    /**
     * 计算新图片中心,把缩放归回1
     */
    private void init() {
        mCurrentMaxScale = Math.max(
                MIN_ZOOM_SCALE,
                4 * Math.min(FLOAT_TYPE * mImageHeight / mSurfaceHeight, 1.0f
                        * mImageWidth / mSurfaceWidth));      //能缩放的尺寸取决于图片/surfaceView的倍数
        if(!change) {           //不是因为缩放而更换图片
            mCurrentScale = MIN_ZOOM_SCALE;
            mRectSrc.set(0,0,mImageWidth,mImageHeight);
            //mRectDes.set(0,0,mSurfaceWidth,mSurfaceHeight);
            mCenterX = mImageWidth / 2;
            mCenterY = mImageHeight / 2;
            Log.d("MySV3","初始："+mCenterX);
        }
        change=false;
        setMaxZoom(20);

        //calcRect();
    }

    private void adjustCenter() {
        int w = mRectSrc.right - mRectSrc.left;
        int h = mRectSrc.bottom - mRectSrc.top;

        if (mCenterX - w / 2 < 0) { //拖到了左边界
            mCenterX = w / 2;
            mRectSrc.left = 0;
            mRectSrc.right = w;
        } else if (mCenterX + w / 2 >= mImageWidth) {   //拖到了右边界
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

        mRectDes.left = (mSurfaceWidth - w) / 2;    //为计算照片规格不符合surfaceview的
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
                    || mImageWidth != mBitmap.getWidth()&&!change) {
                mImageHeight = mBitmap.getHeight();
                mImageWidth = mBitmap.getWidth();
                init();
            }
            mImageHeight = mBitmap.getHeight();
            mImageWidth = mBitmap.getWidth();
            //showBitmap();
        }

    }

    private void showBitmap(float offset) {
        synchronized (MySurfaceView3.class) {
            //先画背景
            Bitmap bgBitmap = Bitmap.createBitmap(mSurfaceWidth,mSurfaceHeight, Bitmap.Config.ARGB_4444);
            Canvas fc = new Canvas(bgBitmap);
            Paint p = new Paint();
            p.setColor(Color.WHITE);
            p.setTextSize(50);
            drawBackGround(fc,offset);
            fc.drawText(mCurrentScale+"",mSurfaceWidth/2,mSurfaceHeight/2,p);  //缩放调试信息

            Canvas c = getHolder().lockCanvas();
            if (c != null && mBitmap != null) {
                //c.drawColor(Color.GRAY);
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

            mCenterX -= offsetX*((int)mCurrentScale/2+1);       //调节拖动速度
            mCenterY -= offsetY*((int)mCurrentScale/2+1);

            adjustCenter();
            showBitmap(offsetY);
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
            if(mCurrentScale>(int)oldScale+1) {     //放大换图
                change=true;
                createBitmap((int) mCurrentScale);
                changeAction((int)mCurrentScale,(int)oldScale);
            }else if(mCurrentScale<(int)oldScale){      //缩小换图
                change=true;
                createBitmap((int) mCurrentScale);
                changeAction((int)mCurrentScale,(int)oldScale);
            }else{
                calcRect(); //计算了显示照片哪部分的rect
            }
            oldScale=mCurrentScale;
            adjustCenter(); //计算显示区域的中心

            showBitmap(0);

        }
    }

    private void changeAction(float scale,float oldScale){
        mCenterX*=scale/oldScale;
        mCenterY*=scale/oldScale;
        mRectSrc.left*=scale/oldScale;
        mRectSrc.top*=scale/oldScale;
        mRectSrc.right*=scale/oldScale;
        mRectSrc.bottom*=scale/oldScale;
        Log.d("MySV3",mCenterX+"");

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if(mBitmap==null)
            return true;
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                mStartPoint.set(event.getX(), event.getY());
                mStatus = DRAG;
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
        Canvas c = getHolder().lockCanvas();
        drawBackGround(c,0);
        getHolder().unlockCanvasAndPost(c);
    }

    // 初始化,每次重回app会调用
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {

        synchronized (MySurfaceView3.class) {
            mRectDes.set(0, 0, width, height);
            mSurfaceHeight = height;    //获取真实的高宽
            mSurfaceWidth = width;
            //init();
            if (mBitmap != null) {
                showBitmap(0);
                Log.d("MySV3","surfaceChange被调用了");
            }
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    private void drawBackGround(Canvas canvas,float offset) {
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
        mPaint.setPathEffect ( new DashPathEffect( new float []{ 5,5,5,5}, 0 ) ) ;
        int oldY = 0;
        for (int i = 0; i <= 10; i++) {// 绘画横线
            canvas.drawLine(0, oldY, mSurfaceWidth, oldY, mPaint);
            if(i!=0&&i!=10) {
                float measure = average-(mCenterY-fgCenterY)/mCurrentScale + step / mCurrentScale * (5 - i);
                canvas.drawText(String.format("%.2f",measure), 0, oldY, p);
            }
            oldY += mSurfaceHeight/10;
        }
        int oldX = 0;
        for (int i = 0; i <= 8; i++) {// 绘画纵线
            canvas.drawLine(oldX, 0, oldX, mSurfaceHeight, mPaint);
            if(i%2==0)
                canvas.drawText(oldX+"",oldX+10,mSurfaceHeight/2+40,mPaint);
            oldX = oldX + mSurfaceWidth/8;

        }
        // 绘制坐标轴
        canvas.drawLine(2, mSurfaceHeight/2, mSurfaceWidth, mSurfaceHeight/2, p);//横轴
        canvas.drawLine(3, 0, 3, mSurfaceHeight, p);//竖轴
    }

    /**
     * 生成并设置bitmap
     */
    private void createBitmap(int scale){
        Bitmap fgBitmap = Bitmap.createBitmap(mSurfaceWidth*scale,mSurfaceHeight*scale, Bitmap.Config.ARGB_4444);
        Canvas canvas = new Canvas(fgBitmap);
        Paint mpaint = new Paint();
        mpaint.setColor(Color.GREEN);
        mpaint.setStrokeWidth(3.5f*(float)Math.log(scale/2*4+Math.E));
        float centerY = mSurfaceHeight*scale/2;   //局部变量，只存在这张图里
        float oldY = centerY;
        float oldX = 0;
        float cx = 0;
        for(int i=0;i<1024;i++) {
            cx += scale;
            float cy = centerY-data[i]*scale;
            canvas.drawLine(oldX,oldY,cx,cy,mpaint);
            oldX = cx;
            oldY = cy;
        }
        mBitmap = fgBitmap;
        mImageHeight = fgBitmap.getHeight();
        mImageWidth = fgBitmap.getWidth();
        fgCenterY = mImageHeight/2;
        //setBitmap(fgBitmap);
    }

    /**
     * 对外接口
     * @param data
     */
    public void drawWave(float data[]){
        this.data = data;
        float max = data[0],min = data[0];
        for(float i:data){
            max = Math.max(max,i);
            min = Math.min(min,i);
        }
        this.average = (max+min)/2;
        this.step = (max-min)/8;
        Log.d(TAG,"max is "+max+",min is "+min);
        createBitmap(1);
        init();
        showBitmap(0);
    }

}