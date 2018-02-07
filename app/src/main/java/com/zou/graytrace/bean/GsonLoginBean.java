package com.zou.graytrace.bean;

/**
 * Created by zou on 2018/1/29.
 */

public class GsonLoginBean {
    private Integer code;
    private String msg;
    private Integer user_id;

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

    public Integer getUser_id() {
        return user_id;
    }

    public void setUser_id(Integer user_id) {
        this.user_id = user_id;
    }
}
