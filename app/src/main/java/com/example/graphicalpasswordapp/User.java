package com.example.graphicalpasswordapp;

import java.io.Serializable;

public class User implements Serializable {
    private String username, email;
    private String firstPassword, secondPassword, thirdPassword;
    private Boolean isAutoRefresh;

    public User(String username, String email, String firstPassword, String secondPassword, String thirdPassword, boolean isAutoRefresh) {
        this.username = username;
        this.email = email;
        this.firstPassword = firstPassword;
        this.secondPassword = secondPassword;
        this.thirdPassword = thirdPassword;
        this.isAutoRefresh = isAutoRefresh;
    }

    public Boolean getAutoRefresh() {
        return isAutoRefresh;
    }

    public void setAutoRefresh(Boolean autoRefresh) {
        isAutoRefresh = autoRefresh;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstPassword() {
        return firstPassword;
    }

    public void setFirstPassword(String firstPassword) {
        this.firstPassword = firstPassword;
    }

    public String getSecondPassword() {
        return secondPassword;
    }

    public void setSecondPassword(String secondPassword) {
        this.secondPassword = secondPassword;
    }

    public String getThirdPassword() {
        return thirdPassword;
    }

    public void setThirdPassword(String thirdPassword) {
        this.thirdPassword = thirdPassword;
    }

    @Override
    public String toString() {
        return username;
    }

}
