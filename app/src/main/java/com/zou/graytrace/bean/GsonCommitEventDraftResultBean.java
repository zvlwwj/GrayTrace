package com.zou.graytrace.bean;

/**
 * Created by zou on 2018/2/2.
 */

public class GsonCommitEventDraftResultBean {
    private Integer code;
    private String msg;
    private String draft_people_event_id;

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

    public String getDraft_people_event_id() {
        return draft_people_event_id;
    }

    public void setDraft_people_event_id(String draft_people_event_id) {
        this.draft_people_event_id = draft_people_event_id;
    }
}
