package com.example.pagedemo.edittext;

/**
 * Created by zengd on 2016/8/17 0017.
 */
public class ItemBean {
    private String Name;
    private String Current;
    private String text;

    public ItemBean() {
        super();
    }
    public ItemBean(String Name,String Current, String text) {
        super();

        this.Name = Name;
        this.Current=Current;
        this.text=text;

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
