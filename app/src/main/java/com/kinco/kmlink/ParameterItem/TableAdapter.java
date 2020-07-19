package com.kinco.kmlink.ParameterItem;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.kinco.kmlink.R;

import java.util.List;

//import com.example.tabledemo.R;


public class TableAdapter extends BaseAdapter {

  private List<OldParameter> list;
  private LayoutInflater inflater;
  public int[] colors = { Color.WHITE, Color.rgb(219, 238, 244) };//RGB颜色
  public TableAdapter(Context context, List<OldParameter> list){
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

    OldParameter OldParameter = (OldParameter) this.getItem(position);

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


    viewHolder.parameterName.setText(OldParameter.getName());
    viewHolder.parameterName.setTextSize(13);
    viewHolder.parameterDescribe.setText(OldParameter.getDescribe()+ OldParameter.getUnit());
    viewHolder.parameterDescribe.setTextSize(13);

    if(OldParameter.getDescribe().equals("true")) {
      viewHolder.parameterName.setTextColor(Color.GREEN);
      viewHolder.parameterDescribe.setTextColor(Color.GREEN);
    }
    if(OldParameter.getDescribe().equals("false")) {
      viewHolder.parameterName.setTextColor(Color.RED);
      viewHolder.parameterDescribe.setTextColor(Color.RED);
    }
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

