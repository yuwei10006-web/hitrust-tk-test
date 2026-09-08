package com.example.tk_demo.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

/**
 * 取消退款（Refund Reverse）。手冊 III、取消授權/取消請款/取消退款 確認：
 * - 函式共用 B2CPayOther，交易類別 type=6（toolkit 常數 RE_REFU）
 * - 使用時機：商家欲對退回貨款取消（訂單須未清算）
 * - 金額手冊未強制必填，跟取消請款一樣設計成選填
 */
public class VoidRefundRequest {

    @NotBlank
    @Size(max = 19)
    @Pattern(regexp = "[A-Za-z0-9]+", message = "orderNo 只能是英數字")
    private String orderNo;

    /** 選填：金額（元） */
    @DecimalMin(value = "0.01")
    private BigDecimal amount;

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

    public String getStoreId() {
        return storeId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }
}