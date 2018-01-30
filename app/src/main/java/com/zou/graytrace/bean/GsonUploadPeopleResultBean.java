package com.zou.graytrace.bean;

/**
 * Created by zou on 2018/1/30.
 */

public class GsonUploadPeopleResultBean {
    private Integer code;
    private String msg;
    private String people_id;

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

    public String getPeople_id() {
        return people_id;
    }

    public void setPeople_id(String people_id) {
        this.people_id = people_id;
    }
}
