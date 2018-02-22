package com.zou.graytrace.bean;

import java.util.ArrayList;

/**
 * Created by zou on 2018/2/22.
 */

public class GsonGetDraftPeopleResultBean {
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
        private Description description;
        private String motto;
        private String industry;
        private ArrayList<Event> events;
        private String uploader;

        public class Description{
            private String description_id;
            private String description_text;

            public String getDescription_id() {
                return description_id;
            }

            public void setDescription_id(String description_id) {
                this.description_id = description_id;
            }

            public String getDescription_text() {
                return description_text;
            }

            public void setDescription_text(String description_text) {
                this.description_text = description_text;
            }
        }

        public class Event{
            private String event_id;
            private String event_title;
            private String event_text;

            public String getEvent_id() {
                return event_id;
            }

            public void setEvent_id(String event_id) {
                this.event_id = event_id;
            }

            public String getEvent_title() {
                return event_title;
            }

            public void setEvent_title(String event_title) {
                this.event_title = event_title;
            }

            public String getEvent_text() {
                return event_text;
            }

            public void setEvent_text(String event_text) {
                this.event_text = event_text;
            }
        }

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

        public Description getDescription() {
            return description;
        }

        public void setDescription(Description description) {
            this.description = description;
        }

        public ArrayList<Event> getEvents() {
            return events;
        }

        public void setEvents(ArrayList<Event> events) {
            this.events = events;
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
