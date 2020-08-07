package com.example.dough.model;

public class User {
    private String displayName;
    private String email;
    private String uId;

    public User(String uId, String displayName, String email) {
        this.displayName = displayName;
        this.email = email;
        this.uId = uId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getuId() {
        return uId;
    }

    public void setuId(String uId) {
        this.uId = uId;
    }
}
