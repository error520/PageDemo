package com.kinco.kmlink.ui.firstpage;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.kinco.kmlink.ParameterItem.ParameterBean;
import com.kinco.kmlink.ui.widget.CustomItemPopup;
import com.kinco.kmlink.utils.PrefUtil;
import com.kinco.kmlink.utils.ResourceUtil;
import com.kinco.kmlink.utils.util;
import com.kinco.kmlink.R;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import razerdp.basepopup.BasePopupWindow;
import razerdp.basepopup.QuickPopupBuilder;
import razerdp.basepopup.QuickPopupConfig;

public class SettingCardAdapter extends RecyclerView.Adapter {
    Context context;
    List<ParameterBean> datas;
    private int fragmentIndex;
    private OnSetButtonListener onSetButtonListener;
    CustomItemPopup customItemPopup;

    public SettingCardAdapter(Context context, int index, List<ParameterBean> datas){
        this.context = context;
        this.fragmentIndex = index;
        this.datas = datas;
    }

    public void setOnSetButtonListener(OnSetButtonListener onSetButtonListener){
        this.onSetButtonListener = onSetButtonListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_setting_card, parent, false) ;
        return new ValueViewHolder(v);
    }

    //把数据绑定到视图上, 并绑定回调(设置各种按键功能)
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ParameterBean parameter = datas.get(position);
        ValueViewHolder valueHolder = (ValueViewHolder)holder;
        valueHolder.name.setText(parameter.getName());
        String currentValue = parameter.getCurrentValue();
        if(parameter.getType()>=2){ //数值型要加上单位
            currentValue +=" "+parameter.getUnit();
        } else if(PrefUtil.showNum && !currentValue.equals("error")){        //选项型是否添加选项号
            int index = parameter.getIndexByOption(currentValue);
            currentValue = index+": "+currentValue;        //添加选项号
        }
        valueHolder.value.setText(currentValue);
        valueHolder.itemView.setOnClickListener(v->{
            if(onSetButtonListener!=null){
                onSetButtonListener.onClick(position);
            }
        });
        valueHolder.itemView.setOnLongClickListener(v->{
            showPopupMenu(v,parameter);
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    @Override
    public int getItemViewType(int position) {
        ParameterBean parameter = datas.get(position);
        return parameter.getType();
    }

    /**
     * value的viewHolder
     */
    static class ValueViewHolder extends RecyclerView.ViewHolder{
        TextView name;
        TextView value;
        ValueViewHolder(View itemView){
            super(itemView);
            name = itemView.findViewById(R.id.tv_setting_name);
            value = itemView.findViewById(R.id.tv_setting_value);
        }
    }

    private void showPopupMenu(View v, ParameterBean bean){
        if(fragmentIndex==0){
            removeFromGeneral(v,bean);
        }else{
            addToGeneral(v,bean);
        }
    }

    private void addToGeneral(View v,ParameterBean bean){
        customItemPopup = new CustomItemPopup(context,R.layout.popup_add_to_general);
        customItemPopup.setOnClickListener(()->{
            if(PrefUtil.addItemToGeneral(context,bean.getResourceId())){   //返回值说明是否添加成功, 若失败是因为已经存在
                util.centerToast(context,"添加成功",0);
                List<ParameterBean> list = SettingPagerAdapter.getFragments().get(0).getParameterList();
                list.add(bean);
            }else {
                util.centerToast(context,"请勿重复添加",0);
            }
            customItemPopup.dismiss();
        });
        customItemPopup.showPopupWindow(v);
    }

    private void removeFromGeneral(View v, ParameterBean bean){
        customItemPopup = new CustomItemPopup(context,R.layout.popup_remove_from_general);
        for(String i:ResourceUtil.settingGeneral){
            if(bean.getResourceId().equals(i)){
                customItemPopup.enable(false);
            }
        }
        customItemPopup.setOnClickListener(()->{
            datas.remove(bean);
            PrefUtil.removeItemFromGeneral(context,bean.getResourceId());
            notifyDataSetChanged();
            customItemPopup.dismiss();
        });
        customItemPopup.showPopupWindow(v);
    }



//    private void showPopupMenu2(View v, ParameterBean parameter){
//        PopupMenu popupMenu = new PopupMenu(context,v,Gravity.TOP);
//        if(fragmentIndex==0){
//            popupMenu.getMenuInflater().inflate(R.menu.remove_from_general,popupMenu.getMenu());
//            popupMenu.setOnMenuItemClickListener(item -> {
//                PrefUtil.removeAllItems(context);
//                datas.remove(parameter);
//                notifyDataSetChanged();
//                return true;
//            });
//        }else{
//            popupMenu.getMenuInflater().inflate(R.menu.add_to_general,popupMenu.getMenu());
//            popupMenu.setOnMenuItemClickListener(item -> {
//                if(PrefUtil.addItemToGeneral(context,parameter.getResourceId())){
//                    util.centerToast(context,"添加成功",0);
//
//                }else {
//                    util.centerToast(context,"请勿重复添加",0);
//                }
//                return true;
//            });
//        }
//        popupMenu.setGravity(Gravity.TOP);
//        popupMenu.show();
//    }

    public interface OnSetButtonListener{
        void onClick(int position);
    }

}
