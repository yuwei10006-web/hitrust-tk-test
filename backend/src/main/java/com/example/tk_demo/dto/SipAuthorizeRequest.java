package com.example.tk_demo.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public class SipAuthorizeRequest {

    /** 長度限制 19 碼、限制英數字（見手冊 I、授權） */
    @NotBlank
    @Size(max = 19)
    @Pattern(regexp = "[A-Za-z0-9]+", message = "orderNo 只能是英數字")
    private String orderNo;

    /** 每期扣款金額，單位是「元」 */
    @NotNull
    @DecimalMin(value = "0.01")
    private BigDecimal amount;

    @Size(max = 40)
    private String orderDesc;

    /** 扣款期數（E56）。手冊備註：此欄位設定時 E57、E58 不可為空 */
    @NotBlank
    @Size(max = 4)
    @Pattern(regexp = "[0-9]+", message = "periodCount 只能是數字")
    private String periodCount;

    /** 扣款週期數值（E57），例如每 1 個月一期就填 "1" */
    @NotBlank
    @Size(max = 4)
    @Pattern(regexp = "[0-9]+", message = "periodCycle 只能是數字")
    private String periodCycle;

    /** 扣款週期種類（E58）：D 天 / W 週 / M 月 / Y 年 */
    @NotBlank
    @Pattern(regexp = "[DWMY]", message = "periodUnit 只能是 D/W/M/Y")
    private String periodUnit;

    @Pattern(regexp = "[A-Za-z0-9]*", message = "storeId 只能是英數字")
    private String storeId;

    // getters / setters
    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getOrderDesc() {
        return orderDesc;
    }

    public void setOrderDesc(String orderDesc) {
        this.orderDesc = orderDesc;
    }

    public String getPeriodCount() {
        return periodCount;
    }

    public void setPeriodCount(String periodCount) {
        this.periodCount = periodCount;
    }

    public String getPeriodCycle() {
        return periodCycle;
    }

    public void setPeriodCycle(String periodCycle) {
        this.periodCycle = periodCycle;
    }

    public String getPeriodUnit() {
        return periodUnit;
    }

    public void setPeriodUnit(String periodUnit) {
        this.periodUnit = periodUnit;
    }

    public String getStoreId() {
        return storeId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }
}