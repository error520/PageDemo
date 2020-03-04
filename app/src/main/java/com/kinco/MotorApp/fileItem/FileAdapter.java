package com.kinco.MotorApp.fileItem;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.kinco.MotorApp.R;
import com.kinco.MotorApp.sys.SysApplication;
import com.kinco.MotorApp.utils.FileUtil;
import com.kinco.MotorApp.utils.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

import androidx.core.content.FileProvider;

public class FileAdapter extends BaseAdapter {
    List<String> fileList;
    Context context = SysApplication.getContext();
    LayoutInflater inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    private String mAuthority = "com.kinco.MotorApp.fileprovider";
    public FileAdapter(List<String> list){
        this.fileList = list;
    }

    @Override
    public int getCount() {
        return fileList.size();
    }

    @Override
    public Object getItem(int position) {
        return fileList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        //Log.d("BreakPoint","getView");
        if(convertView==null)
            convertView = inflater.inflate(R.layout.file_item,null);
        TextView textView = convertView.findViewById(R.id.file_name);
        textView.setText(fileList.get(position));
        ImageView btnShare = convertView.findViewById(R.id.btnShare);
        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File file = getFile(position);
                if(file.exists()){
                    Uri uri = FileProvider.getUriForFile(context,mAuthority,file);
                    Intent shareIntent = new Intent();
                    shareIntent.setAction(Intent.ACTION_SEND);
                    shareIntent.setType("*/*"); //
                    shareIntent.putExtra(Intent.EXTRA_STREAM, uri);//分享出去的内容
                    shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    context.startActivity(shareIntent);    //注意这里的变化
                }
        }});
        ImageView btnDelete = convertView.findViewById(R.id.btnDelete);
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File file = getFile(position);
                if(file!=null){
                    file.delete();
                    fileList.remove(position);
                    notifyDataSetChanged();
                }else
                    util.centerToast(context,"文件不存在",0);

            }
        });
        return convertView;
    }

    public static Uri getUriForFile(String path) {
        return new Uri.Builder().scheme("content").authority("com.kinco.MotorApp.fileprovider").encodedPath(path).build();
    }

    private File getFile(int positon){
        File file = new File(FileUtil.waveDir+File.separator+fileList.get(positon));
        if(file.isFile()&&file.exists())
            return file;
        else
            return null;
    }

    private void shareFile(){

    }

    private void deleteFile(int position){
        if(!fileList.isEmpty()){
           String fileName = fileList.get(position);
           File file = new File(FileUtil.waveDir,fileName);
           if(file.exists()&&file.isFile()){
               file.delete();
           }

        }
    }

}
