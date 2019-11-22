package com.kinco.MotorApp;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.kinco.MotorApp.R;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class OSCActivity extends AppCompatActivity implements View.OnClickListener {
    private SurfaceHolder holder;
    private SurfaceView showSurfaceView;

    private Button btnShowSin;
    private Button btnShowCos;
    private Button btnShowBrokenLine;

    private Paint paint;

    private int HEIGHT;
    // 要绘制的曲线的水平宽度
    private int WIDTH;
    // 离屏幕左边界的起始距离
    private final int X_OFFSET = 5;
    // 初始化X坐标
    private int cx = X_OFFSET;
    // 实际的Y轴的位置
    private int centerY ;
    private Timer timer = new Timer();
    private TimerTask task = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.osc_page);
        // 获得SurfaceView对象
        showSurfaceView = (SurfaceView) findViewById(R.id.showSurfaceView);
        btnShowSin = (Button) findViewById(R.id.btnShowSin);
        btnShowCos = (Button) findViewById(R.id.btnShowCos);
        btnShowBrokenLine = (Button) findViewById(R.id.btnShowBrokenLine);

        btnShowSin.setOnClickListener(this);
        btnShowCos.setOnClickListener(this);
        btnShowBrokenLine.setOnClickListener(this);

        InitData();

        // 初始化SurfaceHolder对象
        holder = showSurfaceView.getHolder();
        paint = new Paint();
        paint.setColor(Color.GREEN);
        paint.setStrokeWidth(3);
    }

    private void InitData() {
        Resources resources = this.getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        //获取屏幕的宽度作为示波器的边长
        HEIGHT = dm.widthPixels;
        WIDTH = dm.widthPixels;
        //Y轴的中心就是高的一半
        centerY = HEIGHT / 2;

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnShowSin:
                showSineCord(view);
                break;
            case R.id.btnShowCos:
                showSineCord(view);
                break;
            case R.id.btnShowBrokenLine:
                showBrokenLine();
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

    /**
     * 正余弦曲线函数
     */
    private void showSineCord(final View view){
        drawBackGround(holder);
        cx = X_OFFSET;
        if (task != null) {
            task.cancel();
        }
        task = new TimerTask() {

            @Override
            public void run() {
                // 根据是正玄还是余玄和X坐标确定Y坐标
                int cy = view.getId()==R.id.btnShowSin?
                        centerY- (int) (100 * Math.sin((cx - 5) * 2 * Math.PI/ 150))
                        :centerY- (int) (100 * Math.cos((cx - 5) * 2 * Math.PI/ 150));

                Canvas canvas = holder.lockCanvas(new Rect(cx, cy - 2,
                        cx + 2, cy + 2));
                // 根据Ｘ，Ｙ坐标画点
                canvas.drawPoint(cx, cy, paint);
                cx++;
                // 超过指定宽度，线程取消，停止画曲线
                if (cx > WIDTH) {
                    task.cancel();
                    task = null;
                }
                // 提交修改
                holder.unlockCanvasAndPost(canvas);
            }
        };
        timer.schedule(task, 0, 30);
    }

    private void drawBackGround(SurfaceHolder holder) {
        Canvas canvas = holder.lockCanvas();
        // 绘制黑色背景
        canvas.drawColor(Color.BLACK);
        Paint p = new Paint();
        p.setColor(Color.WHITE);
        p.setStrokeWidth(2);

        // 画网格8*8
        Paint mPaint = new Paint();
        mPaint.setColor(Color.GRAY);// 网格为黄色
        mPaint.setStrokeWidth(1);// 设置画笔粗细
        int oldY = 0;
        for (int i = 0; i <= 8; i++) {// 绘画横线
            canvas.drawLine(0, oldY, WIDTH, oldY, mPaint);
            oldY = oldY + WIDTH/8;
        }
        int oldX = 0;
        for (int i = 0; i <= 8; i++) {// 绘画纵线
            canvas.drawLine(oldX, 0, oldX, HEIGHT, mPaint);
            oldX = oldX + HEIGHT/8;
        }

        // 绘制坐标轴
        canvas.drawLine(X_OFFSET, centerY, WIDTH, centerY, p);
        canvas.drawLine(X_OFFSET, 40, X_OFFSET, HEIGHT, p);
        holder.unlockCanvasAndPost(canvas);
        holder.lockCanvas(new Rect(0, 0, 0, 0));
        holder.unlockCanvasAndPost(canvas);
    }

}

