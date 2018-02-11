package com.zou.graytrace.bean;

/**
 * Created by zou on 2018/2/11.
 */

public class GsonGetPeopleResultBean {
    private Integer code;
    private String msg;
    private Info info;
    public class Info {
        private String name;
        private String cover_url;
        private int alive;
        private String nationality;
        private String birthplace;
        private String residence;
        private String grave_place;
        private String birth_day;
        private String death_day;
        private String article_ids;
        private int reputation;
        private String description_id;
        private int comment_count;
        private String motto;
        private String industry;
        private String event_ids;
        private String uploader;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getCover_url() {
            return cover_url;
        }

        public void setCover_url(String cover_url) {
            this.cover_url = cover_url;
        }

        public int getAlive() {
            return alive;
        }

        public void setAlive(int alive) {
            this.alive = alive;
        }

        public String getNationality() {
            return nationality;
        }

        public void setNationality(String nationality) {
            this.nationality = nationality;
        }

        public String getBirthplace() {
            return birthplace;
        }

        public void setBirthplace(String birthplace) {
            this.birthplace = birthplace;
        }

        public String getResidence() {
            return residence;
        }

        public void setResidence(String residence) {
            this.residence = residence;
        }

        public String getGrave_place() {
            return grave_place;
        }

        public void setGrave_place(String grave_place) {
            this.grave_place = grave_place;
        }

        public String getBirth_day() {
            return birth_day;
        }

        public void setBirth_day(String birth_day) {
            this.birth_day = birth_day;
        }

        public String getDeath_day() {
            return death_day;
        }

        public void setDeath_day(String death_day) {
            this.death_day = death_day;
        }

        public String getArticle_ids() {
            return article_ids;
        }

        public void setArticle_ids(String article_ids) {
            this.article_ids = article_ids;
        }

        public int getReputation() {
            return reputation;
        }

        public void setReputation(int reputation) {
            this.reputation = reputation;
        }

        public String getDescription_id() {
            return description_id;
        }

        public void setDescription_id(String description_id) {
            this.description_id = description_id;
        }

        public int getComment_count() {
            return comment_count;
        }

        public void setComment_count(int comment_count) {
            this.comment_count = comment_count;
        }

        public String getMotto() {
            return motto;
        }

        public void setMotto(String motto) {
            this.motto = motto;
        }

        public String getIndustry() {
            return industry;
        }

        public void setIndustry(String industry) {
            this.industry = industry;
        }

        public String getEvent_ids() {
            return event_ids;
        }

        public void setEvent_ids(String event_ids) {
            this.event_ids = event_ids;
        }

        public String getUploader() {
            return uploader;
        }

        public void setUploader(String uploader) {
            this.uploader = uploader;
        }
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

    public Info getInfo() {
        return info;
    }

    public void setInfo(Info info) {
        this.info = info;
    }
}
