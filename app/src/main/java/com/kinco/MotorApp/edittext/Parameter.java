package com.kinco.MotorApp.edittext;

public class Parameter {

    private String Name;
    private String Describe;
    private String Unit;


    public Parameter() {
        super();
    }

    public Parameter( String Name, String Describe,String Unit) {
        super();

        this.Name = Name;
        this.Describe = Describe;
        this.Unit=Unit;

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


}
