package com.kinco.MotorApp.utils;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

import com.kinco.MotorApp.R;

public class XmlUtil {
    public static String[][] getOptions(Resources res,int []item){
        String [][]options = new String[item.length][];
        for(int i=0; i<item.length; i++)
            options[i] = res.getStringArray(R.array.A0_options)[item[i]].split(",");
        return options;
    }

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

}
