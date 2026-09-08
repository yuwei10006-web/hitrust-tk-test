import { useState } from "react";
import { getOrder } from "../lib/api.js";

export default function QueryPanel({ orderNo, storeId }) {
  const [submitting, setSubmitting] = useState(false);
  const [result, setResult] = useState(null);
  const [error, setError] = useState(null);

  async function handleQuery() {
    setError(null);
    setResult(null);
    setSubmitting(true);
    try {
      setResult(await getOrder(orderNo, storeId));
    } catch (err) {
      setError({ title: "查詢失敗", detail: err.message });
    } finally {
      setSubmitting(false);
    }
  }

  return (
    <section className="panel">
      <h2 className="panel-title">查詢</h2>
      <p className="panel-hint">查詢上方訂單編號目前在 TrustPay 的完整交易狀態。</p>
      <button className="submit-btn" type="button" onClick={handleQuery} disabled={submitting || !orderNo}>
        {submitting ? "查詢中…" : "查詢這筆訂單"}
      </button>
      {error && (
        <div className="form-error">
          {error.title}
          <br />
          <strong>{error.detail}</strong>
        </div>
      )}
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