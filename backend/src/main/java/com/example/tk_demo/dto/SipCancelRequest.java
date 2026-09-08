package com.example.tk_demo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * 取消定期定額（SIP_CANCEL）。手冊 IX、定期定額訂單取消 確認：
 * - 函式共用 B2CPayOther，交易類別 type=33
 * - orderNo 必須是「主訂單」編號，取消後就不會再依週期自動扣款
 * - 回應只有 retCode，沒有其他明細欄位可查
 */
public class SipCancelRequest {

    @NotBlank
    @Size(max = 19)
    @Pattern(regexp = "[A-Za-z0-9]+", message = "orderNo 只能是英數字")
    private String orderNo;

    @Pattern(regexp = "[A-Za-z0-9]*", message = "storeId 只能是英數字")
    private String storeId;

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getStoreId() {
        return storeId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }
}