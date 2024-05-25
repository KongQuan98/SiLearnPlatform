package com.example.imagepro;

import android.graphics.Bitmap;

import java.util.List;

public class CommunityModel {

    private String postImages;
    private String description;
    private String userID;
    private String userImage;
    private String userOrg;
    private String postTime;
    private String documentID;
    private boolean isExpandable;

    public CommunityModel() {
    }

    public CommunityModel(String postImages, String description, String userID, String userImage, String userOrg, String postTime, String documentID) {
        this.postImages = postImages;
        this.description = description;
        this.userID = userID;
        this.userImage = userImage;
        this.userOrg = userOrg;
        this.postTime = postTime;
        this.documentID = documentID;
        isExpandable = false;
    }

    public String getDocumentID() {
        return documentID;
    }

    public void setDocumentID(String documentID) {
        this.documentID = documentID;
    }


    public String getPostImages() {
        return postImages;
    }

    public void setPostImages(String postImages) {
        this.postImages = postImages;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getUserImage() {
        return userImage;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }

    public String getUserOrg() {
        return userOrg;
    }

    public void setUserOrg(String userOrg) {
        this.userOrg = userOrg;
    }

    public String getPostTime() {
        return postTime;
    }

    public void setPostTime(String postTime) {
        this.postTime = postTime;
    }

    public boolean isExpandable() {
        return isExpandable;
    }

    public void setExpandable(boolean expandable) {
        isExpandable = expandable;
    }
}
