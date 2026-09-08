package com.example.tk_demo.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public class AuthorizeRequest {

    /** 長度限制 19 碼、限制英數字（見手冊 I、授權） */
    @NotBlank
    @Size(max = 19)
    @Pattern(regexp = "[A-Za-z0-9]+", message = "orderNo 只能是英數字")
    private String orderNo;

    /** 單位是「元」，例如 1500.00；轉換成 TrustPay 格式統一交給 TrustPayAmount 處理，這裡不要自己乘 100 */
    @NotNull
    @DecimalMin(value = "0.01")
    private BigDecimal amount;

    @Size(max = 40)
    private String orderDesc;

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

    /**
     * 選填：要用哪個商店代號送出這筆交易。
     * 不填就用後端設定檔（application-*.yml 的 hitrust.store-id）的預設值。
     * 注意：toolkit 內部會去 classpath 找對應的 <storeId>.conf（裡面指到該商店的 RSA 私鑰），
     * 所以這裡打任何字串都可以送出去，但除非後端已經放了那個 StoreID 的 .conf + 金鑰檔，
     * 不然 toolkit 會直接丟例外（會被 GlobalExceptionHandler 接住變成 500）。
     */
    @Pattern(regexp = "[A-Za-z0-9]*", message = "storeId 只能是英數字")
    private String storeId;

    public String getStoreId() {
        return storeId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }
}
