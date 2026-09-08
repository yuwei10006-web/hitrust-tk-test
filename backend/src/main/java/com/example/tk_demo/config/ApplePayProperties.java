package com.example.tk_demo.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "apple.pay")
public class ApplePayProperties {

    /** Apple Developer 後台申請的 Merchant ID，例如 merchant.com.yourcompany.tkdemo */
    private String merchantId;

    /** 顯示在 Apple Pay 授權畫面上的商家名稱 */
    private String displayName;

    /** 已完成 Apple 網域驗證的網域（不含 https://），對應 initiativeContext */
    private String domainName;

    /** Merchant Identity Certificate（.p12）路徑，支援 classpath: 前綴 */
    private String certPath;

    private String certPassword;

    public String getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getDomainName() {
        return domainName;
    }

    public void setDomainName(String domainName) {
        this.domainName = domainName;
    }

    public String getCertPath() {
        return certPath;
    }

    public void setCertPath(String certPath) {
        this.certPath = certPath;
    }

    public String getCertPassword() {
        return certPassword;
    }

    public void setCertPassword(String certPassword) {
        this.certPassword = certPassword;
    }
}