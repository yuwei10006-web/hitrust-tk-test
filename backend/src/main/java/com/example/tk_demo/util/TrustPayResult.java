package com.example.tk_demo.util;

/**
 * TrustPay retCode 對照。
 * 完整代碼表由網際威信提供的 Google 試算表維護，這裡先只放程式裡真的需要判斷分支的代碼，
 * 之後要加其他判斷（例如特定失敗原因要顯示不同訊息）就往這裡加，不要在 Controller/Service 裡到處比對字串。
 */
public final class TrustPayResult {

    /** 交易成功 */
    public static final String SUCCESS = "00";

    private TrustPayResult() {
    }

    public static boolean isSuccess(String retCode) {
        return SUCCESS.equals(retCode);
    }
}
