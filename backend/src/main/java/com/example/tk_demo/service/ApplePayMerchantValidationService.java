package com.example.tk_demo.service;

import com.example.tk_demo.config.ApplePayProperties;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.security.KeyStore;
import java.time.Duration;

/**
 * 呼叫 Apple 的 paymentSession endpoint 完成商家驗證。
 * 用 Merchant Identity Certificate（.p12）做 mutual TLS，這是 Apple Pay Web 規格要求的方式，
 * 跟 HiTRUST 完全無關。回傳的 JSON 原封不動交回前端的 session.completeMerchantValidation()。
 */
@Service
public class ApplePayMerchantValidationService {

    private final ApplePayProperties props;
    private final ResourceLoader resourceLoader = new DefaultResourceLoader();
    private volatile HttpClient cachedClient;

    public ApplePayMerchantValidationService(ApplePayProperties props) {
        this.props = props;
    }

    public String validateMerchant(String validationUrl) throws Exception {
        HttpClient client = getOrBuildClient();

        String body = String.format(
                "{\"merchantIdentifier\":\"%s\",\"displayName\":\"%s\",\"initiative\":\"web\",\"initiativeContext\":\"%s\"}",
                props.getMerchantId(), props.getDisplayName(), props.getDomainName());

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(validationUrl))
                .timeout(Duration.ofSeconds(15))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() / 100 != 2) {
            throw new IllegalStateException(
                    "Apple 商家驗證失敗，HTTP " + response.statusCode() + ": " + response.body());
        }
        return response.body();
    }

    private synchronized HttpClient getOrBuildClient() throws Exception {
        if (cachedClient != null) {
            return cachedClient;
        }

        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        Resource certResource = resourceLoader.getResource(props.getCertPath());
        try (InputStream in = certResource.getInputStream()) {
            keyStore.load(in, props.getCertPassword().toCharArray());
        }

        KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        kmf.init(keyStore, props.getCertPassword().toCharArray());

        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(kmf.getKeyManagers(), null, null);

        cachedClient = HttpClient.newBuilder()
                .sslContext(sslContext)
                .connectTimeout(Duration.ofSeconds(10))
                .build();
        return cachedClient;
    }
}