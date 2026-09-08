package com.example.tk_demo.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

/**
 * 取消請款（Capture Reverse）。手冊 III、取消授權/取消請款/取消退款 確認：
 * - 函式共用 B2CPayOther，交易類別 type=4（toolkit 常數 RE_CAPT）
 * - 使用時機：商家針對「已請款訂單」(訂單須未清算)進行取消請款
 * - 金額欄位手冊只註明台北富邦收單「取消授權」必須帶金額，取消請款沒有特別強制，
 * 但保留選填欄位以防個別收單行有同樣要求
 */
public class VoidCaptureRequest {

    @NotBlank
    @Size(max = 19)
    @Pattern(regexp = "[A-Za-z0-9]+", message = "orderNo 只能是英數字")
    private String orderNo;

    /** 選填：金額（元） */
    @DecimalMin(value = "0.01")
    private BigDecimal amount;

    /** 選填：要用哪個商店代號送出這筆交易，說明同 AuthorizeRequest.storeId */
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