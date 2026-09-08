import { useState } from "react";
import { authorize } from "../lib/api.js";
import { genOrderNo } from "../lib/orderNo.js";

export default function AuthorizePanel({ orderNo, onOrderNoChange, onSuccess, storeId }) {
  const [amount, setAmount] = useState("100");
  const [orderDesc, setOrderDesc] = useState("測試訂單");
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState(null);

  async function handleSubmit(e) {
    e.preventDefault();
    setError(null);
    setSubmitting(true);
    try {
      const result = await authorize({ orderNo, amount: Number(amount), orderDesc, storeId });
      if (result.retCode === "00" && result.redirectUrl) {
        onSuccess(orderNo);
        window.location.href = result.redirectUrl;
        return;
      }
      setError({ title: "後端拒絕了這筆授權請求", detail: `retCode: ${result.retCode}` });
    } catch (err) {
      setError({ title: "呼叫 /api/payment/authorize 失敗", detail: err.message });
    } finally {
      setSubmitting(false);
    }
  }

  return (
    <section className="panel">
      <h2 className="panel-title">授權</h2>
      <p className="panel-hint">送出後你會被導去 TrustPay 的付款頁輸入測試卡號，完成後會被導回結果頁。</p>
      <form onSubmit={handleSubmit}>
        <div className="field">
          <label htmlFor="orderNo">訂單編號</label>
          <div className="field-row">
            <input
              id="orderNo"
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
          <label htmlFor="amount">金額（元）</label>
          <input
            id="amount"
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
          <label htmlFor="orderDesc">訂單描述</label>
          <textarea
            id="orderDesc"
            className="tk-input"
            value={orderDesc}
            onChange={(e) => setOrderDesc(e.target.value)}
            maxLength={40}
          />
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