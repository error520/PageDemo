package com.kinco.MotorApp.edittext;


import android.location.Address;

public class Text {

    public String address;

    private String title;

    private String current;

    private String[] content;

    private int id;

    public String getAddress() {

        return address;

    }

    public void setAddress(String address) {

        this.address = address;

    }

    public int getId() {

        return id;

    }

    public void setId(int id) {

        this.id = id;

    }

    public String getTitle() {

        return title;

    }

    public void setTitle(String title) {

        this.title = title;

    }

    public String getCurrent() {

        return current;

    }

    public void setCurrent(String current) {

        this.current = current;

    }

    public String[] getContent() {

        return content;

    }

    public void setContent(String[] content) {

        this.content = content;

    }


}
