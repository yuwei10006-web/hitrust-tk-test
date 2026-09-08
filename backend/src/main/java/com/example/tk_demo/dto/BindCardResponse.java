package com.example.tk_demo.dto;

public class BindCardResponse {
    private String retCode;
    private String redirectUrl; // 成功時，前端要導頁的完整網址（去 TrustPay 頁面輸入卡號）

    public BindCardResponse(String retCode, String redirectUrl) {
        this.retCode = retCode;
        this.redirectUrl = redirectUrl;
    }

    public String getRetCode() {
        return retCode;
    }

    public String getRedirectUrl() {
        return redirectUrl;
    }
}