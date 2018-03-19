package com.zou.graytrace.bean;

/**
 * Created by zou on 2018/2/2.
 */

public class GsonCommitCommentResultBean {
    private Integer code;
    private String msg;
    private String comment_id;

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

    public String getComment_id() {
        return comment_id;
    }

    public void setComment_id(String comment_id) {
        this.comment_id = comment_id;
    }
}
