package com.example.tk_demo.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

/**
 * 直接授權（Authorization SSL）。手冊 V、直接授權（AuthorizationSSL）確認：
 * - 函式：B2CPayAuthSSL（跟一般授權 B2CPayAuth 是不同 class）
 * - 差異：卡號/有效期限直接在商家頁面收集，不導去 TrustPay 網頁
 * - orderDesc、depositFlag 手冊標「必要」，但有設定檔預設值可用，這裡仍設計成可留空
 * - CVC2/CVV2（E01）僅在商代有開啟時才必帶，這裡設計成選填
 */
public class DirectAuthorizeRequest {

    @NotBlank
    @Size(max = 19)
    @Pattern(regexp = "[A-Za-z0-9]+", message = "orderNo 只能是英數字")
    private String orderNo;

    @NotNull
    @DecimalMin(value = "0.01")
    private BigDecimal amount;

    @Size(max = 40)
    private String orderDesc;

    /** 卡號（PAN） */
    @NotBlank
    @Pattern(regexp = "[0-9]{12,19}", message = "卡號格式錯誤")
    private String pan;

    /** 有效期限，格式 YYMM，例如 2812 代表 2028 年 12 月 */
    @NotBlank
    @Pattern(regexp = "[0-9]{4}", message = "expiry 格式須為 YYMM")
    private String expiry;

    /** CVC2/CVV2，商代若有開啟則必帶，這裡設計成選填 */
    @Pattern(regexp = "[0-9]{0,4}", message = "cvv 格式錯誤")
    private String cvv;

    /** '1': Sale 交易（自動請款）, '0': 一般交易。留空就用設定檔預設值 */
    @Pattern(regexp = "[01]?", message = "depositFlag 只能是 0 或 1")
    private String depositFlag;

    @Pattern(regexp = "[A-Za-z0-9]*", message = "storeId 只能是英數字")
    private String storeId;

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

    public String getPan() {
        return pan;
    }

    public void setPan(String pan) {
        this.pan = pan;
    }

    public String getExpiry() {
        return expiry;
    }

    public void setExpiry(String expiry) {
        this.expiry = expiry;
    }

    public String getCvv() {
        return cvv;
    }

    public void setCvv(String cvv) {
        this.cvv = cvv;
    }

    public String getDepositFlag() {
        return depositFlag;
    }

    public void setDepositFlag(String depositFlag) {
        this.depositFlag = depositFlag;
    }

    public String getStoreId() {
        return storeId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }
}