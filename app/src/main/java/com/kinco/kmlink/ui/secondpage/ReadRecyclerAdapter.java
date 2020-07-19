package com.kinco.kmlink.ui.secondpage;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.kinco.kmlink.ParameterItem.ParameterBean;
import com.kinco.kmlink.R;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ReadRecyclerAdapter extends RecyclerView.Adapter {
    Context context;
    List<ParameterBean> datas;

    ReadRecyclerAdapter(Context context, List<ParameterBean> datas){
        this.context = context;
        this.datas = datas;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_read_card,parent,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ParameterBean parameter = datas.get(position);
//        Log.d("july18","重建"+parameter.getName());
        ((ViewHolder)holder).name.setText(parameter.getName());
        ((ViewHolder)holder).value.setText(parameter.getCurrentValue()+" "+parameter.getUnit());
        if(parameter.getType()>=2){
            ((ViewHolder)holder).progressBar.setVisibility(View.VISIBLE);
            ((ViewHolder)holder).progressBar.setMax((int)Double.parseDouble(parameter.getRangeHint().split("~")[1]));
            ((ViewHolder)holder).progressBar.setProgress((int)Double.parseDouble(parameter.getCurrentValue()),true);
        }else{
            ((ViewHolder)holder).progressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
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
}
