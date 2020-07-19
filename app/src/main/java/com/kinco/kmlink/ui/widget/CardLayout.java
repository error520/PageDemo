package com.kinco.kmlink.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kinco.kmlink.R;

import androidx.annotation.Nullable;

public class CardLayout extends ViewGroup {
    Context context;
    LinearLayout linearLayout;

    private String TAG = getClass().getSimpleName();
    public CardLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        View root = LayoutInflater.from(context).inflate(R.layout.costom_cardview,this);
        linearLayout = root.findViewById(R.id.inside_LinearLayout);
        this.context = context;
        Log.d(TAG,"CardLayout()");
        for(int i=0; i<getChildCount(); i++){
            TextView ttt = new TextView(context);
            ttt.setText("ddd");
            View child = getChildAt(i);
//            removeView(child);
            linearLayout.addView(ttt);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        Log.d(TAG,"onLayout()");
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        Log.d(TAG,"onMeasure()");

    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        Log.d(TAG,"onFinishInflate()");

    }
}
