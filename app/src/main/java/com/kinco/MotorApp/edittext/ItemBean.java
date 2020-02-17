package com.kinco.MotorApp.edittext;

/**
 * Modified by Nicholas on 2019/12/21 0017.
 */
public class ItemBean {
    private String Name;
    private String text;
    private String Unit;
    private String Address;
    private String Range;
    private float Min;  //最小单位
    private String defaultValue="0";
    private String currentValue="0";
    public int group;
    public int positon;


    public ItemBean() {
        super();
    }
    public ItemBean(String Name,String Unit, String Range, float Min,String currentValue ,String Address,
                    int group,int positon) {
        super();

        this.Name = Name;
        this.Unit=Unit;
        this.currentValue=currentValue;
        this.Range=Range;
        this.Min=Min;
        this.Address=Address;

        this.group = group;
        this.positon = positon;

    }
    public String getAddress() {
        return Address;
    }

    public void setAddress(String Address) {
        this.Address=Address;
    }


    public String getName() {
        return Name;
    }

    public void setName(String Name) {
        this.Name = Name;
    }
    public String getUnit() {
        return Unit;
    }
    public String getRange(){return Range;}
    public float getMin(){return Min;}


    public String getCurrentValue() {
        return currentValue;
    }

    public void setCurrentValue(String currentValue) {
        this.currentValue = currentValue;
    }


//
//    public String getText() {
//        return text;
//    }
//
//    public void setText(String text) {
//        this.text = text;
//    }

    public String getDefaultValue(){return defaultValue;}
    public void setDefaultValue(String defaultValue){this.defaultValue = defaultValue;}



}
