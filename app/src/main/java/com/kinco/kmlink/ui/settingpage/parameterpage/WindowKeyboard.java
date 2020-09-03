package com.kinco.kmlink.ui.settingpage.parameterpage;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.gridlayout.widget.GridLayout;

import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputLayout;
import com.kinco.kmlink.BluetoothService.BleService;
import com.kinco.kmlink.EventBusUtils.RequestEvent;
import com.kinco.kmlink.ParameterItem.ParameterBean;
import com.kinco.kmlink.R;
import com.kinco.kmlink.utils.util;

import org.greenrobot.eventbus.EventBus;

import java.lang.reflect.Method;

public class WindowKeyboard extends WindowSetValue implements View.OnClickListener {
    private EditText edValue;
    private Editable editable;
    private double min;
    private double max;
    private boolean isValid;
    private int digits = 0;
    private TextInputLayout tiLayout;

    public WindowKeyboard(Context context, ParameterBean bean) {
        super(context, bean);
        min = bean.getRange()[0];
        max = bean.getRange()[1];
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateContentView() {
//        View layout = createPopupById(R.layout.window_keyboard_input);
        LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.window_keyboard_input, null);
        GridLayout gridLayout = layout.findViewById(R.id.gridLayout);
        for (int i = 0; i < gridLayout.getChildCount(); i++) {
            gridLayout.getChildAt(i).setOnClickListener(this);
        }
        TextView keyDot = layout.findViewById(R.id.key_dot);
        if (bean.getAccuracy() == 1) {
            keyDot.setEnabled(false);
        } else {
            String accuracy = String.valueOf(bean.getAccuracy());
            digits = accuracy.split("\\.")[1].length();
        }
        TextView keySign = layout.findViewById(R.id.key_sign);
        if (bean.getType() == 2) {
            keySign.setEnabled(false);
        }
        String unit = bean.getUnit();
        TextView tvName = layout.findViewById(R.id.tv_name);
        tvName.setText(bean.getName());
        TextView tvRange = layout.findViewById(R.id.tv_range);
        tvRange.setText(getContext().getString(R.string.range) + ": " + bean.getRangeHint() + " " + unit);
        TextView tvInitial = layout.findViewById(R.id.tv_initial_value);
        tvInitial.setText(getContext().getString(R.string.initial) + ": " + bean.getDefaultValue() + " " + unit);
        TextView tvCurrent = layout.findViewById(R.id.tv_current_value);
        tvCurrent.setText(getContext().getString(R.string.current) + ": " + bean.getCurrentValue() + " " + unit);
        edValue = layout.findViewById(R.id.ed_value);
        disableSoftInput(edValue);
        tiLayout = layout.findViewById(R.id.ti_layout);
        tiLayout.setHint(getContext().getString(R.string.input_value_hint)+" ("+unit+")");
        edValue.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tiLayout.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.toString().length()==0){
                    edValue.clearFocus();
                    return;
                }
                //自动插入0
                if (s.toString().startsWith("-") && s.toString().indexOf(".") == 1) {
                    s.insert(1, "0");
                }
                //控制小数位数
                if (s.toString().contains(".")) {
                    String[] splitDecimal = s.toString().split("\\.");
                    if (splitDecimal.length > 1) {
                        if (splitDecimal[1].length() > digits) {
                            int start = edValue.getSelectionStart();
                            s.delete(start - 1, start);
                        }
                    }
                }
                //提示范围
                if (s.length() > 0) {
                    isValid = true;
                    if (s.toString().equals("-") || s.toString().equals(".")) {
                        return;
                    }
                    double value = Float.parseFloat(s.toString());
                    if (value < min || value > max) {
//                        edValue.setError(getContext().getString(R.string.data_out_of_range));
                        tiLayout.setError(getContext().getString(R.string.data_out_of_range));
                        isValid = false;
                    }
                }
            }
        });
        editable = edValue.getText();
        ImageButton btnBackSpace = layout.findViewById(R.id.key_backspace);
        btnBackSpace.setOnLongClickListener(v -> {
            editable.clear();
            return false;
        });
        setPopupGravity(Gravity.BOTTOM);
        return layout;
    }

    @Override
    public void onClick(View v) {
        int start = edValue.getSelectionStart();
        switch (v.getId()) {
            case R.id.key_cancel:
                dismiss();
                break;
            case R.id.key_set:
                sendRequest(editable.toString());
                break;
            case R.id.key_sign:
                edValue.requestFocus();
                if (editable.toString().contains("-")) {
                    editable.delete(0, 1);
                } else {
                    editable.insert(0, "-");
                }
                break;
            case R.id.key0:
                edValue.requestFocus();
                if (!editable.toString().equals("0") && !editable.toString().equals("-0")) {
                    editable.insert(start, "0");
                }
                break;
            case R.id.key_dot:
                edValue.requestFocus();
                if (!editable.toString().contains(".")) {
                    editable.insert(start, ".");
                }
                break;
            case R.id.key_backspace:
                if (editable != null && editable.length() > 0) {
                    editable.delete(start - 1, start);
                }
                break;
            default:
                edValue.requestFocus();
                editable.insert(start, ((TextView) v).getText());
        }
    }

    private void sendRequest(String text) {
        if (text.length() == 0) {
//            edValue.setError(getContext().getString(R.string.cannot_be_blank));
            tiLayout.setError(getContext().getString(R.string.cannot_be_blank));
            return;
        }
        if (!isValid) {
            return;
        }
        if (!BleService.isConnected.getValue()) {
            util.centerToast(getContext(), getContext().getString(R.string.device_disconnected), 0);
            return;
        }
        try {
            String data = util.inputToRequest(bean, text);
            String[] request = new String[]{"write", bean.getAddress(), data};
            EventBus.getDefault().post(new RequestEvent(request));
        } catch (NumberFormatException e) {
            tiLayout.setError(getContext().getString(R.string.wrong_format));
        }
    }

    private void disableSoftInput(EditText editText){
        Class<EditText> cls = EditText.class;
        Method method;
        try{
            method = cls.getMethod("setShowSoftInputOnFocus", boolean.class);
            method.setAccessible(true);
            method.invoke(editText,false);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

}
