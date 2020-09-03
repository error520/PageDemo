package com.kinco.kmlink.ui.menupage;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.InputType;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.method.ReplacementTransformationMethod;
import android.text.style.ReplacementSpan;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.kinco.kmlink.BluetoothService.BleService;
import com.kinco.kmlink.EventBusUtils.BleDataEvent;
import com.kinco.kmlink.EventBusUtils.RequestEvent;
import com.kinco.kmlink.R;
import com.kinco.kmlink.sys.SecondaryActivity;
import com.kinco.kmlink.utils.util;
import com.wuhenzhizao.titlebar.utils.KeyboardConflictCompat;
import com.wuhenzhizao.titlebar.widget.CommonTitleBar;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.sql.Time;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class SerialActivity extends SecondaryActivity {
    EditText editText;
    static Boolean modbusMode = true;
    List<String> logList;
    Handler handler = new Handler();
    private SerialLogAdapter adapter;
    private RecyclerView recyclerView;
    private TextInputLayout tiLayout;
    private int maxByteLength = 6;
    SeparatorSpan[] spans = new SeparatorSpan[20];

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_serial);
        EventBus.getDefault().register(this);
        logList = BleService.BLELog;
        recyclerView = findViewById(R.id.rv_chat_log);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        LinearLayoutManager manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);
        adapter = new SerialLogAdapter(this, logList);
        recyclerView.setAdapter(adapter);
        recyclerView.scrollToPosition(logList.size() - 1);
        tiLayout = findViewById(R.id.editLayout);
        editText = findViewById(R.id.editText);
        MaterialButtonToggleGroup toggleGroup = findViewById(R.id.toggle_group);
        toggleGroup.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            modbusMode = checkedId == R.id.toggle_modbus;
            int textLength = editText.getText().toString().length();
            int byteLength = Math.round((float)textLength/2);
            if(modbusMode){
                maxByteLength = 6;
            }else{
                maxByteLength = 20;
            }
            if(byteLength>maxByteLength){
                Editable editable = editText.getEditableText();
                editable.delete(maxByteLength*2,textLength);
                tiLayout.setHelperText(maxByteLength+"/"+maxByteLength);
            }else {
                tiLayout.setHelperText(byteLength+"/"+maxByteLength);
            }
        });
        if (modbusMode) {
            toggleGroup.check(R.id.toggle_modbus);
            maxByteLength = 6;
        } else {
            toggleGroup.check(R.id.toggle_free);
            maxByteLength = 20;
        }
        for(int i=0; i<20; i++){
            spans[i] = new SeparatorSpan();
        }
        editText.setTransformationMethod(new ToUpperCaseMethod());  //小写字母转大写
        editText.addTextChangedListener(new addSpanWatcher());        //自动加空格
        editText.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                handler.postDelayed(() -> {
                    recyclerView.scrollToPosition(logList.size() - 1);
                }, 400);
            }
        });
        editText.setOnClickListener(v -> {
            handler.postDelayed(() -> {
                recyclerView.scrollToPosition(logList.size() - 1);
            }, 400);
        });
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        recyclerView.setOnTouchListener((v, event) -> {
            if(event.getAction()==MotionEvent.ACTION_UP){
//                editText.clearFocus();
                imm.hideSoftInputFromWindow(editText.getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
            }
//            recyclerView.performClick();
            return false;
        });
        CommonTitleBar titleBar = findViewById(R.id.toolBar);
        titleBar.setListener((v, action, extra) -> {
            if (action == CommonTitleBar.ACTION_LEFT_BUTTON) {
                finish();
            } else if (action == CommonTitleBar.ACTION_RIGHT_TEXT) {
                logList.clear();
                adapter.notifyDataSetChanged();
            }
        });
        Button btnSend = findViewById(R.id.btnSend);
        btnSend.setOnClickListener(v -> {
            if (editText.getText().length() == 0) {
                return;
            }
            boolean addCrc = toggleGroup.getCheckedButtonId() == R.id.toggle_modbus;
            if (BleService.isConnected.getValue()) {
                String[] request = {"send bytes", editText.getText().toString().toUpperCase(),
                        Boolean.toString(addCrc)};
                EventBus.getDefault().post(new RequestEvent(request));
                refreshLog();
            } else {
                util.centerToast(this, getString(R.string.device_disconnected), 0);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNewBleData(BleDataEvent event) {
        refreshLog();
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        KeyboardConflictCompat.assistWindow(getWindow());
    }

    private class ToUpperCaseMethod extends ReplacementTransformationMethod {
        @Override
        protected char[] getOriginal() {
            return new char[]{'a', 'b', 'c', 'd', 'e',
                    'f', 'g', 'h', 'i', 'j', 'k', 'l',
                    'm', 'n', 'o', 'p', 'q', 'r', 's',
                    't', 'u', 'v', 'w', 'x', 'y', 'z'};
        }

        @Override
        protected char[] getReplacement() {
            return new char[]{'A', 'B', 'C', 'D', 'E',
                    'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
                    'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};
        }
    }

    private class addSpanWatcher implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            int byteLength = Math.round((float)s.length()/2);
            if(byteLength>maxByteLength){
                return;
            }else{
                tiLayout.setHelperText(byteLength+"/"+maxByteLength);
            }
            if (!(s instanceof SpannableStringBuilder)) {
                return;
            }
            SpannableStringBuilder ssb = (SpannableStringBuilder) s;
            for(int i=1; i<ssb.length(); i++){
                if(i%2==0){
                    ssb.setSpan(spans[i/2-1], i, i+1, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                }
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
            int byteLength = Math.round((float)s.length()/2);
            if(byteLength>maxByteLength){
                s.delete(s.length()-1,s.length());
            }
        }
    }

    private void refreshLog() {
        if (adapter != null && recyclerView!=null) {
            adapter.notifyDataSetChanged();
            recyclerView.scrollToPosition(logList.size() - 1);
        }
    }

    private static class SeparatorSpan extends ReplacementSpan{
        private float fontSpacing = 1.0f;
        private String separator = " ";
        private float separatorWidth;
        @Override
        public int getSize(@NonNull Paint paint, CharSequence text, int start, int end, @Nullable Paint.FontMetricsInt fm) {
            float textWidth = paint.measureText(text,start,end);
            separatorWidth = paint.measureText(separator,0,separator.length());
            return (int) (textWidth + fontSpacing + separatorWidth + fontSpacing + 0.5f);
        }

        @Override
        public void draw(@NonNull Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, @NonNull Paint paint) {
            final String realText = text.subSequence(start, end).toString();
//            Log.d("july18","real是"+realText);
            final float separatorX = fontSpacing + x;
            final float realTextX = fontSpacing + separatorWidth + fontSpacing + x;
            canvas.drawText(separator, separatorX, y, paint);
            canvas.drawText(realText, realTextX, y, paint);
        }
    }
}
