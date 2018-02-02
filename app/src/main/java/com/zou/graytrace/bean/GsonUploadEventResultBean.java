package com.zou.graytrace.bean;

/**
 * Created by zou on 2018/2/2.
 */

public class GsonUploadEventResultBean {
    private Integer code;
    private String msg;
    private String people_event_id;

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

    public String getPeople_event_id() {
        return people_event_id;
    }

    public void setPeople_event_id(String people_event_id) {
        this.people_event_id = people_event_id;
    }
}
