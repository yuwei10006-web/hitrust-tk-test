package com.example.tk_demo.dto;

public class AuthorizeResponse {
    private String retCode;
    private String redirectUrl;   // 成功時，前端要導頁的完整網址

    public AuthorizeResponse(String retCode, String redirectUrl) {
        this.retCode = retCode;
        this.redirectUrl = redirectUrl;
    }
    public String getRetCode() { return retCode; }
    public String getRedirectUrl() { return redirectUrl; }
}