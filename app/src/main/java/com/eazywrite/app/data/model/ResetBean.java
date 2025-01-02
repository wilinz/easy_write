package com.eazywrite.app.data.model;

import android.util.Log;

public class ResetBean {

    /**
     * code
     */
    private String code;
    /**
     * username
     */
    private String username;
    /**
     * newPassword
     */
    private String newPassword;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public void getAll() {
        Log.d("HelloWorld", "username:"+username+" newPassword:"+newPassword+" code:"+code);
    }
}
