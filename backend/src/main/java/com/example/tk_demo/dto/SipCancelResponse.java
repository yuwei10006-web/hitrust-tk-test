package com.example.tk_demo.dto;

public class SipCancelResponse {
    private String retCode;

    public SipCancelResponse(String retCode) {
        this.retCode = retCode;
    }

    public String getRetCode() {
        return retCode;
    }
}