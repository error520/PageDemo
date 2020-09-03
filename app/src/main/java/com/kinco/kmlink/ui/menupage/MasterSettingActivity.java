package com.kinco.kmlink.ui.menupage;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.kinco.kmlink.R;
import com.kinco.kmlink.sys.SecondaryActivity;
import com.kinco.kmlink.utils.PrefUtil;
import com.wuhenzhizao.titlebar.widget.CommonTitleBar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class MasterSettingActivity extends SecondaryActivity {
    private TextView[] tvValue = new TextView[3];
    private String[] defaultValue = {"50", "1000", "2000"};
    private int[] titleId = {R.string.ble_message_gap, R.string.read_gap,R.string.timeout_waiting};


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_master_setting);
        CommonTitleBar titleBar = findViewById(R.id.toolBar);
        titleBar.setListener((v, action, extra) -> {
            if (action == CommonTitleBar.ACTION_LEFT_BUTTON) {
                finish();
            }
        });
        TextView tvValue0 = findViewById(R.id.tv_value0);
        tvValue0.setText(PrefUtil.bleGap + " ms");
        tvValue[0] = tvValue0;
        TextView tvValue1 = findViewById(R.id.tv_value1);
        tvValue1.setText(PrefUtil.readGap + " ms");
        tvValue[1] = tvValue1;
        TextView tvValue2 = findViewById(R.id.tv_value2);
        tvValue2.setText(PrefUtil.timeoutWaiting + " ms");
        tvValue[2] = tvValue2;
        Switch showNum = findViewById(R.id.switch_showNum);
        showNum.setChecked(PrefUtil.getShowNum(this));
        showNum.setOnCheckedChangeListener((v, isChecked) -> {
            PrefUtil.setShowNum(this, isChecked);
        });
        View itemBleGap = findViewById(R.id.item_ble_gap);
        itemBleGap.setOnClickListener(v -> {
            showSettingDialog(0);
        });
        View itemReadGap = findViewById(R.id.item_read_gap);
        itemReadGap.setOnClickListener(v -> {
            showSettingDialog(1);
        });
        View itemTimout = findViewById(R.id.item_timeout_waiting);
        itemTimout.setOnClickListener(v->{
            showSettingDialog(2);
        });
        View itemShowNum = findViewById(R.id.item_show_num);
        itemShowNum.setOnClickListener(v -> {
            showNum.setChecked(!showNum.isChecked());
        });
    }


    @SuppressLint("SetTextI18n")
    private void showSettingDialog(int index) {
        EditText editText = new EditText(this);
        editText.setFocusable(true);
        editText.setFocusableInTouchMode(true);
        editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(6)});
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(50,0,50,0);
        editText.setLayoutParams(params);
        FrameLayout layout = new FrameLayout(this);
        layout.addView(editText);
        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        editText.setWidth(LinearLayout.LayoutParams.MATCH_PARENT);
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setView(layout)
                .setPositiveButton(getString(R.string.OK), null)
                .setNegativeButton(getString(R.string.default_value), null);
        builder.setTitle(getString(titleId[index]) + "(ms)");
        editText.setText(tvValue[index].getText().toString().split(" ")[0]);
        AlertDialog dialog = builder.show();
        Button btnPositive = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        btnPositive.setOnClickListener(v -> {
            String value = editText.getText().toString();
            PrefUtil.setPreferences(this, index, value);
            tvValue[index].setText(value + " ms");
            dialog.cancel();
        });
        btnPositive.setTextColor(getColor(R.color.colorAccent));
        Button btnNegative = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
        btnNegative.setOnClickListener(v -> {
            editText.setText(defaultValue[index]);
            editText.setSelection(defaultValue[index].length());
        });
        btnNegative.setTextColor(getColor(R.color.colorAccent));
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        editText.requestFocus();
        dialog.setOnDismissListener(dialog1 -> {
//            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//            imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
        });
//        WindowManager manager = getWindowManager();
//        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
//        manager.addView(layout,layoutParams);
//        InputMethodManager imm = (InputMethodManager) editText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
//        imm.showSoftInput(editText, 0);
    }

}
