import { useState } from "react";
import { chargeApplePay } from "../lib/api.js";
import { genOrderNo } from "../lib/orderNo.js";

export default function ApplePayPanel({ orderNo, onOrderNoChange, storeId }) {
  const [amount, setAmount] = useState("100");
  const [orderDesc, setOrderDesc] = useState("Apple Pay 測試訂單");
  const [paymentData, setPaymentData] = useState("");
  const [submitting, setSubmitting] = useState(false);
  const [result, setResult] = useState(null);
  const [error, setError] = useState(null);

  async function handleSubmit(e) {
    e.preventDefault();
    setError(null);
    setResult(null);
    setSubmitting(true);
    try {
      const res = await chargeApplePay({
        orderNo,
        amount: Number(amount),
        orderDesc,
        paymentData,
        storeId,
      });
      setResult(res);
    } catch (err) {
      setError({ title: "呼叫 /api/payment/apple-pay/charge 失敗", detail: err.message });
    } finally {
      setSubmitting(false);
    }
  }

  return (
    <section className="panel">
      <h2 className="panel-title">Apple Pay</h2>
      <p className="panel-hint">
        照手冊 E60/E61 直接測 <code>B2CPayAuthSSLApplePay</code>。
        <code>paymentData</code> 要貼 Apple 回傳的加密付款資料 JSON——目前還沒接 Apple Pay JS，
        先手動貼假資料驗證這支後端呼叫本身是通的。
      </p>
      <form onSubmit={handleSubmit}>
        <div className="field">
          <label htmlFor="apOrderNo">訂單編號</label>
          <div className="field-row">
            <input
              id="apOrderNo"
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
          <label htmlFor="apAmount">金額（元）</label>
          <input
            id="apAmount"
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
          <label htmlFor="apOrderDesc">訂單描述</label>
          <textarea
            id="apOrderDesc"
            className="tk-input"
            value={orderDesc}
            onChange={(e) => setOrderDesc(e.target.value)}
            maxLength={40}
          />
        </div>

        <div className="field">
          <label htmlFor="apPaymentData">paymentData（E61，貼 Apple 加密資料 JSON）</label>
          <textarea
            id="apPaymentData"
            className="tk-input"
            rows={6}
            value={paymentData}
            onChange={(e) => setPaymentData(e.target.value)}
            required
          />
        </div>

        <button className="submit-btn" type="submit" disabled={submitting}>
          {submitting ? "送出中…" : "送出 Apple Pay 付款"}
        </button>
        {error && (
          <div className="form-error">
            {error.title}
            <br />
            <strong>{error.detail}</strong>
          </div>
        )}
      </form>

      {result && (
        <div className={`inline-result ${result.retCode === "00" ? "tone-success" : "tone-error"}`} style={{ marginTop: 16 }}>
          <div className="inline-result-row">
            <span>retCode</span>
            <span>{result.retCode}</span>
          </div>
        </div>
      )}
    </section>
  );
}