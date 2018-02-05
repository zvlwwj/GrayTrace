package com.zou.graytrace.bean;

/**
 * Created by zou on 2018/2/5.
 */

public class PeopleEventText {
    private String title;
    private String tag;
    private String id;

    public PeopleEventText(String title, String tag, String id) {
        this.title = title;
        this.tag = tag;
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
