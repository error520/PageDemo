package com.kinco.MotorApp.edittext;

/**
 * Created by zengd on 2016/8/17 0017.
 */
public class ItemBean {
    private String Name;
    private String Current;
    private String text;
    private String address;

    public ItemBean() {
        super();
    }
    public ItemBean(String Name,String Current, String text, String address) {
        super();

        this.Name = Name;
        this.Current=Current;
        this.text=text;
        this.address=address;

    }
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address=address;
    }


    public String getName() {
        return Name;
    }

    public void setName(String Name) {
        this.Name = Name;
    }

    public String getCurrent() {
        return Current;
    }

    public void setCurrent(String Current) {
        this.Current = Current;
    }


    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }


}
