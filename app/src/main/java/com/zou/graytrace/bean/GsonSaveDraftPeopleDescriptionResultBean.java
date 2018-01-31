package com.zou.graytrace.bean;

/**
 * Created by zou on 2018/1/31.
 */

public class GsonSaveDraftPeopleDescriptionResultBean {
    private Integer code;
    private String msg;
    private String draft_people_description_id;

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

    public String getDraft_people_description_id() {
        return draft_people_description_id;
    }

    public void setDraft_people_description_id(String draft_people_description_id) {
        this.draft_people_description_id = draft_people_description_id;
    }
}
