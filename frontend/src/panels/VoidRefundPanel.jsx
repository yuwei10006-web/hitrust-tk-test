import { useState } from "react";
import { voidRefund } from "../lib/api.js";

export default function VoidRefundPanel({ orderNo, onVoided, storeId }) {
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
      const res = await voidRefund({
        orderNo,
        amount: amount ? Number(amount) : undefined,
        storeId,
      });
      setResult(res);
      if (res.retCode === "00") onVoided?.(orderNo);
    } catch (err) {
      setError({ title: "呼叫 /api/payment/void-refund 失敗", detail: err.message });
    } finally {
      setSubmitting(false);
    }
  }

  return (
    <section className="panel">
      <h2 className="panel-title">取消退款</h2>
      <p className="panel-hint">
        對上方「操作中的訂單編號」取消退款，僅適用於已退款、且尚未清算的訂單。金額欄位一般不用填，
        如遇個別收單銀行要求帶金額再填。
      </p>
      <form onSubmit={handleSubmit}>
        <div className="field">
          <label htmlFor="voidRefundAmount">金額（元，選填）</label>
          <input
            id="voidRefundAmount"
            className="tk-input amount"
            type="number"
            min="0.01"
            step="0.01"
            value={amount}
            onChange={(e) => setAmount(e.target.value)}
            placeholder="一般可留空"
          />
        </div>
        <button className="submit-btn" type="submit" disabled={submitting || !orderNo}>
          {submitting ? "取消退款中…" : "送出取消退款"}
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