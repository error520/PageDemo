package com.kinco.kmlink.ui.widget;

import android.animation.Animator;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.kinco.kmlink.ParameterItem.ParameterBean;
import com.kinco.kmlink.R;
import com.kinco.kmlink.ui.firstpage.SettingPagerAdapter;
import com.kinco.kmlink.utils.util;

import java.util.List;

import razerdp.basepopup.BaseLazyPopupWindow;
import razerdp.basepopup.BasePopupWindow;

/**
 * 添加和移除item的弹起菜单
 */
public class CustomItemPopup extends BaseLazyPopupWindow {
    OnClickListener onClickListener;
    int layoutId;
    Boolean flag = true;

    public CustomItemPopup(Context context,int id) {
        super(context);
        this.layoutId = id;
        setBackgroundColor(Color.TRANSPARENT);
    }

    @Override
    public View onCreateContentView() {
        View layout = createPopupById(layoutId);   //适配好宽度了
        TextView button;
        if(layoutId==R.layout.popup_add_to_general){
            button = layout.findViewById(R.id.tv_add);
        }else{
            button = layout.findViewById(R.id.tv_remove);
        }
        button.setActivated(flag);
        button.setOnClickListener(v->{
            if(onClickListener!=null&&flag){
                onClickListener.onClick();
            }
        });
        setPopupGravity(Gravity.TOP);
        return layout;
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public void enable(Boolean flag){
        this.flag = flag;
    }

    @Override
    protected Animation onCreateShowAnimation() {
        Animation animation = AnimationUtils.loadAnimation(getContext(),R.anim.pop_up);
        return animation;
    }

    @Override
    protected Animation onCreateDismissAnimation() {
        Animation animation = AnimationUtils.loadAnimation(getContext(),R.anim.pop_down);
        return animation;
    }

    public interface OnClickListener{
        void onClick();
    }
}
