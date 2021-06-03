package com.example.facebookmini.model;

public class Story {
    String imgUrl;
    long timeStart;
    long timeEnd;
    String storyid;
    String userid;

    public Story() {
    }

    public Story(String imgUrl, long timeStart, long timeEnd, String storyid, String userid) {
        this.imgUrl = imgUrl;
        this.timeStart = timeStart;
        this.timeEnd = timeEnd;
        this.storyid = storyid;
        this.userid = userid;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public long getTimeStart() {
        return timeStart;
    }

    public void setTimeStart(long timeStart) {
        this.timeStart = timeStart;
    }

    public long getTimeEnd() {
        return timeEnd;
    }

    public void setTimeEnd(long timeEnd) {
        this.timeEnd = timeEnd;
    }

    public String getStoryid() {
        return storyid;
    }

    public void setStoryid(String storyid) {
        this.storyid = storyid;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }
}
