package com.example.tk_demo.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

/**
 * 直接授權含交易序號標記（AuthorizationSSLTrxToken）。手冊 VI、直接授權含交易序號標記確認：
 * - 函式：B2CPayAuthSSLTrxToken（跟直接授權 B2CPayAuthSSL 是不同 class，但一樣繼承 B2CPay）
 * - 差異：不用卡號/CVV，改用先前綁卡拿到的 trxToken + expiry 代表卡片
 * - orderDesc、depositFlag、queryFlag 手冊標「必要」，但有設定檔預設值可用，這裡仍設計成可留空
 */
public class DirectAuthorizeTrxTokenRequest {

    @NotBlank
    @Size(max = 19)
    @Pattern(regexp = "[A-Za-z0-9]+", message = "orderNo 只能是英數字")
    private String orderNo;

    @NotNull
    @DecimalMin(value = "0.01")
    private BigDecimal amount;

    @Size(max = 40)
    private String orderDesc;

    /** 綁卡時取得的交易序號 */
    @NotBlank
    private String trxToken;

    /** 有效期限，格式 YYMM，例如 2812 代表 2028 年 12 月（綁卡查詢結果裡的 expiry 原樣帶入即可） */
    @NotBlank
    @Pattern(regexp = "[0-9]{4}", message = "expiry 格式須為 YYMM")
    private String expiry;

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

    public String getTrxToken() {
        return trxToken;
    }

    public void setTrxToken(String trxToken) {
        this.trxToken = trxToken;
    }

    public String getExpiry() {
        return expiry;
    }

    public void setExpiry(String expiry) {
        this.expiry = expiry;
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