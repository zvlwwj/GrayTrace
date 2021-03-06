package com.zou.graytrace.bean;

import java.util.List;

/**
 * Created by zou on 2018/2/8.
 */

public class GsonGetCreationPeopleSampleResultBean {
    private Integer code;
    private String msg;
    private List<Info> infos = null;
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

    public List<Info> getInfos() {
        return infos;
    }

    public void setInfos(List<Info> infos) {
        this.infos = infos;
    }

    public class Info {
        private String name;
        private String descriptionText;
        private String coverUrl;
        private String people_id;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDescriptionText() {
            return descriptionText;
        }

        public void setDescriptionText(String descriptionText) {
            this.descriptionText = descriptionText;
        }

        public String getCoverUrl() {
            return coverUrl;
        }

        public void setCoverUrl(String coverUrl) {
            this.coverUrl = coverUrl;
        }

        public String getPeople_id() {
            return people_id;
        }

        public void setPeople_id(String people_id) {
            this.people_id = people_id;
        }
    }
}
