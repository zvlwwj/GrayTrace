package com.zou.graytrace.bean;

/**
 * Created by zou on 2018/1/31.
 */

public class GsonUpdateDraftPeopleResultBean {
    private Integer code;
    private String msg;
    private String draft_people_id;

    public String getDraft_people_id() {
        return draft_people_id;
    }

    public void setDraft_people_id(String draft_people_id) {
        this.draft_people_id = draft_people_id;
    }

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
}
