package com.kinco.MotorApp.ParameterItem;

import android.content.Context;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.kinco.MotorApp.R;;

import java.util.List;

/**
 * 设置界面的adapter
 */
public class ListViewAdapter extends BaseAdapter {

    private List<ItemBean> mData;
    private Context mContext;
    private LayoutInflater inflater;
    public int[] colors = { Color.WHITE, Color.rgb(219, 238, 244) };//RGB颜色

    //监听器定义
    private ListViewAdapter.AddressNoListener addressNoListener;

    //接口定义
    public interface AddressNoListener{
        void clickListener(String Address, String Name,String Unit,String Range,float Min, String defaultValue,String currentValue,
                           int group, int position);  //确定传出的值
    }
    //    public AddressNoListener getAddressNoListener(){return addressNoListener;}
    public void setAddressNoListener(ListViewAdapter.AddressNoListener addressNoListener)
    {this.addressNoListener = addressNoListener;}

    public ListViewAdapter(Context mContext, List<ItemBean> mData) {
        this.mContext = mContext;
        this.mData = mData;
        inflater = LayoutInflater.from(mContext);

    }


    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ItemBean ItemBean=(ItemBean)this.getItem(position);
        ViewHolder holder = null;
        final ItemBean itemObj = mData.get(position);
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_set, null);
            holder = new ViewHolder(convertView);
            holder.edit_name=(TextView)convertView.findViewById(R.id.edit_name);//1
            holder.edit_unit=(TextView)convertView.findViewById(R.id.edit_unit);
            holder.editText.setHint(itemObj.getCurrentValue());
            holder.edit_current=(Button)convertView.findViewById(R.id.button_write) ;
            holder.edit_current.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    addressNoListener.clickListener(itemObj.getAddress(),itemObj.getName(),itemObj.getUnit(),
                            itemObj.getRange(),itemObj.getMin(),itemObj.getDefaultValue(),itemObj.getCurrentValue(),
                            itemObj.group, itemObj.positon);
                }
            });
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }


//        holder.edit_name.setText(position+1+"");//1
//        holder.edit_name.setText(GroupArray[position]);

        holder.edit_name.setText(ItemBean.getName());
        holder.edit_name.setTextSize(13);
        holder.edit_unit.setText(ItemBean.getUnit());
        holder.edit_unit.setTextSize(13);
//        holder.edit_current.setText(ItemBean.getCurrent());
        //This is important. Remove TextWatcher first.
        if (holder.editText.getTag() instanceof TextWatcher) {
            holder.editText.removeTextChangedListener((TextWatcher) holder.editText.getTag());
        }

        holder.editText.setText(itemObj.getCurrentValue());

        TextWatcher watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (TextUtils.isEmpty(s)) {
                    itemObj.setCurrentValue("");
                } else {
                    itemObj.setCurrentValue(s.toString());
                }
            }
        };

        holder.editText.addTextChangedListener(watcher);
        holder.editText.setTag(watcher);

//        convertView.setBackgroundColor(colors[position % 2]);// 每隔item之间颜色不同

        return convertView;

    }

    private class ViewHolder {
        public TextView edit_name;
        public Button edit_current;
        public TextView edit_unit;
        private TextView editText;//1

        public ViewHolder(View convertView) {
            editText = (TextView) convertView.findViewById(R.id.edit_text);

        }
    }

}
