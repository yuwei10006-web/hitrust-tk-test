package com.example.tk_demo.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * TrustPay 的 setAmount() 規格：字串須「包含兩位小數」，例如送出 "100" 代表 1.00 元。
 * 也就是實際上是「元 * 100 後的整數字串」。所有交易類型（授權/請款/退款）都要送這個格式，
 * 統一在這裡轉換，不要在各個 Service method 裡各自處理，避免有的地方轉、有的地方忘記轉。
 */
public final class TrustPayAmount {

    private TrustPayAmount() {
    }

    /** 把「元」金額（例如 1500.00）轉成 TrustPay 要的字串（例如 "150000"） */
    public static String toTrustPayString(BigDecimal amountInDollars) {
        if (amountInDollars == null) {
            throw new IllegalArgumentException("amount 不能是 null");
        }
        if (amountInDollars.signum() <= 0) {
            throw new IllegalArgumentException("amount 必須大於 0");
        }
        return amountInDollars
                .setScale(2, RoundingMode.HALF_UP)
                .movePointRight(2)
                .toBigIntegerExact()
                .toString();
    }
}
