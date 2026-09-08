package com.example.tk_demo.controller;

import com.example.tk_demo.dto.AuthorizeRequest;
import com.example.tk_demo.dto.AuthorizeResponse;
import com.example.tk_demo.dto.BindCardRequest;
import com.example.tk_demo.dto.BindCardResponse;
import com.example.tk_demo.dto.response.OrderResult;
import com.example.tk_demo.dto.response.SipQueryResult;
import com.example.tk_demo.service.CreditCardPaymentService;
import jakarta.validation.Valid;


import org.springframework.web.bind.annotation.*;
import com.example.tk_demo.dto.CaptureRequest;
import com.example.tk_demo.dto.CaptureResponse;
import com.example.tk_demo.dto.VoidAuthorizeRequest;
import com.example.tk_demo.dto.VoidCaptureRequest;
import com.example.tk_demo.dto.RefundRequest;
import com.example.tk_demo.dto.SipAuthorizeRequest;
import com.example.tk_demo.dto.SipCancelRequest;
import com.example.tk_demo.dto.SipCancelResponse;
import com.example.tk_demo.dto.VoidRefundRequest;
import com.example.tk_demo.dto.DirectAuthorizeRequest;
import com.example.tk_demo.dto.DirectAuthorizeTrxTokenRequest;
import com.example.tk_demo.dto.FollowPayTokenRequest;
import com.example.tk_demo.dto.FollowPayTokenResponse;
import com.example.tk_demo.dto.AuthorizeTrxTokenRequest;
import com.example.tk_demo.dto.ApplePayChargeRequest;
import com.example.tk_demo.dto.ApplePayChargeResponse;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {

    private final CreditCardPaymentService paymentService;

    public PaymentController(CreditCardPaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/authorize")
    public AuthorizeResponse authorize(@Valid @RequestBody AuthorizeRequest request) throws Exception {
        return paymentService.authorize(request);
    }

    @PostMapping("/capture")
    public CaptureResponse capture(@Valid @RequestBody CaptureRequest request) throws Exception {
        return paymentService.capture(request);
    }

    /** 取消授權：僅適用於尚未請款的訂單，回傳跟查詢一樣的完整訂單明細 */
    @PostMapping("/void-authorize")
    public OrderResult voidAuthorize(@Valid @RequestBody VoidAuthorizeRequest request) throws Exception {
        return paymentService.voidAuthorize(request);
    }

    /**
     * 前端結果頁靠這支拿訂單真正的狀態（retCode、authCode…等），
     * 不要依賴 Return 導頁 URL 上的參數，也不用等 Update 非同步通知才能顯示結果。
     */
    @GetMapping("/orders/{orderNo}")
    public OrderResult getOrder(@PathVariable String orderNo,
            @RequestParam(required = false) String storeId) throws Exception {
        return paymentService.query(orderNo, storeId);
    }

    /** 取消請款：僅適用於已請款、尚未清算的訂單 */
    @PostMapping("/void-capture")
    public OrderResult voidCapture(@Valid @RequestBody VoidCaptureRequest request) throws Exception {
        return paymentService.voidCapture(request);
    }

    /** 退款：僅適用於請款已清算的訂單 */
    @PostMapping("/refund")
    public OrderResult refund(@Valid @RequestBody RefundRequest request) throws Exception {
        return paymentService.refund(request);
    }

    /** 取消退款：僅適用於已退款、尚未清算的訂單 */
    @PostMapping("/void-refund")
    public OrderResult voidRefund(@Valid @RequestBody VoidRefundRequest request) throws Exception {
        return paymentService.voidRefund(request);
    }

    /** 直接授權：卡號/有效期限直接在商家頁面收集，不導頁，一次呼叫拿到結果 */
    @PostMapping("/direct-authorize")
    public OrderResult directAuthorize(@Valid @RequestBody DirectAuthorizeRequest request) throws Exception {
        return paymentService.directAuthorize(request);
    }

    /** 綁卡：走真實授權金額，成功後導頁刷卡；trxToken 要等使用者導回後查詢才會拿到 */
    @PostMapping("/bind-card")
    public BindCardResponse bindCard(@Valid @RequestBody BindCardRequest request) throws Exception {
        return paymentService.bindCard(request);
    }

    /** 直接授權含交易序號標記：用綁卡取得的 trxToken + expiry 代表卡片，不用卡號，一次呼叫拿到結果 */
    @PostMapping("/direct-authorize-trxtoken")
    public OrderResult directAuthorizeTrxToken(@Valid @RequestBody DirectAuthorizeTrxTokenRequest request)
            throws Exception {
        return paymentService.directAuthorizeTrxToken(request);
    }

    /** 授權含交易序號標記：用 trxToken 代表卡片，導頁流程（支援 3DS），跟 authorize 一樣要等前端查詢才知道真正結果 */
    @PostMapping("/authorize-trxtoken")
    public AuthorizeResponse authorizeTrxToken(@Valid @RequestBody AuthorizeTrxTokenRequest request) throws Exception {
        return paymentService.authorizeTrxToken(request);
    }

    @PostMapping("/sip-authorize")
    public AuthorizeResponse createSipOrder(@Valid @RequestBody SipAuthorizeRequest request) throws Exception {
        return paymentService.createSipOrder(request);
    }

    /** 查詢定期定額狀態：帶「主訂單」編號，回傳首期+各期扣款明細 */
    @GetMapping("/sip-orders/{orderNo}")
    public SipQueryResult querySip(@PathVariable String orderNo,
            @RequestParam(required = false) String storeId) throws Exception {
        return paymentService.querySip(orderNo, storeId);
    }

    /** 取消定期定額：orderNo 必須是主訂單編號 */
    @PostMapping("/sip-cancel")
    public SipCancelResponse cancelSip(@Valid @RequestBody SipCancelRequest request) throws Exception {
        return paymentService.cancelSip(request);
    }

    @PostMapping("/follow-pay-token")
    public FollowPayTokenResponse createFollowPayToken(@Valid @RequestBody FollowPayTokenRequest request)
            throws Exception {
        return paymentService.createFollowPayToken(request);
    }


    @PostMapping("/apple-pay/charge")
    public ApplePayChargeResponse chargeApplePay(@Valid @RequestBody ApplePayChargeRequest request) throws Exception {
        return paymentService.payWithApplePay(request);
    }

}