package com.example.tk_demo.service;

import com.example.tk_demo.config.PaymentProperties;
import com.example.tk_demo.dto.AuthorizeRequest;
import com.example.tk_demo.dto.AuthorizeResponse;
import com.example.tk_demo.dto.BindCardRequest;
import com.example.tk_demo.dto.BindCardResponse;
import com.example.tk_demo.dto.response.OrderResult;
import com.example.tk_demo.util.TrustPayAmount;
import com.example.tk_demo.util.TrustPayResult;
import com.hitrust.b2ctoolkit.b2cpay.B2CPayAuth;
import com.hitrust.b2ctoolkit.b2cpay.B2CPayOther;
import com.hitrust.b2ctoolkit.b2cpay.B2CPayUpdate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.example.tk_demo.dto.CaptureRequest;
import com.example.tk_demo.dto.CaptureResponse;
import com.example.tk_demo.dto.VoidAuthorizeRequest;
import com.example.tk_demo.dto.VoidCaptureRequest;
import com.example.tk_demo.dto.RefundRequest;
import com.example.tk_demo.dto.SipAuthorizeRequest;
import com.example.tk_demo.dto.SipCancelRequest;
import com.example.tk_demo.dto.VoidRefundRequest;
import com.hitrust.b2ctoolkit.b2cpay.B2CPay;
import com.hitrust.b2ctoolkit.b2cpay.B2CPayAuthSSL;
import com.hitrust.b2ctoolkit.b2cpay.B2CPayAuthSSLTrxToken;
import com.example.tk_demo.dto.DirectAuthorizeRequest;
import com.example.tk_demo.dto.DirectAuthorizeTrxTokenRequest;
import com.example.tk_demo.dto.FollowPayTokenRequest;
import com.example.tk_demo.dto.FollowPayTokenResponse;
import com.hitrust.b2ctoolkit.b2cpay.B2CPayAuthTrxToken;
import com.example.tk_demo.dto.AuthorizeTrxTokenRequest;
import com.example.tk_demo.dto.response.SipInfo;
import com.example.tk_demo.dto.response.SipDetail;
import com.example.tk_demo.dto.response.SipQueryResult;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import com.example.tk_demo.dto.SipCancelResponse;
import com.example.tk_demo.dto.ApplePayChargeRequest;
import com.example.tk_demo.dto.ApplePayChargeResponse;
import com.hitrust.b2ctoolkit.b2cpay.B2CPayAuthSSLApplePay;

@Service
public class CreditCardPaymentService {

    private static final Logger log = LoggerFactory.getLogger(CreditCardPaymentService.class);

    private final PaymentProperties props;

    public CreditCardPaymentService(PaymentProperties props) {
        this.props = props;
    }

    /**
     * 決定這筆交易要用哪個商店代號：request 有帶就用 request 的，沒帶就退回設定檔預設值。
     * 注意：toolkit 會拿這個值去 classpath 找 <storeId>.conf（裡面指向該商店的 RSA 私鑰），
     * 所以就算這裡放行任何英數字字串，後端沒有對應 .conf + 金鑰檔的話 transaction() 還是會失敗。
     */
    private String resolveStoreId(String requestStoreId) {
        if (requestStoreId != null && !requestStoreId.isBlank()) {
            return requestStoreId.trim();
        }
        return props.getStoreId();
    }

    /**
     * TrustPay 非同步 POST 回 UpdateURL 時只會帶 KEY/MAC/CIPHER，不會帶商店代號，
     * 所以 decryptUpdate() 沒辦法自己判斷該用哪個商店的私鑰解密。
     * 解法：把 storeId 編進 UpdateURL 路徑裡（每個商店各自的回呼網址），
     * TrustPay 打回來的那個路徑本身就告訴我們要用哪把私鑰。
     * 注意：這些網址必須先跟網際威信客服登記白名單（見手冊 Ch.4），
     * 改成 path 帶 storeId 之後，要重新跟他們確認/登記每個商店對應的 UpdateURL。
     */
    private String buildUpdateUrl(String storeId) {
        String base = props.getUpdateUrl();
        String normalizedBase = base.endsWith("/") ? base.substring(0, base.length() - 1) : base;
        return normalizedBase + "/" + storeId;
    }

    /**
     * 統一呼叫 toolkit 的 transaction()，並把完整的請求/回應內容印出來。
     * toolkit 本身就有留這兩個欄位（getRequestMessage / getResponseMessage），
     * 不用自己另外組字串；action 只是拿來在 log 裡標明是哪一種交易。
     * 之後不管加哪一種新的交易（新的 B2CPayXxx），只要改呼叫這支取代 trx.transaction()，
     * 就自動會印出完整 log，不用每個方法自己再寫一次 log.info。
     */
    private <T extends B2CPay> T transact(String action, T trx) throws Exception {
        try {
            trx.getClass().getMethod("transaction").invoke(trx);
        } catch (java.lang.reflect.InvocationTargetException e) {
            Throwable cause = e.getCause();
            throw (cause instanceof Exception) ? (Exception) cause : e;
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("transaction() 反射呼叫失敗: " + trx.getClass().getSimpleName(), e);
        }
        log.info("[{}] orderNo={}, retCode={}\n----- Request -----\n{}\n----- Response -----\n{}",
                action, trx.getOrderNo(), trx.getRetCode(),
                trx.getRequestMessage(), trx.getResponseMessage());
        return trx;
    }

    public AuthorizeResponse authorize(AuthorizeRequest req) throws Exception {
        String trustPayAmount = TrustPayAmount.toTrustPayString(req.getAmount());

        String storeId = resolveStoreId(req.getStoreId());

        B2CPayAuth auth = new B2CPayAuth();
        auth.setStoreId(storeId);
        auth.setOrderNo(req.getOrderNo());
        auth.setOrderDesc(req.getOrderDesc());
        auth.setAmount(trustPayAmount);
        auth.setReturnURL(props.getReturnUrl());
        auth.setUpdateURL(buildUpdateUrl(storeId));
        transact("authorize", auth);

        if (TrustPayResult.isSuccess(auth.getRetCode())) {
            return new AuthorizeResponse(auth.getRetCode(), auth.getToken());
        }
        return new AuthorizeResponse(auth.getRetCode(), null);
    }

    /**
     * 直接授權（Authorization SSL）。手冊 V、直接授權確認：
     * - 函式：B2CPayAuthSSL，跟導頁式授權（B2CPayAuth）是不同 class
     * - 卡號/有效期限/CVV 直接在商家頁面收集後送出，不用導頁，一次呼叫就有結果
     */
    public OrderResult directAuthorize(DirectAuthorizeRequest req) throws Exception {
        String trustPayAmount = TrustPayAmount.toTrustPayString(req.getAmount());

        String storeId = resolveStoreId(req.getStoreId());

        B2CPayAuthSSL auth = new B2CPayAuthSSL();
        auth.setStoreId(storeId);
        auth.setOrderNo(req.getOrderNo());
        auth.setAmount(trustPayAmount);
        if (req.getOrderDesc() != null && !req.getOrderDesc().isBlank()) {
            auth.setOrderDesc(req.getOrderDesc());
        }
        auth.setPan(req.getPan());
        auth.setExpiry(req.getExpiry());
        if (req.getCvv() != null && !req.getCvv().isBlank()) {
            auth.setE01(req.getCvv());
        }
        if (req.getDepositFlag() != null && !req.getDepositFlag().isBlank()) {
            auth.setDepositFlag(req.getDepositFlag());
        }
        auth.setQueryFlag("1");
        auth.setUpdateURL(buildUpdateUrl(storeId));
        transact("directAuthorize", auth);

        return toOrderResult(auth);
    }

    /**
     * 請款。手冊 II、請款（Capture）確認：
     * - 金額欄位是 setAmount(String)，不是 setCaptureAmount（那是回應用的 getter）
     * - 預設只回 retCode，要 setQueryFlag("1") 才會把請款金額/日期等明細一起帶回來
     */
    public CaptureResponse capture(CaptureRequest req) throws Exception {
        String trustPayAmount = TrustPayAmount.toTrustPayString(req.getAmount());

        String storeId = resolveStoreId(req.getStoreId());

        B2CPayOther trx = new B2CPayOther();
        trx.setStoreId(storeId);
        trx.setType(trx.CAPTURE);
        trx.setOrderNo(req.getOrderNo());
        trx.setAmount(trustPayAmount);
        trx.setQueryFlag("1");
        trx.setUpdateURL(buildUpdateUrl(storeId));
        transact("capture", trx);

        return new CaptureResponse(trx.getRetCode(), trx.getCaptureAmount(), trx.getCaptureDate());
    }

    /**
     * 退款。手冊 II、退款（Refund）確認：
     * - 函式共用 B2CPayOther，交易類別 type=5（toolkit 常數 REFUND）
     * - 只能對「請款已清算」的訂單退款
     * - queryFlag 設 "1" 把退款金額/日期等明細一起帶回來
     */
    public OrderResult refund(RefundRequest req) throws Exception {
        String trustPayAmount = TrustPayAmount.toTrustPayString(req.getAmount());

        String storeId = resolveStoreId(req.getStoreId());

        B2CPayOther trx = new B2CPayOther();
        trx.setStoreId(storeId);
        trx.setType(trx.REFUND);
        trx.setOrderNo(req.getOrderNo());
        trx.setAmount(trustPayAmount);
        trx.setQueryFlag("1");
        trx.setUpdateURL(buildUpdateUrl(storeId));
        transact("refund", trx);

        return toOrderResult(trx);
    }

    /**
     * 取消授權。手冊 III、取消授權（Authorization Reverse）確認：
     * - 函式共用 B2CPayOther，交易類別 type=8（toolkit 常數 RE_AUTH，位於父類別 B2CPay）
     * - 只能對「未請款」的訂單取消授權
     * - 金額預設不用填；*若收單銀行為台北富邦時，取消授權必須填入金額*，所以有帶就送、沒帶就不送
     * - queryFlag 設 "1" 把明細一起帶回來，跟 capture/query 作法一致
     */
    public OrderResult voidAuthorize(VoidAuthorizeRequest req) throws Exception {
        String storeId = resolveStoreId(req.getStoreId());

        B2CPayOther trx = new B2CPayOther();
        trx.setStoreId(storeId);
        trx.setType(trx.RE_AUTH);
        trx.setOrderNo(req.getOrderNo());
        if (req.getAmount() != null) {
            trx.setAmount(TrustPayAmount.toTrustPayString(req.getAmount()));
        }
        trx.setQueryFlag("1");
        trx.setUpdateURL(buildUpdateUrl(storeId));
        transact("voidAuthorize", trx);

        return toOrderResult(trx);
    }

    /**
     * 取消請款。手冊 III、取消請款（Capture Reverse）確認：
     * - 函式共用 B2CPayOther，交易類別 type=4（toolkit 常數 RE_CAPT，位於父類別 B2CPay）
     * - 只能對「已請款、但尚未清算」的訂單取消請款
     * - 金額手冊沒特別要求必填，跟取消授權一樣做成選填欄位
     */
    public OrderResult voidCapture(VoidCaptureRequest req) throws Exception {
        String storeId = resolveStoreId(req.getStoreId());

        B2CPayOther trx = new B2CPayOther();
        trx.setStoreId(storeId);
        trx.setType(trx.RE_CAPT);
        trx.setOrderNo(req.getOrderNo());
        if (req.getAmount() != null) {
            trx.setAmount(TrustPayAmount.toTrustPayString(req.getAmount()));
        }
        trx.setQueryFlag("1");
        trx.setUpdateURL(buildUpdateUrl(storeId));
        transact("voidCapture", trx);

        return toOrderResult(trx);
    }

    /** 用訂單編號查詢完整交易明細，Return 導頁、之後的請款/退款查詢都會共用這支 */
    public OrderResult query(String orderNo, String storeId) throws Exception {
        B2CPayOther trx = new B2CPayOther();
        trx.setStoreId(resolveStoreId(storeId));
        trx.setType(trx.QUERY);
        trx.setOrderNo(orderNo);
        trx.setE55("1");
        transact("query", trx);

        return toOrderResult(trx);
    }

    /**
     * 解密 TrustPay 非同步回傳的 KEY/MAC/CIPHER，回傳真正的交易結果。
     * storeId 來自 UpdateURL 的路徑（見 buildUpdateUrl），不是來自 TrustPay 的參數——
     * 因為 TrustPay 只會 POST KEY/MAC/CIPHER 三個欄位，沒有商店代號，
     * 必須靠回呼網址本身分辨要用哪個商店的私鑰解密。
     * storeId 為 null/空字串時（例如舊資料或未帶路徑的呼叫），退回設定檔預設商店。
     */
    public OrderResult decryptUpdate(String storeId, String key, String mac, String cipher) throws Exception {
        B2CPayUpdate update = new B2CPayUpdate();
        update.setStoreId(resolveStoreId(storeId));
        update.setKey(key);
        update.setMac(mac);
        update.setCipher(cipher);
        transact("decryptUpdate", update);

        OrderResult result = new OrderResult();
        result.setOrderNo(update.getOrderNo());
        result.setRetCode(update.getRetCode());
        result.setCurrency(update.getCurrency());
        result.setOrderDate(update.getOrderDate());
        result.setOrderStatus(update.getOrderStatus());
        result.setApproveAmount(update.getApproveAmount());
        result.setAuthCode(update.getAuthCode());
        result.setAuthRRN(update.getAuthRRN());
        result.setCaptureAmount(update.getCaptureAmount());
        result.setCaptureDate(update.getCaptureDate());
        result.setAcquirer(update.getAcquirer());
        result.setEci(update.getEci());
        result.setTrxToken(update.getTrxToken());
        result.setExpiry(update.getExpiry());
        result.setLastBindCardTime(update.getLastBindCardTime());
        return result;
    }

    private OrderResult toOrderResult(B2CPay trx) {
        OrderResult result = new OrderResult();
        result.setOrderNo(trx.getOrderNo());
        result.setRetCode(trx.getRetCode());
        result.setCurrency(trx.getCurrency());
        result.setOrderDate(trx.getOrderDate());
        result.setOrderStatus(trx.getOrderStatus());
        result.setApproveAmount(trx.getApproveAmount());
        result.setAuthCode(trx.getAuthCode());
        result.setAuthRRN(trx.getAuthRRN());
        result.setCaptureAmount(trx.getCaptureAmount());
        result.setCaptureDate(trx.getCaptureDate());
        result.setRefundAmount(trx.getRefundAmount());
        result.setRefundBatch(trx.getPayBatchNum());
        result.setRefundRRN(trx.getRefundRRN());
        result.setRefundCode(trx.getRefundCode());
        result.setRefundDate(trx.getRefundDate());
        result.setAcquirer(trx.getAcquirer());
        result.setEci(trx.getEci());
        result.setTrxToken(trx.getTrxToken());
        result.setExpiry(trx.getExpiry());
        result.setLastBindCardTime(trx.getLastBindCardTime());
        return result;
    }

    /**
     * 取消退款。手冊 III、取消退款（Refund Reverse）確認：
     * - 函式共用 B2CPayOther，交易類別 type=6（toolkit 常數 RE_REFU）
     * - 只能對「已退款、但尚未清算」的訂單取消退款
     */
    public OrderResult voidRefund(VoidRefundRequest req) throws Exception {
        String storeId = resolveStoreId(req.getStoreId());

        B2CPayOther trx = new B2CPayOther();
        trx.setStoreId(storeId);
        trx.setType(trx.RE_REFU);
        trx.setOrderNo(req.getOrderNo());
        if (req.getAmount() != null) {
            trx.setAmount(TrustPayAmount.toTrustPayString(req.getAmount()));
        }
        trx.setQueryFlag("1");
        trx.setUpdateURL(buildUpdateUrl(storeId));
        transact("voidRefund", trx);

        return toOrderResult(trx);
    }

    /**
     * 綁卡。手冊 I、授權 + VI/VII 交易序號標記章節確認：
     * - 函式共用 B2CPayAuth（跟一般授權是同一支），差異只在 setE55("1")
     * - 走真實金額授權，之後要不要請款由呼叫端自己決定
     * - trxToken 不會出現在這支回應、也不保證出現在 ReturnURL；
     * 要等使用者刷卡完成導回、前端打 GET /api/payment/orders/{orderNo}（帶 E55=1 的 Query）才會拿到
     */
    public BindCardResponse bindCard(BindCardRequest req) throws Exception {
        String trustPayAmount = TrustPayAmount.toTrustPayString(req.getAmount());

        String storeId = resolveStoreId(req.getStoreId());

        B2CPayAuth auth = new B2CPayAuth();
        auth.setStoreId(storeId);
        auth.setOrderNo(req.getOrderNo());
        auth.setOrderDesc(req.getOrderDesc());
        auth.setAmount(trustPayAmount);
        auth.setReturnURL(props.getReturnUrl());
        auth.setUpdateURL(buildUpdateUrl(storeId));
        auth.setE55("1");
        transact("bindCard", auth);

        if (TrustPayResult.isSuccess(auth.getRetCode())) {
            return new BindCardResponse(auth.getRetCode(), auth.getToken());
        }
        return new BindCardResponse(auth.getRetCode(), null);
    }

    /**
     * 直接授權含交易序號標記。手冊 VI、直接授權含交易序號標記確認：
     * - 函式：B2CPayAuthSSLTrxToken，不用卡號，改用綁卡拿到的 trxToken + expiry
     * - queryFlag 固定開 "1"，跟直接授權一致，把明細一起帶回來
     */
    public OrderResult directAuthorizeTrxToken(DirectAuthorizeTrxTokenRequest req) throws Exception {
        String trustPayAmount = TrustPayAmount.toTrustPayString(req.getAmount());

        String storeId = resolveStoreId(req.getStoreId());

        B2CPayAuthSSLTrxToken auth = new B2CPayAuthSSLTrxToken();
        auth.setStoreId(storeId);
        auth.setOrderNo(req.getOrderNo());
        auth.setAmount(trustPayAmount);
        if (req.getOrderDesc() != null && !req.getOrderDesc().isBlank()) {
            auth.setOrderDesc(req.getOrderDesc());
        }
        auth.setTrxToken(req.getTrxToken());
        auth.setExpiry(req.getExpiry());
        if (req.getDepositFlag() != null && !req.getDepositFlag().isBlank()) {
            auth.setDepositFlag(req.getDepositFlag());
        }
        auth.setQueryFlag("1");
        auth.setUpdateURL(buildUpdateUrl(storeId));
        transact("directAuthorizeTrxToken", auth);

        return toOrderResult(auth);
    }

    /**
     * 授權含交易序號標記。手冊 VII、授權含交易序號標記確認：
     * - 函式：B2CPayAuthTrxToken，跟一般授權（B2CPayAuth）一樣是導頁流程，
     * 回應只有 retCode + token，真正的交易結果一樣要等 ReturnURL 導回後前端查詢
     * - 跟 authorize() 的差異只在於用 trxToken+expiry 代表卡片，不用把消費者導去輸入卡號
     */
    public AuthorizeResponse authorizeTrxToken(AuthorizeTrxTokenRequest req) throws Exception {
        String trustPayAmount = TrustPayAmount.toTrustPayString(req.getAmount());

        String storeId = resolveStoreId(req.getStoreId());

        B2CPayAuthTrxToken auth = new B2CPayAuthTrxToken();
        auth.setStoreId(storeId);
        auth.setOrderNo(req.getOrderNo());
        auth.setOrderDesc(req.getOrderDesc());
        auth.setAmount(trustPayAmount);
        auth.setTrxToken(req.getTrxToken());
        auth.setExpiry(req.getExpiry());
        if (req.getCvv() != null && !req.getCvv().isBlank()) {
            auth.setE01(req.getCvv());
        }
        if (req.getDepositFlag() != null && !req.getDepositFlag().isBlank()) {
            auth.setDepositFlag(req.getDepositFlag());
        }
        auth.setReturnURL(props.getReturnUrl());
        auth.setUpdateURL(buildUpdateUrl(storeId));
        auth.setQueryFlag("1");
        transact("authorizeTrxToken", auth);

        if (TrustPayResult.isSuccess(auth.getRetCode())) {
            return new AuthorizeResponse(auth.getRetCode(), auth.getToken());
        }
        return new AuthorizeResponse(auth.getRetCode(), null);
    }

    /**
     * 建立定期定額訂單（SIP）。手冊 E56/E57/E58 說明確認：
     * - 跟一般授權（authorize）共用 B2CPayAuth，差異只在多帶 E56/E57/E58 三個欄位
     * - 首期照樣要導頁完成 3D 驗證，之後的扣款由 TrustLink 依週期自動執行
     * - amount 是「每期」扣款金額，不是總金額
     */
    public AuthorizeResponse createSipOrder(SipAuthorizeRequest req) throws Exception {
        String trustPayAmount = TrustPayAmount.toTrustPayString(req.getAmount());

        String storeId = resolveStoreId(req.getStoreId());

        B2CPayAuth auth = new B2CPayAuth();
        auth.setStoreId(storeId);
        auth.setOrderNo(req.getOrderNo());
        auth.setOrderDesc(req.getOrderDesc());
        auth.setAmount(trustPayAmount);
        auth.setReturnURL(props.getReturnUrl());
        auth.setUpdateURL(buildUpdateUrl(storeId));
        auth.setE56(req.getPeriodCount());
        auth.setE57(req.getPeriodCycle());
        auth.setE58(req.getPeriodUnit());
        transact("createSipOrder", auth);

        if (TrustPayResult.isSuccess(auth.getRetCode())) {
            return new AuthorizeResponse(auth.getRetCode(), auth.getToken());
        }
        return new AuthorizeResponse(auth.getRetCode(), null);
    }

    /**
     * 查詢定期定額狀態。手冊 VIII、定期定額訂單查詢（SIP_Query）確認：
     * - 函式共用 B2CPayOther，交易類別 type=9（toolkit 常數 SIP_QUERY）
     * - orderNo 帶「主訂單」編號（第一期建立時的 orderNo，不是次期自動產生的 AUTO_CREATE_ORDER_xx）
     * - 結果不是走 toOrderResult()，是另外解析 getSIP_Data() 這個 JSON 字串
     */
    public SipQueryResult querySip(String orderNo, String storeId) throws Exception {
        B2CPayOther trx = new B2CPayOther();
        trx.setStoreId(resolveStoreId(storeId));
        trx.setType(trx.SIP_QUERY);
        trx.setOrderNo(orderNo);
        transact("querySip", trx);

        SipQueryResult result = new SipQueryResult();
        result.setRetCode(trx.getRetCode());

        String sipDataJson = trx.getSIP_Data();
        if (sipDataJson != null && !sipDataJson.isBlank()) {
            JSONObject root = new JSONObject(sipDataJson);

            JSONObject infoJson = root.optJSONObject("SIPInfo");
            if (infoJson != null) {
                SipInfo sipInfo = new SipInfo();
                sipInfo.setCreateTime(infoJson.optString("createTime", null));
                sipInfo.setPeriodType(infoJson.optString("periodType", null));
                sipInfo.setDeductFreq(infoJson.optString("deductFreq", null));
                sipInfo.setDeductTotalNum(infoJson.optString("deductTotalNum", null));
                sipInfo.setDeductChargedNum(infoJson.optString("deductChargedNum", null));
                sipInfo.setMaskPan(infoJson.optString("maskPan", null));
                sipInfo.setExpire(infoJson.optString("expire", null));
                result.setSipInfo(sipInfo);
            }

            JSONArray detailJson = root.optJSONArray("SIPDetail");
            if (detailJson != null) {
                List<SipDetail> list = new ArrayList<>();
                for (int i = 0; i < detailJson.length(); i++) {
                    JSONObject d = detailJson.getJSONObject(i);
                    SipDetail detail = new SipDetail();
                    detail.setPeriodNumber(d.optString("periodNumber", null));
                    detail.setOrderNumber(d.optString("orderNumber", null));
                    detail.setStatus(d.optString("status", null));
                    detail.setOrderStatus(d.optString("orderStatus", null));
                    detail.setEstimatedDeductDate(d.optString("estimatedDeductDate", null));
                    detail.setTrxStatus(d.optString("trxStatus", null));
                    list.add(detail);
                }
                result.setSipDetail(list);
            }
        }

        return result;
    }

    /**
     * 取消定期定額。手冊 IX、定期定額訂單取消（SIP_CANCEL）確認：
     * - 函式共用 B2CPayOther，交易類別 type=33（toolkit 常數 SIP_CANCEL）
     * - orderNo 必須是主訂單編號；取消後不會再依週期自動扣款
     * - 回應只有 retCode，不用像 voidAuthorize 那樣另外設 queryFlag 查明細
     */
    public SipCancelResponse cancelSip(SipCancelRequest req) throws Exception {
        String storeId = resolveStoreId(req.getStoreId());

        B2CPayOther trx = new B2CPayOther();
        trx.setStoreId(storeId);
        trx.setType(trx.SIP_CANCEL);
        trx.setOrderNo(req.getOrderNo());
        transact("cancelSip", trx);

        return new SipCancelResponse(trx.getRetCode());
    }

    /**
     * 隨身付（FOLLOW_PAY）。範例 JSP 確認：
     * - 其實就是 B2CPayAuth，跟一般授權 authorize() 完全同一支呼叫
     * - 差別在前端：auth.getToken() 拿到的網址要當成 iframe 的 src，
     * 搭配 hitrust-fp.js 讓消費者在同一頁完成付款，不用整頁導頁
     */
    public FollowPayTokenResponse createFollowPayToken(FollowPayTokenRequest req) throws Exception {
        String trustPayAmount = TrustPayAmount.toTrustPayString(req.getAmount());
        String storeId = resolveStoreId(req.getStoreId());

        B2CPayAuth auth = new B2CPayAuth();
        auth.setStoreId(storeId);
        auth.setOrderNo(req.getOrderNo());
        auth.setOrderDesc(req.getOrderDesc());
        auth.setAmount(trustPayAmount);
        auth.setReturnURL(props.getReturnUrl());
        auth.setUpdateURL(buildUpdateUrl(storeId));
        transact("followPayToken", auth);

        if (TrustPayResult.isSuccess(auth.getRetCode())) {
            return new FollowPayTokenResponse(auth.getRetCode(), auth.getToken());
        }
        return new FollowPayTokenResponse(auth.getRetCode(), null);
    }

    /**
     * Apple Pay 付款。手冊 XI、ApplePay/GooglePay/SamsungPay 加密資料付款 確認：
     * - E60='1' 表示 Apple Pay（'2' google pay、'3' samsung pay）
     * - E61 = Apple 回傳的加密 paymentData，直接整包 JSON 字串塞進去，不用另外 base64
     * - 回應只有 retCode，沒有導頁/token，因為裝置端授權已經取代 3D 驗證
     */
    public ApplePayChargeResponse payWithApplePay(ApplePayChargeRequest req) throws Exception {
        String trustPayAmount = TrustPayAmount.toTrustPayString(req.getAmount());
        String storeId = resolveStoreId(req.getStoreId());

        B2CPayAuthSSLApplePay auth = new B2CPayAuthSSLApplePay();
        auth.setStoreId(storeId);
        auth.setOrderNo(req.getOrderNo());
        auth.setOrderDesc(req.getOrderDesc());
        auth.setAmount(trustPayAmount);
        auth.setDepositFlag("1");
        auth.setQueryFlag("1");
        auth.setE60("1");
        auth.setE61(req.getPaymentData());
        transact("applePay", auth);

        return new ApplePayChargeResponse(auth.getRetCode());
    }
}