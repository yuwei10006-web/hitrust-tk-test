package com.example.tk_demo.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

/**
 * 取消授權（Authorization Reverse）。手冊 III、取消授權/取消請款/取消退款 確認：
 * - 函式共用 B2CPayOther，交易類別 type=8（RE_AUTH）
 * - amount 預設不需要，但若收單銀行為台北富邦，取消授權「必須」填入金額，
 * 所以這裡設計成選填，由呼叫端視情況帶入
 */
public class VoidAuthorizeRequest {

    @NotBlank
    @Size(max = 19)
    @Pattern(regexp = "[A-Za-z0-9]+", message = "orderNo 只能是英數字")
    private String orderNo;

    /** 選填：僅收單銀行為台北富邦時才需要帶金額（元） */
    @DecimalMin(value = "0.01")
    private BigDecimal amount;

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

    @Pattern(regexp = "[A-Za-z0-9]*", message = "storeId 只能是英數字")
    private String storeId;

    public String getStoreId() {
        return storeId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }
}
