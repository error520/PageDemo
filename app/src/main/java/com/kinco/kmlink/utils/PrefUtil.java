package com.kinco.kmlink.utils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;


import com.kinco.kmlink.ParameterItem.ParameterBean;

import java.util.List;
import java.util.Locale;

/**
 * Created by Nicholas on 2020/7/17
 *
 * 存储、获取偏好配置的工具类
 */
public class PrefUtil {
    //偏好配置-键
    private static final String PREF = "spf";
    private static String KEY_LANGUAGE = "app_language";
    private static String KEY_GENERAL_EXTRA = "general_extra_items";    //添加到常用的额外item

    private static String KEY_BLE_GAP = "ble_message_gap";  //报文间隔
    private static String KEY_READ_GAP = "read_gap";    //状态读取间隔
    private static String KEY_WAIT_TIME = "wait_time";  //超时等待时间
    private static String KEY_SHOW_NUM = "show_option_num";     //显示选项序号

    public static int bleGap = 50;
    public static int readGap = 1000;
    public static int timeoutWaiting = 2000;
    public static boolean showNum = true;
    /**
     * 获得存入sp的语言
     * */
    public static String getLanguage(Context context) {
        if (TextUtils.isEmpty(getSpf(context, KEY_LANGUAGE))) {
            return Locale.getDefault().getLanguage();   //如果没有存入偏好设置, 则返回当前默认语言;
        }
        return getSpf(context, KEY_LANGUAGE);
    }

    /**
     * 语言存入sp
     * */
    public static void setLanguage(Context context, String value) {
        if (value == null) {
            value = "en";//context.getResources().getString(R.string.tv_english);
        }
        setSpf(context, KEY_LANGUAGE, value);
    }

    private static String getSpf(Context context, String key) {
        return context.getSharedPreferences(PREF, Context.MODE_PRIVATE).getString(key, "");
    }

    private static void setSpf(Context context, String key, String value) {
        SharedPreferences.Editor editor = context.getSharedPreferences(PREF, Context.MODE_PRIVATE).edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static List<ParameterBean> getCustomItems(Context context){
        String generalExtraSpf = PrefUtil.getSpf(context, KEY_GENERAL_EXTRA);
        if(TextUtils.isEmpty(generalExtraSpf)) {
            return null;
        }else{
            String[] savedIds = generalExtraSpf.split("\\+");
            return ResourceUtil.getParameters(context,savedIds);
        }
    }

    public static boolean addItemToGeneral(Context context, String resourceId){
        String generalExtraSpf = getSpf(context, KEY_GENERAL_EXTRA);
        if(TextUtils.isEmpty(generalExtraSpf)){  //此前没添加过item
            setSpf(context,KEY_GENERAL_EXTRA,resourceId);
        }else{  //此前已经有了
            String[] savedIds = generalExtraSpf.split("\\+");
            for(String i:savedIds){
                if(i.equals(resourceId)){
                    return false;   //已经添加过, 不能重复添加
                }
            }
            //如果没有重复的, 则添加
            setSpf(context,KEY_GENERAL_EXTRA,generalExtraSpf+"+"+resourceId);
        }
        return true;
    }

    public static void removeItemFromGeneral(Context context, String resourceId){
        String generalExtraSpf = getSpf(context, KEY_GENERAL_EXTRA);
        if(generalExtraSpf.contains(resourceId+"+")){
            generalExtraSpf = generalExtraSpf.replace(resourceId+"+","");
        }else if(generalExtraSpf.contains(resourceId)){     //其在末尾
            generalExtraSpf = generalExtraSpf.replace(resourceId,"");
        }
        setSpf(context,KEY_GENERAL_EXTRA,generalExtraSpf);
    }

    public static boolean removeAllItems(Context context){
        if(TextUtils.isEmpty(getSpf(context,KEY_GENERAL_EXTRA))){
            util.centerToast(context,"已经空了",0);
            return false;
        }else{
            SharedPreferences.Editor editor = context.getSharedPreferences(PREF,Context.MODE_PRIVATE).edit();
            editor.remove(KEY_GENERAL_EXTRA);
            editor.commit();
            util.centerToast(context,"清空成功",0);
            return true;
        }
    }

    public static void setPreferences(Context context, int index, String value){
        switch (index){
            case 0:
                bleGap = Integer.parseInt(value);
                setSpf(context,KEY_BLE_GAP,value);
                break;
            case 1:
                readGap = Integer.parseInt(value);
                setSpf(context,KEY_READ_GAP,value);
                break;
            case 2:
                timeoutWaiting = Integer.parseInt(value);
                setSpf(context,KEY_WAIT_TIME,value);
                break;
            case 3:
                showNum = Boolean.parseBoolean(value);
                setSpf(context,KEY_SHOW_NUM,value);
                break;
        }
    }

    public static int getBleMessageGap(Context context){
        if(TextUtils.isEmpty(getSpf(context,KEY_BLE_GAP))){
            return 50;
        }else{
            return Integer.parseInt(getSpf(context,KEY_BLE_GAP));
        }
    }

    public static int getReadGap(Context context){
        if(TextUtils.isEmpty(getSpf(context,KEY_READ_GAP))){
            return 1000;
        }else{
            return Integer.parseInt(getSpf(context,KEY_READ_GAP));
        }
    }

    public static int getTimeoutWaiting(Context context){
        if(TextUtils.isEmpty(getSpf(context,KEY_WAIT_TIME))){
            return 2000;
        }else{
            return Integer.parseInt(getSpf(context,KEY_WAIT_TIME));
        }
    }

    public static boolean getShowNum(Context context){
        String flag = getSpf(context,KEY_SHOW_NUM);
        return TextUtils.isEmpty(flag) || flag.equals("true");
    }

    public static void setShowNum(Context context, boolean show){
        showNum = show;
        if(show){
            setSpf(context,KEY_SHOW_NUM,"true");
        }else {
            setSpf(context,KEY_SHOW_NUM,"false");
        }
    }

    public static void initConfig(Context context){
        bleGap = getBleMessageGap(context);
        readGap = getReadGap(context);
        timeoutWaiting = getTimeoutWaiting(context);
        showNum = getShowNum(context);
    }
}
