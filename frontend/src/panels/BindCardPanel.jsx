import { useState } from "react";
import { bindCard } from "../lib/api.js";
import { genOrderNo } from "../lib/orderNo.js";

export default function BindCardPanel({ orderNo, onOrderNoChange, onSuccess, storeId }) {
  const [amount, setAmount] = useState("1");
  const [orderDesc, setOrderDesc] = useState("綁卡");
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState(null);

  async function handleSubmit(e) {
    e.preventDefault();
    setError(null);
    setSubmitting(true);
    try {
      const result = await bindCard({ orderNo, amount: Number(amount), orderDesc, storeId });
      if (result.retCode === "00" && result.redirectUrl) {
        onSuccess(orderNo);
        window.location.href = result.redirectUrl;
        return;
      }
      setError({ title: "後端拒絕了這筆綁卡請求", detail: `retCode: ${result.retCode}` });
    } catch (err) {
      setError({ title: "呼叫 /api/payment/bind-card 失敗", detail: err.message });
    } finally {
      setSubmitting(false);
    }
  }

  return (
    <section className="panel">
      <h2 className="panel-title">綁卡</h2>
      <p className="panel-hint">
        送出後會導去 TrustPay 付款頁輸入卡號，完成後導回結果頁。這筆會走真實金額授權，
        並開啟交易序號標記，成功後結果頁會顯示 trxToken，供之後直接授權含序號標記／授權含序號標記使用。
      </p>
      <form onSubmit={handleSubmit}>
        <div className="field">
          <label htmlFor="bindCardOrderNo">訂單編號</label>
          <div className="field-row">
            <input
              id="bindCardOrderNo"
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
          <label htmlFor="bindCardAmount">金額（元）</label>
          <input
            id="bindCardAmount"
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
          <label htmlFor="bindCardOrderDesc">訂單描述</label>
          <textarea
            id="bindCardOrderDesc"
            className="tk-input"
            value={orderDesc}
            onChange={(e) => setOrderDesc(e.target.value)}
            maxLength={40}
          />
        </div>
        <button className="submit-btn" type="submit" disabled={submitting}>
          {submitting ? "建立綁卡中…" : "送出並前往付款"}
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