package com.kinco.kmlink.ui.secondpage;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.kinco.kmlink.ParameterItem.ParameterBean;
import com.kinco.kmlink.R;
import com.kinco.kmlink.utils.PrefUtil;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ReadCardAdapter extends RecyclerView.Adapter {
    private Context context;
    private List<ParameterBean> datas;
    private String type;

    ReadCardAdapter(Context context, List<ParameterBean> datas, String viewType){
        this.context = context;
        this.datas = datas;
        this.type = viewType;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(type.equals("card")){
            View v = LayoutInflater.from(context).inflate(R.layout.item_read_card,parent,false);
            return new ViewHolder(v);
        }else{
            View v = LayoutInflater.from(context).inflate(R.layout.item_status_word,parent,false);
            return new SwViewHolder(v);
        }

    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof ViewHolder){
            ViewHolder viewHolder = (ViewHolder)holder;
            ParameterBean parameter = datas.get(position);
            viewHolder.name.setText(parameter.getName());
            if(parameter.getType()>=2){
                viewHolder.value.setText(parameter.getCurrentValue()+" "+parameter.getUnit());
                viewHolder.progressBar.setVisibility(View.VISIBLE);
                viewHolder.progressBar.setMax((int)Double.parseDouble(parameter.getRangeHint().split("~")[1]));
                int newValue = Math.abs((int)Double.parseDouble(parameter.getCurrentValue()));
                if(newValue!=viewHolder.progressBar.getProgress()){
                    viewHolder.progressBar.setProgress(newValue,false);
                }
            }else{
                String currentValue = parameter.getCurrentValue();
                if(PrefUtil.showNum && !currentValue.equals("error") && !currentValue.equals("null")
                && !parameter.getResourceId().equals("B0_05")){
                    currentValue = parameter.getIndexByOption(currentValue)+": "+currentValue;
                }
                viewHolder.value.setText(currentValue);
                viewHolder.progressBar.setVisibility(View.GONE);
            }
        }else{
            SwViewHolder viewHolder = (SwViewHolder)holder;
            ParameterBean parameter = datas.get(position);
            viewHolder.name.setText(parameter.getName());
            viewHolder.value.setText(parameter.getCurrentValue());
        }

    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView name;
        TextView value;
        ProgressBar progressBar;
        ViewHolder(View itemView){
            super(itemView);
            name = itemView.findViewById(R.id.tv_read_name);
            value = itemView.findViewById(R.id.tv_read_value);
            progressBar = itemView.findViewById(R.id.pb_value);
        }
    }

    static class SwViewHolder extends RecyclerView.ViewHolder{
        TextView name;
        TextView value;
        SwViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.tv_sw_name);
            value = itemView.findViewById(R.id.tv_sw_value);
        }
    }

}
