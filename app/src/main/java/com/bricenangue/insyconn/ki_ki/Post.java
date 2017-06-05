package com.bricenangue.insyconn.ki_ki;

/**
 * Created by bricenangue on 26/01/2017.
 */

public class Post {
    private String text;
    private String pictureUrl;
    private long timeofCreation;
    private String creator_id;
    private String creator_name;
    private String create_pic_URL;

    public Post() {
    }

    public String getPictureURL() {
        return pictureUrl;
    }

    public void setPictureURL(String pictureURL) {
        this.pictureUrl = pictureURL;
    }

    public String getCreator_id() {
        return creator_id;
    }

    public void setCreator_id(String creator_id) {
        this.creator_id = creator_id;
    }

    public String getCreator_name() {
        return creator_name;
    }

    public void setCreator_name(String creator_name) {
        this.creator_name = creator_name;
    }

    public String getCreator_pic_URL() {
        return create_pic_URL;
    }

    public void setCreator_pic_URL(String creator_pic_URL) {
        this.create_pic_URL = creator_pic_URL;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public long getTimeofCreation() {
        return timeofCreation;
    }

    public void setTimeofCreation(long timeofCreation) {
        this.timeofCreation = timeofCreation;
    }
}
