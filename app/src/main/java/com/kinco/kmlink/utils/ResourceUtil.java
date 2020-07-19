package com.kinco.kmlink.utils;

import android.content.Context;
import android.content.res.Resources;
import android.text.TextUtils;
import android.util.Log;

import com.kinco.kmlink.ParameterItem.ParameterBean;
import com.kinco.kmlink.R;

import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;

public class ResourceUtil {
    public static String[] settingGeneral = {
            "A0_01",
            "A0_02",
            "A0_03",
            "A0_04",
            "A0_05",
            "A0_33",
            "A0_34",
            "A0_23"
    };
    public static String[] settingMotor = {
            "A0_37",
            "A0_18",
            "A0_24",
            "A0_25",
            "A0_26",
            "A0_27",
            "A0_19",
            "A0_20",
            "A0_28",
            "A0_29",
            "A0_30",
            "A0_21"
    };
    public static String[] settingVF = {
            "A0_32",
            "A0_46",
            "A0_47",
            "A0_48",
            "A0_49",
            "A0_50",
            "A0_51",
            "A0_52",
            "A0_53"
    };
    public static String[] settingTerminal = {
            "A0_10",
            "A0_08",
            "A0_09",
            "A0_40",
            "A0_41",
            "A0_42",
            "A0_43",
            "A0_14",
            "A0_44",
            "A0_12",
            "A0_45",
            "A0_11",
            "A0_13"
    };
    public static String[] settingControl = {
            "A0_06",
            "A0_07",
            "A0_31",
            "A0_36",
            "A0_15",
            "A0_16",
            "A0_17",
            "A0_22",
            "A0_35",
            "A0_02",
            "A0_03",
            "A0_38",
            "A0_39"
    };
    public static String[][] settingParameters = {settingGeneral,settingMotor,settingVF,settingTerminal,settingControl};

    public static String[] readGeneral = {
            "B0_00",
            "B0_01",
            "B0_02",
            "B0_03",
            "B0_17",
            "B0_09",
            "B0_10",
            "B0_16"
    };
    public static String[] readTerminal = {
            "B0_06",
            "B0_07",
            "B0_18",
            "B0_19",
            "B0_20",
            "B0_21",
            "B0_22",
            "B0_23"
    };
    public static String[] readFault = {
            "B0_10",
            "B0_11",
            "B0_12",
            "B0_13"
    };
    public static String[] readStatus = {
            "B0_05"
    };
    public static String[] readOthers = {
            "B0_14",
            "B0_15"
    };
    public static String[][] readParameters = {readGeneral,readTerminal,readFault,readStatus,readOthers};


    //以选择的options里位置取值
    public static String[][] getOptions(Resources res,int []item){
        String [][]options = new String[item.length][];
        for(int i=0; i<item.length; i++)
            options[i] = res.getStringArray(R.array.A0_options)[item[i]].split(",");
        return options;
    }

    //以功能码取值
    public static String[][] getOptions(Context context, int []item){
        String [][]options = new String[item.length][];
        int compareNum[] = {1,2,4,5,8,9,10,11,12,13,14,15,17,33,34,35,37,38,40,41,42,43,44,45,46};
        for(int i=0; i<item.length; i++){
            for(int j=0; j<compareNum.length; j++){
                String nn[] = context.getResources().getStringArray(R.array.A0_options)[j].split(",");
                if(item[i]==compareNum[j]) {
                    options[i] = context.getResources().getStringArray(R.array.A0_options)[j].split(",");
                }

            }

        }

        return options;

    }

    /**
     * 批量得到参数名
     * @param context
     * @param item
     * @return
     */
    public static String[] getName(Context context, int []item){
        String []Name = new String[item.length];
        Resources res = context.getResources();
        for(int i=0;i<item.length;i++){
            String num = String.valueOf(item[i]);
            if(num.length()<2)
                num = "0"+num;
            int id = res.getIdentifier("A0_"+num,"string",context.getPackageName());
            Name[i] = context.getResources().getString(id);
        }
        return Name;
    }

    public static float[] getMin(Context context, int item[]){
        float []Min = new float[item.length];
        Resources res = context.getResources();
        for(int i=0;i<item.length;i++){
            String num = String.valueOf(item[i]);
            if(num.length()<2)
                num = "0"+num;
            int id = res.getIdentifier("A0_"+num+"M","string",context.getPackageName());
            Min[i] = Float.parseFloat(res.getString(id));
        }

        return Min;
    }

    public static String[] getUnit(Context context, int item[]){
        String []Unit = new String[item.length];
        Resources res = context.getResources();
        for(int i=0;i<item.length;i++){
            String num = String.valueOf(item[i]);
            if(num.length()<2)
                num = "0"+num;

            Log.d("XmlUtil","A0_"+num+"U");
            int id = res.getIdentifier("A0_"+num+"U","string",context.getPackageName());
            Unit[i] = res.getString(id);
        }
        return Unit;
    }

    public static String[] getHint(Context context, int []item){
        String []Hint = new String[item.length];
        Resources res = context.getResources();
        for(int i=0;i<item.length;i++){
            String num = String.valueOf(item[i]);
            if(num.length()<2)
                num = "0"+num;
            int id = res.getIdentifier("A0_"+num+"H","string",context.getPackageName());
            Hint[i] = context.getResources().getString(id);
        }
        return Hint;
    }

    public static List<ParameterBean> getParameters(Context context,String[] parameterIds){
        List<ParameterBean> parameterBeanList = new ArrayList<>();
        Resources res = context.getResources();
        for(String i:parameterIds) {
            try {
                if(i.equals("A0_23")){
                    continue;
                }
                int id = res.getIdentifier(i, "array", context.getPackageName());
                String[] parameterXmlArray = res.getStringArray(id);
                ParameterBean bean = new ParameterBean();
                bean.setName(parameterXmlArray[0]);
//                for(String yyy:parameterXmlArray)
//                    Log.d("child",yyy);
                int type = Integer.valueOf(parameterXmlArray[1]);
                bean.setType(type);
                if (type == 0 || type >= 2) {
                    //从2到末尾是选项或数值属性
                    for (int j = 2; j < parameterXmlArray.length; j++) {
                        bean.addDescriptionItem(parameterXmlArray[j]);
                    }
                } else if (type == 1) {
                    id = res.getIdentifier(parameterXmlArray[2], "array", context.getPackageName());
                    String[] options = res.getStringArray(id);
                    for (String option : options) {
                        bean.addDescriptionItem(option);
                    }
                }
                if(type<2){
                    bean.setCurrentValue(bean.getDescription().get(0)); //初始值
                }
                bean.setResourceId(i);
                bean.setAddress(idToAddress(i));
                parameterBeanList.add(bean);
            } catch (Exception e) {
                Log.e("child", "出错的是" + i);
                e.printStackTrace();
            }
        }
        return parameterBeanList;
    }

    public static List<ParameterBean> getBParameters(Context context, String[] resourceId){
        List<ParameterBean> parameterBeanList = new ArrayList<>();
        Resources res = context.getResources();
        for(String i:resourceId){
            try{
                int id = res.getIdentifier(i,"array",context.getPackageName());
                String[] parameterXmlArray = res.getStringArray(id);
                if(!i.equals("B0_05")){ //其他正常的参数
                    ParameterBean bean = new ParameterBean();
                    if(PrefUtil.getLanguage(context).equals("en")){
                        bean.setName(parameterXmlArray[0]);
                    }else{
                        bean.setName(parameterXmlArray[1]);
                    }
                    int type = Integer.valueOf(parameterXmlArray[2]);
                    bean.setType(type);
                    if(type==1){    //共用选项参数
                        id = res.getIdentifier(parameterXmlArray[3], "array", context.getPackageName());
                        String[] options = res.getStringArray(id);
                        for(String option:options){
                            bean.addDescriptionItem(option);
                        }
                        bean.setCurrentValue("null");
                    }else{
                        for(int j=3; j<parameterXmlArray.length; j++){
                            bean.addDescriptionItem(parameterXmlArray[j]);
                        }
                        if(type==0){
                            bean.setCurrentValue("null");
                        }
                    }
                    bean.setResourceId(i);
                    bean.setAddress(idToAddress(i));
                    parameterBeanList.add(bean);
                }else{  //B0.05参数(设备运行状态)
                    int offset = PrefUtil.getLanguage(context).equals("en")?3:4;
                    for(int j=offset; j<parameterXmlArray.length; j+=2){
                        ParameterBean bean = new ParameterBean();
                        bean.setName(parameterXmlArray[j]);
                        bean.setType(0);
                        bean.addDescriptionItem("false");
                        bean.addDescriptionItem("true");
                        bean.setCurrentValue("null");
                        bean.setResourceId(i);
                        bean.setAddress(idToAddress(i));
                        parameterBeanList.add(bean);
                    }
                }

            }catch (Exception e){
                Log.e("child", "出错的是" + i);
                e.printStackTrace();
            }
        }
        return parameterBeanList;
    }

    private static String idToAddress(String id){
        String[] a1 = id.split("_");
        int data  =Integer.parseInt(a1[1],10);
        String address = Integer.toString(data,16);
        if(address.length()<2){
            address = "0"+address;
        }
        if(id.contains("A")){
            address = "00"+address;
        }else{
            address = "01"+address;
        }
        return address;
    }




}
