import { useState } from "react";
import { refund } from "../lib/api.js";

export default function RefundPanel({ orderNo, onRefunded, storeId }) {
  const [amount, setAmount] = useState("");
  const [submitting, setSubmitting] = useState(false);
  const [result, setResult] = useState(null);
  const [error, setError] = useState(null);

  async function handleSubmit(e) {
    e.preventDefault();
    setError(null);
    setResult(null);
    setSubmitting(true);
    try {
      const res = await refund({ orderNo, amount: Number(amount), storeId });
      setResult(res);
      if (res.retCode === "00") onRefunded?.(orderNo);
    } catch (err) {
      setError({ title: "呼叫 /api/payment/refund 失敗", detail: err.message });
    } finally {
      setSubmitting(false);
    }
  }

  return (
    <section className="panel">
      <h2 className="panel-title">退款</h2>
      <p className="panel-hint">對上方「操作中的訂單編號」送出退款，訂單必須是請款已清算的狀態。</p>
      <form onSubmit={handleSubmit}>
        <div className="field">
          <label htmlFor="refundAmount">退款金額（元）</label>
          <input
            id="refundAmount"
            className="tk-input amount"
            type="number"
            min="0.01"
            step="0.01"
            value={amount}
            onChange={(e) => setAmount(e.target.value)}
            required
          />
        </div>
        <button className="submit-btn" type="submit" disabled={submitting || !orderNo}>
          {submitting ? "退款中…" : "送出退款"}
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
        <div className={`inline-result ${result.retCode === "00" ? "tone-success" : "tone-error"}`}>
          {Object.entries(result).map(([k, v]) => (
            <div className="inline-result-row" key={k}>
              <span>{k}</span>
              <span>{v ?? "-"}</span>
            </div>
          ))}
        </div>
      )}
    </section>
  );
}