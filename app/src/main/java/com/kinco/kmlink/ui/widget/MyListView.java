package com.kinco.kmlink.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.ListView;

import com.kinco.kmlink.R;

public class MyListView extends ListView {
    float maxHeight = 500;

    public MyListView(Context context) {
        super(context);
    }

    public MyListView(Context context, AttributeSet attrs){
        super(context, attrs, 0);
    }

    public MyListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.ConstraintHeightListView, 0, defStyleAttr);
        int count = array.getIndexCount();
        for (int i = 0; i < count; i++) {
            int type = array.getIndex(i);
            if (type == R.styleable.ConstraintHeightListView_maxHeight) {
                //获得布局中限制的最大高度
                maxHeight = array.getDimension(type, -1);
            }
        }
        array.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //获取lv本身高度
        int specSize = MeasureSpec.getSize(heightMeasureSpec);
        //限制高度小于lv高度,设置为限制高度
        if (maxHeight <= specSize && maxHeight > -1) {
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(Float.valueOf(maxHeight).intValue(),
                    MeasureSpec.AT_MOST);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
