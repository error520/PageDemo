package com.kinco.kmlink.ParameterItem;

import android.util.Log;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

/**
 * 参数数据类, 用于存储一个参数的属性, 当前值等信息
 */
public class ParameterBean {
    private String resourceId;
    private String name;
    private int type;
    private List<String> description;
    private Map<Integer,String> optionMap;
    private String address;
    private String currentValue="0";    //十进制存储, 当前要传出的值, 可能被上位机修改, 也可能被下位机修改
    private String defaultValue="0.0";  //默认值

    public ParameterBean(){ }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setDescription(List<String> description) {
        this.description = new ArrayList<>(description);    //传值而不是引用
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getName() {
        return name;
    }

    //0选择型, 1选择共用型, 2无符号数值型, 3有符号数值型
    public int getType() {
        return type;
    }

    public List<String> getDescription() {
        return description;
    }

    public String getAddress() {
        return address;
    }

    public void addDescriptionItem(String string){
        if(this.description==null){
            this.description = new ArrayList<>();
        }
        this.description.add(string);
    }

    public String getCurrentValue() {
        return currentValue;
    }

    public void setCurrentValue(String currentValue) {
        this.currentValue = currentValue;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public float getAccuracy(){
        if(type>=2)
            return Float.parseFloat(description.get(0));
        else
            return 1;
    }

    public String getUnit(){
        try{
            if(type>=2){
                return description.get(1);
            }else {
                return "";
            }
        }catch (Exception e){
            Log.d("child","出问题的是"+resourceId);
        }
        return "";
    }

    public String getHint(){
        return description.get(2);
    }

    /**
     * 返回处理好的hint
     * @return
     */
    public String getRangeHint(){
        String[] range = description.get(2).split("~");
        double min = Double.parseDouble(range[0])*Double.parseDouble(description.get(0));
        double max = Double.parseDouble(range[1])*Double.parseDouble(description.get(0));
        DecimalFormat df = new DecimalFormat(description.get(0));  //显示恰当的小数点位数
        if(type==2){
            return df.format(min) + "~" + df.format(max);
        }else{  //signed value
            max /= 2;
            min = -max;
            return df.format(min) + "~" + df.format(max);
        }
    }

    public double[] getRange(){
        String[] range = description.get(2).split("~");
        double min = Double.parseDouble(range[0])*Double.parseDouble(description.get(0));
        double max = Double.parseDouble(range[1])*Double.parseDouble(description.get(0));
        if(type==2){
            return new double[]{min,max};
        }else{  //signed value
            max /= 2;
            min = -max;
            return new double[]{min,max};
        }
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public void addOption(int index, String option){
        if(this.optionMap == null){
            optionMap = new TreeMap<>();
        }
        optionMap.put(index,option);
    }

    public void setCurrentOption(int index){
        if(optionMap.containsKey(index)){
            currentValue = optionMap.get(index);
        } else{
            currentValue = "error";
        }
    }

    public Map<Integer, String> getOptionMap(){
        return this.optionMap;
    }

    public int getIndexByOption(String option){
        int index = 0;
        for(Integer key:optionMap.keySet()){
            if(Objects.equals(optionMap.get(key), option)){
                index = key;    //选项号
                break;
            }
        }
        return index;
    }

    //调试用
    public void printInfo(){
        Log.d("child","name:"+name);
        Log.d("child","type:"+type);
        for(String kkk:description){
            Log.d("child","description:"+kkk);
        }
        Log.d("child","address:"+address);
    }

}
