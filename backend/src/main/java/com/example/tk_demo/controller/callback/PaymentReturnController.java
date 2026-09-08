package com.example.tk_demo.controller.callback;

import com.example.tk_demo.config.PaymentProperties;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * 手冊上的 ReturnURL：TrustPay 導使用者的瀏覽器 GET 回這裡。
 * 這裡只做一件事——把使用者轉交給前端結果頁，orderNo 帶過去就好。
 * 真正的交易結果一律由前端另外打 GET /api/payment/orders/{orderNo} 取得，
 * 不要在這裡直接把 URL 上的 retcode 轉給前端顯示。
 */
@Controller
public class PaymentReturnController {

    private final PaymentProperties props;

    public PaymentReturnController(PaymentProperties props) {
        this.props = props;
    }

    @GetMapping("/api/payment/return")
    public RedirectView handleReturn(@RequestParam String ordernumber,
            @RequestParam(required = false) String merid) {
        UriComponentsBuilder builder = UriComponentsBuilder
                .fromUriString(props.getFrontendResultUrl())
                .queryParam("orderNo", ordernumber);
        if (merid != null && !merid.isBlank()) {
            builder.queryParam("storeId", merid);
        }
        return new RedirectView(builder.toUriString());
    }
}
