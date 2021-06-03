package com.example.facebookmini.model;

import com.google.firebase.database.Exclude;

public class User {
    private String IDUser;
    private String userName, password, fullName, email;
    private String images;


    public User(String userName, String password, String fullName, String email, String images) {
        this.userName = userName;
        this.fullName = fullName;
        this.email = email;
        this.images = images;
        this.password=password;
    }
    public User(String IDUser,String userName, String password, String fullName, String email, String images) {
        this.userName = userName;
        this.fullName = fullName;
        this.email = email;
        this.images = images;
        this.password=password;
        this.IDUser=IDUser;
    }
    public User() {
    }
    public String getIDUser() {
        return IDUser;
    }
    public void setIDUser(String IDUser) {
        this.IDUser = IDUser;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String address) {
        this.email = address;
    }


    public String getImages() {
        return images;
    }

    public void setImages(String images) {
        this.images = images;
    }


}
