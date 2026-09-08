import { useState } from "react";
import { authorizeTrxToken } from "../lib/api.js";
import { genOrderNo } from "../lib/orderNo.js";

export default function AuthorizeTrxTokenPanel({ orderNo, onOrderNoChange, onSuccess, storeId }) {
  const [amount, setAmount] = useState("100");
  const [orderDesc, setOrderDesc] = useState("測試訂單（授權含序號標記）");
  const [trxToken, setTrxToken] = useState("");
  const [expiry, setExpiry] = useState("");
  const [cvv, setCvv] = useState("");
  const [depositFlag, setDepositFlag] = useState("");
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState(null);

  async function handleSubmit(e) {
    e.preventDefault();
    setError(null);
    setSubmitting(true);
    try {
      const result = await authorizeTrxToken({
        orderNo,
        amount: Number(amount),
        orderDesc,
        trxToken,
        expiry,
        cvv,
        depositFlag,
        storeId,
      });
      if (result.retCode === "00" && result.redirectUrl) {
        onSuccess?.(orderNo);
        window.location.href = result.redirectUrl;
        return;
      }
      setError({ title: "後端拒絕了這筆授權請求", detail: `retCode: ${result.retCode}` });
    } catch (err) {
      setError({ title: "呼叫 /api/payment/authorize-trxtoken 失敗", detail: err.message });
    } finally {
      setSubmitting(false);
    }
  }

  return (
    <section className="panel">
      <h2 className="panel-title">授權含交易序號標記</h2>
      <p className="panel-hint">
        用 trxToken + 到期日代表卡片，不用輸入卡號；視情況可能還是會導頁完成 3DS 驗證，完成後導回結果頁。
      </p>
      <form onSubmit={handleSubmit}>
        <div className="field">
          <label htmlFor="atOrderNo">訂單編號</label>
          <div className="field-row">
            <input
              id="atOrderNo"
              className="tk-input"
              value={orderNo}
              onChange={(e) => onOrderNoChange(e.target.value.toUpperCase())}
              maxLength={19}
              pattern="[A-Za-z0-9]+"
              required
            />
            <button
              type="button"
              className="icon-btn"
              onClick={() => onOrderNoChange(genOrderNo())}
              title="重新產生訂單編號"
            >
              ↻
            </button>
          </div>
        </div>
        <div className="field">
          <label htmlFor="atAmount">金額（元）</label>
          <input
            id="atAmount"
            className="tk-input amount"
            type="number"
            min="1"
            step="1"
            value={amount}
            onChange={(e) => setAmount(e.target.value)}
            required
          />
        </div>
        <div className="field">
          <label htmlFor="atOrderDesc">訂單描述</label>
          <textarea
            id="atOrderDesc"
            className="tk-input"
            value={orderDesc}
            onChange={(e) => setOrderDesc(e.target.value)}
            maxLength={40}
          />
        </div>
        <div className="field">
          <label htmlFor="atTrxToken">交易序號 trxToken</label>
          <input
            id="atTrxToken"
            className="tk-input"
            value={trxToken}
            onChange={(e) => setTrxToken(e.target.value)}
            required
          />
        </div>
        <div className="field">
          <label htmlFor="atExpiry">到期日（YYMM）</label>
          <input
            id="atExpiry"
            className="tk-input"
            value={expiry}
            onChange={(e) => setExpiry(e.target.value.replace(/\D/g, ""))}
            maxLength={4}
            placeholder="例如 2812"
            required
          />
        </div>
        <div className="field">
          <label htmlFor="atCvv">CVC2/CVV2（選填，商代有開啟才需要）</label>
          <input
            id="atCvv"
            className="tk-input"
            value={cvv}
            onChange={(e) => setCvv(e.target.value.replace(/\D/g, ""))}
            maxLength={4}
          />
        </div>
        <div className="field">
          <label htmlFor="atDepositFlag">自動請款標記（選填）</label>
          <select
            id="atDepositFlag"
            className="tk-input"
            value={depositFlag}
            onChange={(e) => setDepositFlag(e.target.value)}
          >
            <option value="">預設（依設定檔）</option>
            <option value="0">0：一般交易</option>
            <option value="1">1：Sale 交易（自動請款）</option>
          </select>
        </div>
        <button className="submit-btn" type="submit" disabled={submitting}>
          {submitting ? "建立授權中…" : "送出並前往付款"}
        </button>
        {error && (
          <div className="form-error">
            {error.title}
            <br />
            <strong>{error.detail}</strong>
          </div>
        )}
      </form>
    </section>
  );
}