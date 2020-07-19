package com.kinco.kmlink.ParameterItem;


/**
 * 下拉选项的数据类
 */
public class Text {

    public String address;

    private String title;

    private String current;

    private String[] content;

    private int id;

    public Text(String Title, String[] content, String address, int id){
        this.title = Title;
        this.content = content;
        this.address = address;
        this.id = id;
    }

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
