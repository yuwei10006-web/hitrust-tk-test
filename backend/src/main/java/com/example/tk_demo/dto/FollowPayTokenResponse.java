package com.example.tk_demo.dto;

public class FollowPayTokenResponse {
    private String retCode;
    private String token;

    public FollowPayTokenResponse(String retCode, String token) {
        this.retCode = retCode;
        this.token = token;
    }

    public String getRetCode() {
        return retCode;
    }

    public String getToken() {
        return token;
    }
}