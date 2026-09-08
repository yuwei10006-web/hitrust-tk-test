package com.example.tk_demo.controller.callback;

import com.example.tk_demo.dto.response.OrderResult;
import com.example.tk_demo.service.CreditCardPaymentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PaymentUpdateController {

    private static final Logger log = LoggerFactory.getLogger(PaymentUpdateController.class);

    private final CreditCardPaymentService paymentService;

    public PaymentUpdateController(CreditCardPaymentService paymentService) {
        this.paymentService = paymentService;
    }

    // 舊網址（沒帶 storeId）保留相容：退回設定檔預設商店解密
    @PostMapping(value = "/api/payment/update", produces = MediaType.TEXT_PLAIN_VALUE)
    @ResponseBody
    public String handleUpdateLegacy(@RequestParam("KEY") String key,
            @RequestParam("MAC") String mac,
            @RequestParam("CIPHER") String cipher) {
        return handleUpdate(null, key, mac, cipher);
    }

    @PostMapping(value = "/api/payment/update/{storeId}", produces = MediaType.TEXT_PLAIN_VALUE)
    @ResponseBody
    public String handleUpdate(@PathVariable(required = false) String storeId,
            @RequestParam("KEY") String key,
            @RequestParam("MAC") String mac,
            @RequestParam("CIPHER") String cipher) {
        try {
            OrderResult result = paymentService.decryptUpdate(storeId, key, mac, cipher);

            // TODO: 這裡之後接資料庫 —— 依 result.getOrderNo() 更新訂單狀態
            log.info("訂單更新: orderNo={}, retCode={}, approveAmount={}",
                    result.getOrderNo(), result.getRetCode(), result.getApproveAmount());

        } catch (Exception e) {
            log.error("解密 Update 通知失敗", e);
            // 就算解密失敗，還是要回 R01=00，避免 TrustPay 誤判成功而一直重送；
            // 真正的失敗要靠你自己的 log/alert 機制去追，不是靠不回應這招
        }

        // 這行絕對不能省，TrustPay 沒收到這個純文字回應會判定失敗、重複發送通知
        return "R01=00";
    }
}