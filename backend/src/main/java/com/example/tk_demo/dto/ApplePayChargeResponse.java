package com.example.tk_demo.dto;

public class ApplePayChargeResponse {
    private String retCode;

    public ApplePayChargeResponse(String retCode) {
        this.retCode = retCode;
    }

    public String getRetCode() {
        return retCode;
    }
}