import { useState } from "react";
import { cancelSipOrder } from "../lib/api.js";

export default function SipCancelPanel({ orderNo, onCancelled, storeId }) {
  const [submitting, setSubmitting] = useState(false);
  const [result, setResult] = useState(null);
  const [error, setError] = useState(null);

  async function handleSubmit(e) {
    e.preventDefault();
    setError(null);
    setResult(null);
    setSubmitting(true);
    try {
      const res = await cancelSipOrder({ orderNo, storeId });
      setResult(res);
      if (res.retCode === "00") onCancelled?.(orderNo);
    } catch (err) {
      setError({ title: "呼叫 /api/payment/sip-cancel 失敗", detail: err.message });
    } finally {
      setSubmitting(false);
    }
  }

  return (
    <section className="panel">
      <h2 className="panel-title">取消定期定額</h2>
      <p className="panel-hint">
        對上方「主訂單編號」取消定期定額，取消後不會再依週期自動扣款，
        <strong>已扣款的各期不會被退款</strong>，要退款請另外對該期訂單編號執行退款。
      </p>
      <form onSubmit={handleSubmit}>
        <button className="submit-btn" type="submit" disabled={submitting || !orderNo}>
          {submitting ? "取消中…" : "送出取消定期定額"}
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
          <div className="inline-result-row">
            <span>retCode</span>
            <span>{result.retCode}</span>
          </div>
        </div>
      )}
    </section>
  );
}