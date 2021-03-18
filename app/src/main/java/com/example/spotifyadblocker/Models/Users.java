package com.example.spotifyadblocker.Models;

public class Users {

    private String userName,phoneNo;

    public Users(String userName, String phoneNo) {
        this.userName = userName;
        this.phoneNo = phoneNo;
    }

    public Users() {
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }
}
