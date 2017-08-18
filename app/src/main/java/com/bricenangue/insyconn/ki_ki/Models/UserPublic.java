package com.bricenangue.insyconn.ki_ki.Models;

import java.util.Map;

import retrofit2.http.Url;

/**
 * Created by bricenangue on 06/12/2016.
 */

public class UserPublic {
    private String name;
    private String email;

    private String chatId;
    private String uniquefirebasebId;
    private String profilePhotoUri;
    private Map<String,Url> userLinks;
    private String gender;


    private long onlineSince;

    public UserPublic() {


    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }



    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public String getUniquefirebasebId() {
        return uniquefirebasebId;
    }

    public void setUniquefirebasebId(String uniquefirebasebId) {
        this.uniquefirebasebId = uniquefirebasebId;
    }

    public String getProfilePhotoUri() {
        return profilePhotoUri;
    }

    public void setProfilePhotoUri(String profilePhotoUri) {
        this.profilePhotoUri = profilePhotoUri;
    }

    public Map<String, Url> getUserLinks() {
        return userLinks;
    }

    public void setUserLinks(Map<String, Url> userLinks) {
        this.userLinks = userLinks;
    }


    public long getOnlineSince() {
        return onlineSince;
    }

    public void setOnlineSince(long onlineSince) {
        this.onlineSince = onlineSince;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }
}

