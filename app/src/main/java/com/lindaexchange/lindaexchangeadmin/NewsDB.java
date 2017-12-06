package com.lindaexchange.lindaexchangeadmin;

/**
 * Created by Milion on 11/3/2017.
 */

public class NewsDB {
    private String key;
    private String topic;
    private String detail;
    private String photo;
    private int epoch;

    public NewsDB() {}

    public NewsDB(String key, String topic, String detail, String photo, int epoch) {
        this.key = key;
        this.topic = topic;
        this.detail = detail;
        this.photo = photo;
        this.epoch = epoch;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getTopic() {
        return topic;
    }

    public String getDetail() {
        return detail;
    }

    public String getPhoto() {
        return photo;
    }

    public int getEpoch() {
        return epoch;
    }
}
