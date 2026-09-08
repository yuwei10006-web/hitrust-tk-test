package com.example.tk_demo.dto.response;

public class SipDetail {
    private String periodNumber;
    private String orderNumber;
    /** 00 已扣款 / 01 待扣款 / 02 扣款失敗 / 03 已終止定期定額 */
    private String status;
    private String orderStatus;
    private String estimatedDeductDate;
    /** 只有扣款失敗時才有值 */
    private String trxStatus;

    public String getPeriodNumber() {
        return periodNumber;
    }

    public void setPeriodNumber(String periodNumber) {
        this.periodNumber = periodNumber;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getEstimatedDeductDate() {
        return estimatedDeductDate;
    }

    public void setEstimatedDeductDate(String estimatedDeductDate) {
        this.estimatedDeductDate = estimatedDeductDate;
    }

    public String getTrxStatus() {
        return trxStatus;
    }

    public void setTrxStatus(String trxStatus) {
        this.trxStatus = trxStatus;
    }
}