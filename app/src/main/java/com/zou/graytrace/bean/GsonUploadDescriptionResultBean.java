package com.zou.graytrace.bean;

/**
 * Created by zou on 2018/1/31.
 */

public class GsonUploadDescriptionResultBean {
    private Integer code;
    private String msg;
    private String people_description_id;

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

    public String getPeople_description_id() {
        return people_description_id;
    }

    public void setPeople_description_id(String people_description_id) {
        this.people_description_id = people_description_id;
    }
}
