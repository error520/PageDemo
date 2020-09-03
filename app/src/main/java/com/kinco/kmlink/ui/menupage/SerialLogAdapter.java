package com.kinco.kmlink.ui.menupage;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kinco.kmlink.R;
import com.kinco.kmlink.sys.SysApplication;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class SerialLogAdapter extends RecyclerView.Adapter {
    private List<String> logList;
    private Context context;

    SerialLogAdapter(Context context, List<String> logList) {
        this.context = context;
        this.logList = logList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(SysApplication.getContext()).inflate(R.layout.item_chat_log,
                parent, false);
        return new LogViewHolder(v);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((LogViewHolder)holder).index.setText(position+"");
        String log = logList.get(position);
        ((LogViewHolder)holder).log.setText(log);
        if(log.startsWith("M")){
            ((LogViewHolder)holder).itemView.setBackgroundColor(context.getColor(R.color.master_log_background));
        }else{
            ((LogViewHolder)holder).itemView.setBackgroundColor(context.getColor(R.color.transparent));
        }
//        Log.d("july18","重建了第"+position+"个");
    }

    @Override
    public int getItemCount() {
        return logList.size();
    }

    private static class LogViewHolder extends RecyclerView.ViewHolder{
        TextView index;
        TextView log;
        LogViewHolder(@NonNull View itemView) {
            super(itemView);
            index = itemView.findViewById(R.id.tv_index);
            log = itemView.findViewById(R.id.tv_log);
        }
    }
}
