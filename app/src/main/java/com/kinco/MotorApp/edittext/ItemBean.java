package com.kinco.MotorApp.edittext;

/**
 * Created by zengd on 2016/8/17 0017.
 */
public class ItemBean {
    private String Name;
    private String Hint;
    private String text;
    private String Unit;
    private String address;

    public ItemBean() {
        super();
    }
    public ItemBean(String Name,String Unit, String text,String Hint ,String address) {
        super();

        this.Name = Name;
        this.Unit=Unit;
        this.Hint=Hint;
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
    public String getUnit() {
        return Unit;
    }

    public void setUnit(String Name) {
        this.Unit = Unit;
    }

    public String getHint() {
        return Hint;
    }

    public void setHint(String Hint) {
        this.Hint = Hint;
    }



    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }


}
