import { useState } from "react";
import { capture } from "../lib/api.js";

export default function CapturePanel({ orderNo, onCaptured, storeId }) {
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
      const res = await capture({ orderNo, amount: Number(amount), storeId });
      setResult(res);
      if (res.retCode === "00") onCaptured(orderNo);
    } catch (err) {
      setError({ title: "呼叫 /api/payment/capture 失敗", detail: err.message });
    } finally {
      setSubmitting(false);
    }
  }

  return (
    <section className="panel">
      <h2 className="panel-title">請款</h2>
      <p className="panel-hint">對上方「操作中的訂單編號」送出請款，訂單必須已完成授權。</p>
      <form onSubmit={handleSubmit}>
        <div className="field">
          <label htmlFor="captureAmount">請款金額（元）</label>
          <input
            id="captureAmount"
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
          {submitting ? "請款中…" : "送出請款"}
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
          <div className="inline-result-row"><span>retCode</span><span>{result.retCode}</span></div>
          <div className="inline-result-row"><span>captureAmount</span><span>{result.captureAmount ?? "-"}</span></div>
          <div className="inline-result-row"><span>captureDate</span><span>{result.captureDate ?? "-"}</span></div>
        </div>
      )}
    </section>
  );
}