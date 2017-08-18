package com.bricenangue.insyconn.ki_ki.Models;

import java.util.Map;

/**
 * Created by bricenangue on 06/12/2016.
 */

public class UserPrivate {
    private Map<String,String> contacts;
    private Map<String, Boolean> chatrooms;
    private long canPromoteNumber;
    private String age;
    private String size;
    private String weight;

    public UserPrivate() {
    }

    public Map<String, String> getContacts() {
        return contacts;
    }

    public void setContacts(Map<String, String> contacts) {
        this.contacts = contacts;
    }

    public Map<String, Boolean> getChatrooms() {
        return chatrooms;
    }

    public void setChatrooms(Map<String, Boolean> chatrooms) {
        this.chatrooms = chatrooms;
    }

    public long getCanPromoteNumber() {
        return canPromoteNumber;
    }

    public void setCanPromoteNumber(long canPromoteNumber) {
        this.canPromoteNumber = canPromoteNumber;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }
}
