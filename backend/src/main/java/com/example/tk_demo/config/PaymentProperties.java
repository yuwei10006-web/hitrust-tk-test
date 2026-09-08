package com.example.tk_demo.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "hitrust")
public class PaymentProperties {

    /** HiTRUSTpay 分配的商店代號，同時對應 classpath 上的 <storeId>.conf */
    private String storeId;

    /** 交易完成後，導回商家頁面的網址（前端頁面） */
    private String returnUrl;

    /** TrustPay 非同步回傳交易結果用的網址（要是這台後端可被外部打到的位置） */
    private String updateUrl;

    private String frontendResultUrl; // 例如 http://localhost:5173/order/result

    // getters/setters
    public String getStoreId() {
        return storeId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }

    public String getReturnUrl() {
        return returnUrl;
    }

    public void setReturnUrl(String returnUrl) {
        this.returnUrl = returnUrl;
    }

    public String getUpdateUrl() {
        return updateUrl;
    }

    public void setUpdateUrl(String updateUrl) {
        this.updateUrl = updateUrl;
    }

    public String getFrontendResultUrl() { return frontendResultUrl; }

    public void setFrontendResultUrl(String v) { this.frontendResultUrl = v; }
}