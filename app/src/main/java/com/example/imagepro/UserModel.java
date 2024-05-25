package com.example.imagepro;

import java.util.ArrayList;

public class UserModel {

    private String email;
    private String fName;
    private String organization;
    private String profileImages;
    private String userType;
    private String userID;

    public UserModel(String email, String fName, String organization, String profileImages, String userType, String userID) {
        this.email = email;
        this.fName = fName;
        this.organization = organization;
        this.profileImages = profileImages;
        this.userType = userType;
        this.userID = userID;
    }

    public UserModel() {
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getfName() {
        return fName;
    }

    public void setfName(String fName) {
        this.fName = fName;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public String getProfileImages() {
        return profileImages;
    }

    public void setProfileImages(String profImages) {
        this.profileImages = profImages;
    }

    public String getUsertype() {
        return userType;
    }

    public void setUsertype(String usertype) {
        this.userType = usertype;
    }


}
