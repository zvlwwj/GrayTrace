package com.zou.graytrace.bean;

import java.io.Serializable;

/**
 * Created by zou on 2018/3/20.
 */

public class CommentBean implements Serializable{
    private int comment_id;
    private String text;
    private int uploader_id;
    private String nick_name;
    private String avatar_url;
    private int upvote_count;
    private boolean is_upvote;
    private int reply_id;
    private String type;
    private int type_id = -1;
    private String time_stamp;

    public boolean is_upvote() {
        return is_upvote;
    }

    public void setIs_upvote(boolean is_upvote) {
        this.is_upvote = is_upvote;
    }

    public int getUploader_id() {
        return uploader_id;
    }

    public void setUploader_id(int uploader_id) {
        this.uploader_id = uploader_id;
    }

    public int getComment_id() {
        return comment_id;
    }

    public void setComment_id(int comment_id) {
        this.comment_id = comment_id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getNick_name() {
        return nick_name;
    }

    public void setNick_name(String nick_name) {
        this.nick_name = nick_name;
    }

    public String getAvatar_url() {
        return avatar_url;
    }

    public void setAvatar_url(String avatar_url) {
        this.avatar_url = avatar_url;
    }

    public int getUpvote_count() {
        return upvote_count;
    }

    public void setUpvote_count(int upvote_count) {
        this.upvote_count = upvote_count;
    }

    public int getReply_id() {
        return reply_id;
    }

    public void setReply_id(int reply_id) {
        this.reply_id = reply_id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getType_id() {
        return type_id;
    }

    public void setType_id(int type_id) {
        this.type_id = type_id;
    }

    public String getTime_stamp() {
        return time_stamp;
    }

    public void setTime_stamp(String time_stamp) {
        this.time_stamp = time_stamp;
    }
}
