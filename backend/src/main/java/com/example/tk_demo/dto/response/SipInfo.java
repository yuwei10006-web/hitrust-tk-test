package com.example.tk_demo.dto.response;

public class SipInfo {
    private String createTime;
    private String periodType; // D 日 / M 月 / Y 年
    private String deductFreq; // 扣款頻率
    private String deductTotalNum; // 扣款總期數
    private String deductChargedNum;// 已扣款期數
    private String maskPan;
    private String expire;

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getPeriodType() {
        return periodType;
    }

    public void setPeriodType(String periodType) {
        this.periodType = periodType;
    }

    public String getDeductFreq() {
        return deductFreq;
    }

    public void setDeductFreq(String deductFreq) {
        this.deductFreq = deductFreq;
    }

    public String getDeductTotalNum() {
        return deductTotalNum;
    }

    public void setDeductTotalNum(String deductTotalNum) {
        this.deductTotalNum = deductTotalNum;
    }

    public String getDeductChargedNum() {
        return deductChargedNum;
    }

    public void setDeductChargedNum(String deductChargedNum) {
        this.deductChargedNum = deductChargedNum;
    }

    public String getMaskPan() {
        return maskPan;
    }

    public void setMaskPan(String maskPan) {
        this.maskPan = maskPan;
    }

    public String getExpire() {
        return expire;
    }

    public void setExpire(String expire) {
        this.expire = expire;
    }
}