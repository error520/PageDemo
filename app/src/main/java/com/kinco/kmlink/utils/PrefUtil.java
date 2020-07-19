package com.kinco.kmlink.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;


import com.kinco.kmlink.ParameterItem.ParameterBean;

import java.util.List;
import java.util.Locale;

/**
 * Created by sunnyDay on 2019/7/17 17:48
 *
 * 存储、获取语言的工具类
 */
public class PrefUtil {

    private static final String PREF = "spf";
    private static String KEY_LANGUAGE = "app_language";
    private static String KEY_GENERAL_EXTRA = "general_extra_items";    //添加到常用的额外item
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

}
