package com.kinco.kmlink.ui.settingpage.parameterpage;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.kinco.kmlink.EventBusUtils.BleDataEvent;
import com.kinco.kmlink.EventBusUtils.RequestEvent;
import com.kinco.kmlink.ParameterItem.ParameterBean;
import com.kinco.kmlink.R;
import com.kinco.kmlink.utils.PrefUtil;
import com.kinco.kmlink.utils.util;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Map;

import razerdp.basepopup.BaseLazyPopupWindow;

public class WindowSetValue extends BaseLazyPopupWindow {
    ParameterBean bean;

    WindowSetValue(Context context, ParameterBean bean) {
        super(context);
        this.bean = bean;
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    @Override
    public View onCreateContentView() {
        View layout = createPopupById(R.layout.window_options);
        TextView textView = layout.findViewById(R.id.tv_name);
        textView.setText(bean.getName());
        RadioGroup radioGroup = layout.findViewById(R.id.rg_options);
        for (Map.Entry<Integer, String> entry : bean.getOptionMap().entrySet()) {
            RadioButton radioButton = new RadioButton(getContext());
            String finalText = entry.getValue();
            if (PrefUtil.showNum) {
                finalText = entry.getKey() + ": " + entry.getValue();
            }
            radioButton.setText(finalText);
            radioButton.setTextSize(16);
            radioButton.setTextColor(getContext().getColorStateList(R.color.selector_option_text_color));
            radioButton.setPadding(4, 24, 4, 24);
            radioGroup.addView(radioButton, MATCH_PARENT, WRAP_CONTENT);
            if (bean.getCurrentValue().equals(entry.getValue())) {
                radioButton.setChecked(true);
            }
        }
        //选择
        radioGroup.setOnCheckedChangeListener((RadioGroup group, int checkedId) -> {
            RadioButton button = findViewById(checkedId);
            int index = 0;
            if (PrefUtil.showNum) {
                String option = button.getText().toString().split(":")[0];
                index = Integer.parseInt(option);
            } else {
                index = bean.getIndexByOption(button.getText().toString());
            }
            String[] request = {"write", bean.getAddress(), Integer.toHexString(index)};
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNewBleDataEvent(BleDataEvent event) {
        byte[] message = event.getBleData();
        if (message.length == 8) {
            util.setParameterByMessage(message, bean);
            util.centerToast(getContext(), getContext().getString(R.string.successful), 0);
            dismiss();
        } else if (message.length == 5) {
            util.centerToast(getContext(), getContext().getString(R.string.failed), 0);
        }
    }

    @Override
    protected Animation onCreateShowAnimation() {
        TranslateAnimation animation = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0,
                Animation.RELATIVE_TO_SELF, 0,
                Animation.RELATIVE_TO_SELF, 1f,
                Animation.RELATIVE_TO_SELF, 0);
        animation.setDuration(200);
        return animation;
    }

    @Override
    protected Animation onCreateDismissAnimation() {
        TranslateAnimation animation = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0,
                Animation.RELATIVE_TO_SELF, 0,
                Animation.RELATIVE_TO_SELF, 0,
                Animation.RELATIVE_TO_SELF, 1f);
        animation.setDuration(150);
        return animation;
    }
}
