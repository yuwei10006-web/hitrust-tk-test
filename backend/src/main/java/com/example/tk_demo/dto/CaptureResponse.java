package com.example.tk_demo.dto;

public class CaptureResponse {
    private String retCode;
    private String captureAmount;
    private String captureDate;

    public CaptureResponse(String retCode, String captureAmount, String captureDate) {
        this.retCode = retCode;
        this.captureAmount = captureAmount;
        this.captureDate = captureDate;
    }
    public String getRetCode() { return retCode; }
    public String getCaptureAmount() { return captureAmount; }
    public String getCaptureDate() { return captureDate; }
}