package com.example.tk_demo.dto.response;

public class OrderResult {
    private String orderNo;
    private String retCode;
    private String currency;
    private String orderDate;
    private String orderStatus;
    private String approveAmount;
    private String authCode;
    private String authRRN;
    private String captureAmount;
    private String captureDate;
    private String refundAmount;
    private String refundBatch;
    private String refundRRN;
    private String refundCode;
    private String refundDate;
    private String acquirer;
    private String eci;
    private String trxToken;
    private String expiry;
    private String lastBindCardTime;

    // getters/setters
    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String v) {
        this.orderNo = v;
    }

    public String getRetCode() {
        return retCode;
    }

    public void setRetCode(String v) {
        this.retCode = v;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String v) {
        this.currency = v;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String v) {
        this.orderDate = v;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String v) {
        this.orderStatus = v;
    }

    public String getApproveAmount() {
        return approveAmount;
    }

    public void setApproveAmount(String v) {
        this.approveAmount = v;
    }

    public String getAuthCode() {
        return authCode;
    }

    public void setAuthCode(String v) {
        this.authCode = v;
    }

    public String getAuthRRN() {
        return authRRN;
    }

    public void setAuthRRN(String v) {
        this.authRRN = v;
    }

    public String getCaptureAmount() {
        return captureAmount;
    }

    public void setCaptureAmount(String v) {
        this.captureAmount = v;
    }

    public String getCaptureDate() {
        return captureDate;
    }

    public void setCaptureDate(String v) {
        this.captureDate = v;
    }

    public String getRefundAmount() {
        return refundAmount;
    }

    public void setRefundAmount(String v) {
        this.refundAmount = v;
    }

    public String getRefundBatch() {
        return refundBatch;
    }

    public void setRefundBatch(String v) {
        this.refundBatch = v;
    }

    public String getRefundRRN() {
        return refundRRN;
    }

    public void setRefundRRN(String v) {
        this.refundRRN = v;
    }

    public String getRefundCode() {
        return refundCode;
    }

    public void setRefundCode(String v) {
        this.refundCode = v;
    }

    public String getRefundDate() {
        return refundDate;
    }

    public void setRefundDate(String v) {
        this.refundDate = v;
    }

    public String getAcquirer() {
        return acquirer;
    }

    public void setAcquirer(String v) {
        this.acquirer = v;
    }

    public String getEci() {
        return eci;
    }

    public void setEci(String v) {
        this.eci = v;
    }

    public String getTrxToken() {
        return trxToken;
    }

    public void setTrxToken(String v) {
        this.trxToken = v;
    }

    public String getExpiry() {
        return expiry;
    }

    public void setExpiry(String v) {
        this.expiry = v;
    }

    public String getLastBindCardTime() {
        return lastBindCardTime;
    }

    public void setLastBindCardTime(String v) {
        this.lastBindCardTime = v;
    }
}