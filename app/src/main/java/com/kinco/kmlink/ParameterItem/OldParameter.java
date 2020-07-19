package com.kinco.kmlink.ParameterItem;

public class OldParameter {

    private String Name;
    private String Describe;
    private String Unit;
    private boolean sign;
    private double min;


    public OldParameter() {
        super();
    }

    public OldParameter(String Name, String Describe, String Unit, boolean sign, double min) {
        super();

        this.Name = Name;
        this.Describe = Describe;
        this.Unit=Unit;
        this.sign=sign;
        this.min=min;


    }


    public String getName() {
        return Name;
    }

    public void setName(String goodsName) {
        this.Name = Name;
    }

    public String getDescribe() {
        return Describe;
    }

    public void setDescribe(String Describe) {
        this.Describe = Describe;
    }

    public String getUnit() {
        return Unit;
    }

    public void setUnit(String Describe) {
        this.Unit = Unit;
    }

    public boolean getSign() {
        return sign;
    }

    public double getMin() {
        return min;
    }



}
