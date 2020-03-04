package com.kinco.MotorApp.ui.functionpage;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.kinco.MotorApp.R;
import com.kinco.MotorApp.fileItem.FileAdapter;
import com.kinco.MotorApp.fileItem.FileBean;
import com.kinco.MotorApp.utils.FileUtil;
import com.kinco.MotorApp.utils.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class FileActivity extends AppCompatActivity{
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null){
            getSupportActionBar().hide();
        }
        setContentView(R.layout.file_page);
        File file = new File(FileUtil.waveDir);
        final String[] fileList = file.list();
        ListView listView = findViewById(R.id.file_list);
        List<String> list = new ArrayList<>();
        if(fileList!=null){
            for(String str:fileList){
                list.add(str);
            }
            FileAdapter fileAdapter = new FileAdapter(list);
            listView.setAdapter(fileAdapter);
        }else{
            list.add(getString(R.string.no_file));
            ArrayAdapter adapter =  new ArrayAdapter(this,R.layout.list_item,list);
            listView.setAdapter(adapter);
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(fileList!=null){
                    Intent intent = new Intent();
                    intent.putExtra("path", FileUtil.waveDir+"/"+fileList[position]);
                    setResult(RESULT_OK,intent);
                    finish();
                }

            }
        });
    }
}
