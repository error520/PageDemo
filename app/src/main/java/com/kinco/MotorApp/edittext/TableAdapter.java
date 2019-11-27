package com.kinco.MotorApp.edittext;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.kinco.MotorApp.R;
import com.kinco.MotorApp.edittext.Parameter;

import java.util.List;

//import com.example.tabledemo.R;


public class TableAdapter extends BaseAdapter {

  private List<Parameter> list;
  private LayoutInflater inflater;
  public int[] colors = { Color.WHITE, Color.rgb(219, 238, 244) };//RGB颜色
  public TableAdapter(Context context, List<Parameter> list){
    this.list = list;
    inflater = LayoutInflater.from(context);
  }

  @Override
  public int getCount() {
    int ret = 0;
    if(list!=null){
      ret = list.size();
    }
    return ret;
  }

  @Override
  public Object getItem(int position) {
    return list.get(position);
  }

  @Override
  public long getItemId(int position) {
    return position;
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {

    Parameter Parameter = (Parameter) this.getItem(position);

    ViewHolder viewHolder;

    if(convertView == null){

      viewHolder = new ViewHolder();

      convertView = inflater.inflate(R.layout.list_item1, null);
      viewHolder.parameterName = (TextView) convertView.findViewById(R.id.text_Name);
      viewHolder.parameterDescribe = (TextView) convertView.findViewById(R.id.text_Describe);
      convertView.setTag(viewHolder);
    }else{
      viewHolder = (ViewHolder) convertView.getTag();
    }


    viewHolder.parameterName.setText(Parameter.getName());
    viewHolder.parameterName.setTextSize(13);
    viewHolder.parameterDescribe.setText(Parameter.getDescribe());
    viewHolder.parameterDescribe.setTextSize(13);
//    convertView.setBackgroundColor(colors[position % 2]);// 每隔item之间颜色不同
    return convertView;
  }

  public static class ViewHolder{
    public TextView parameterFC;
    public TextView parameterName;
    public TextView parameterDescribe;
    public TextView parameterUnit;
    public TextView parameterRange;

  }


}

