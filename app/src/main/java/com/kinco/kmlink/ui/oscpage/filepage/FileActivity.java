package com.kinco.kmlink.ui.oscpage.filepage;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.kinco.kmlink.R;
import com.kinco.kmlink.sys.SecondaryActivity;
import com.kinco.kmlink.utils.FileUtil;
import com.wuhenzhizao.titlebar.widget.CommonTitleBar;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class FileActivity extends SecondaryActivity{
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (getSupportActionBar() != null){
//            getSupportActionBar().hide();
//        }
        setContentView(R.layout.file_page);
        CommonTitleBar titleBar = findViewById(R.id.toolBar);
        titleBar.setListener((v, action, extra) -> {
            if (action == CommonTitleBar.ACTION_LEFT_BUTTON) {
                finish();
            }
        });
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
