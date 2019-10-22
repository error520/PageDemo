package com.example.pagedemo;

public class Parameter {

    private String Name;
    private String Describe;



    public Parameter() {
        super();
    }

    public Parameter( String Name, String Describe) {
        super();

        this.Name = Name;
        this.Describe = Describe;

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


}
