package com.kinco.MotorApp.ui.functionpage;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.kinco.MotorApp.BluetoothService.BLEService;
import com.kinco.MotorApp.R;
import com.kinco.MotorApp.utils.util;

import androidx.appcompat.app.AppCompatActivity;

public class LogActivity extends AppCompatActivity {
    ListView listView;
    ArrayAdapter adapter;
    TextInputEditText editText;
    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.log_page);
        final TextInputLayout etLayout = findViewById(R.id.etLayout);
        editText = findViewById(R.id.etView);
        editText.setText(BLEService.reloadGap+"");
        listView = findViewById(R.id.log_listview);
        adapter = new ArrayAdapter(this,R.layout.list_item,BLEService.BLELog);
        listView.setAdapter(adapter);
        Button btnClear = findViewById(R.id.btnClear);
        btnClear.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                BLEService.BLELog.clear();
                adapter.notifyDataSetChanged();
            }
        });
        Button btnOK = findViewById(R.id.btnOK);
        btnOK.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v) {
                String setData = editText.getText().toString();
                if(setData.equals(""))
                    editText.setError("不能为空!");
                else{
                    BLEService.reloadGap = Integer.valueOf(setData);
                    editText.clearFocus();
                    util.centerToast(LogActivity.this,"OK!",0);
                }

            }
        });
    }
}
