package com.zou.graytrace.bean;

/**
 * Created by zou on 2018/2/7.
 */

public class GsonGetAccountInfoResult {
    private Integer code;
    private String msg;
    private Info info;

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

    public Info getInfo() {
        return info;
    }

    public void setInfo(Info info) {
        this.info = info;
    }

    public class Info {
        private String nick_name;
        private String avatar_url;
        private String draft_people_ids;
        private String draft_thing_ids;
        private String draft_event_ids;
        private String people_ids;
        private String thing_ids;
        private String event_ids;
        private String collection_people_ids;
        private String collection_thing_ids;
        private String collection_event_ids;

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

        public String getDraft_people_ids() {
            return draft_people_ids;
        }

        public void setDraft_people_ids(String draft_people_ids) {
            this.draft_people_ids = draft_people_ids;
        }

        public String getDraft_thing_ids() {
            return draft_thing_ids;
        }

        public void setDraft_thing_ids(String draft_thing_ids) {
            this.draft_thing_ids = draft_thing_ids;
        }

        public String getDraft_event_ids() {
            return draft_event_ids;
        }

        public void setDraft_event_ids(String draft_event_ids) {
            this.draft_event_ids = draft_event_ids;
        }

        public String getPeople_ids() {
            return people_ids;
        }

        public void setPeople_ids(String people_ids) {
            this.people_ids = people_ids;
        }

        public String getThing_ids() {
            return thing_ids;
        }

        public void setThing_ids(String thing_ids) {
            this.thing_ids = thing_ids;
        }

        public String getEvent_ids() {
            return event_ids;
        }

        public void setEvent_ids(String event_ids) {
            this.event_ids = event_ids;
        }

        public String getCollection_people_ids() {
            return collection_people_ids;
        }

        public void setCollection_people_ids(String collection_people_ids) {
            this.collection_people_ids = collection_people_ids;
        }

        public String getCollection_thing_ids() {
            return collection_thing_ids;
        }

        public void setCollection_thing_ids(String collection_thing_ids) {
            this.collection_thing_ids = collection_thing_ids;
        }

        public String getCollection_event_ids() {
            return collection_event_ids;
        }

        public void setCollection_event_ids(String collection_event_ids) {
            this.collection_event_ids = collection_event_ids;
        }
    }
}
