package com.zou.graytrace.bean;

/**
 * Created by zoujingyi on 2018/2/3.
 */

public class GsonPeopleEventFromDraft {
    private Integer code;
    private String msg;
    private String event_title;
    private String event_text;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getEvent_title() {
        return event_title;
    }

    public void setEvent_title(String event_title) {
        this.event_title = event_title;
    }

    public String getEvent_text() {
        return event_text;
    }

    public void setEvent_text(String event_text) {
        this.event_text = event_text;
    }
}
