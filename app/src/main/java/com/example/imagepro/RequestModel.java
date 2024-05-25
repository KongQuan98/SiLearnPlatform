package com.example.imagepro;

public class RequestModel {

    private String email;
    private String fName;
    private String organization;
    private String password;
    private String profileImages;
    private String request;
    private String requestID;
    private String userType;

    public RequestModel(){
    }

    public RequestModel(String email, String fName, String organization, String password, String profileImages, String request, String requestID, String userType) {
        this.email = email;
        this.fName = fName;
        this.organization = organization;
        this.password = password;
        this.profileImages = profileImages;
        this.request = request;
        this.requestID = requestID;
        this.userType = userType;
    }

    public String getRequestID() {
        return requestID;
    }

    public void setRequestID(String requestID) {
        this.requestID = requestID;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getProfileImages() {
        return profileImages;
    }

    public void setProfileImages(String profileImages) {
        this.profileImages = profileImages;
    }

    public String getRequest() {
        return request;
    }

    public void setRequest(String request) {
        this.request = request;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }
}
