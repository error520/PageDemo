package com.kinco.kmlink.ui.firstpage;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.kinco.kmlink.EventBusUtils.BleDataEvent;
import com.kinco.kmlink.EventBusUtils.RequestEvent;
import com.kinco.kmlink.ParameterItem.ParameterBean;
import com.kinco.kmlink.R;
import com.kinco.kmlink.sys.SysApplication;
import com.kinco.kmlink.utils.util;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import razerdp.basepopup.BaseLazyPopupWindow;
import razerdp.basepopup.BasePopupWindow;

public class SetValueWindow extends BaseLazyPopupWindow {
    ParameterBean bean;

    public SetValueWindow(Context context, ParameterBean bean) {
        super(context);
        this.bean = bean;
        EventBus.getDefault().register(this);
    }

    @Override
    public View onCreateContentView() {
        View layout = createPopupById(R.layout.window_options);
        TextView textView = layout.findViewById(R.id.tv_name);
        textView.setText(bean.getName());
        RadioGroup radioGroup = layout.findViewById(R.id.rg_options);
        for(String option:bean.getDescription()){
            RadioButton radioButton = new RadioButton(getContext());
            radioButton.setText(option);
            radioButton.setTextSize(16);
            radioButton.setTextColor(getContext().getColorStateList(R.color.selector_option_text_color));
//            radioButton.setTextColor(getContext().getColor(R.color.btn_red));
            radioButton.setPadding(4,24,4,24);
            radioGroup.addView(radioButton,MATCH_PARENT,WRAP_CONTENT);
            if(bean.getCurrentValue().equals(option)){
                radioButton.setChecked(true);
            }
        }
        //选择
        radioGroup.setOnCheckedChangeListener((RadioGroup group, int checkedId)->{
            RadioButton button = (RadioButton)findViewById(checkedId);
            String option = button.getText().toString().split(":")[0];
            int value = Integer.parseInt(option);
            String[] request = {bean.getAddress(),Integer.toHexString(value),"write"};
            EventBus.getDefault().post(new RequestEvent(request));
        });
        setPopupGravity(Gravity.BOTTOM);
        setMaxHeight(1000);
        return layout;
    }

    @Override
    public void onDismiss() {
        super.onDismiss();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void onNewBleData(BleDataEvent event){
        byte[] message = event.getBleData();
        if(message.length==8){
            util.setParameterByMessage(message,bean);
            dismiss();
        }
    }

    @Override
    protected Animation onCreateShowAnimation() {
        TranslateAnimation animation = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF,0,
                Animation.RELATIVE_TO_SELF,0,
                Animation.RELATIVE_TO_SELF,1f,
                Animation.RELATIVE_TO_SELF,0);
        animation.setDuration(200);
//        Animation animation = AnimationUtils.loadAnimation(getContext(),R.anim.pop_up);
        return animation;
    }

    @Override
    protected Animation onCreateDismissAnimation() {
        TranslateAnimation animation = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF,0,
                Animation.RELATIVE_TO_SELF,0,
                Animation.RELATIVE_TO_SELF,0,
                Animation.RELATIVE_TO_SELF,1f);
        animation.setDuration(150);
        return animation;
    }
}
