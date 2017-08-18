package com.bricenangue.insyconn.ki_ki.Models;

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
    private String postUniqueId;

    public Post() {
    }

    public String getText() {
        return text;
    }

    public Post(String text, String pictureUrl, long timeofCreation, String creator_id, String creator_name, String create_pic_URL, String postUniqueId) {
        this.text = text;
        this.pictureUrl = pictureUrl;
        this.timeofCreation = timeofCreation;
        this.creator_id = creator_id;
        this.creator_name = creator_name;
        this.create_pic_URL = create_pic_URL;
        this.postUniqueId = postUniqueId;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getPictureUrl() {
        return pictureUrl;
    }

    public void setPictureUrl(String pictureUrl) {
        this.pictureUrl = pictureUrl;
    }

    public long getTimeofCreation() {
        return timeofCreation;
    }

    public void setTimeofCreation(long timeofCreation) {
        this.timeofCreation = timeofCreation;
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

    public String getCreate_pic_URL() {
        return create_pic_URL;
    }

    public void setCreate_pic_URL(String create_pic_URL) {
        this.create_pic_URL = create_pic_URL;
    }

    public String getPostUniqueId() {
        return postUniqueId;
    }

    public void setPostUniqueId(String postUniqueId) {
        this.postUniqueId = postUniqueId;
    }
}
