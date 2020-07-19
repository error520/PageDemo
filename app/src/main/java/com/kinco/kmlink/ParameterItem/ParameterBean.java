package com.kinco.kmlink.ParameterItem;

import android.util.Log;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;


public class ParameterBean {
    private String resourceId;
    private String name;
    private int type;
    private String rangeHint;
    private List<String> description;
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
            return Float.valueOf(description.get(0));
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
        double min = Double.valueOf(range[0])*Double.valueOf(description.get(0));
        double max = Double.valueOf(range[1])*Double.valueOf(description.get(0));
        DecimalFormat df = new DecimalFormat(description.get(0));  //显示恰当的小数点位数
        if(type==2){
            String hint = df.format(min) + "~" + df.format(max);
            return hint;
        }else{  //signed value
            max /= 2;
            min = -max;
            String hint = df.format(min) + "~" + df.format(max);
            return hint;
        }
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }
    
    public void printInfo(){
        Log.d("child","name:"+name);
        Log.d("child","type:"+type);
        for(String kkk:description){
            Log.d("child","description:"+kkk);
        }
        Log.d("child","address:"+address);
    }

}
