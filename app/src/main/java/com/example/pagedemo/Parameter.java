package com.example.pagedemo;

public class Parameter {
    private String FC;
    private String Name;
    private String Describe;
    private String Unit;
    private String Range;


    public Parameter() {
        super();
    }

    public Parameter(String FC, String Name, String Describe, String Unit,
                 String Range) {
        super();
        this.FC = FC;
        this.Name = Name;
        this.Describe = Describe;
        this.Unit = Unit;
        this.Range = Range;
    }

    public String getFC() {
        return FC;
    }

    public void setFC(String FC) {
        this.FC = FC;
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

    public void setUnit(String Unit) {
        this.Unit = Unit;
    }

    public String getRange() {
        return Range;
    }

    public void setRange(String Range) {
        this.Range= Range;
    }

}
