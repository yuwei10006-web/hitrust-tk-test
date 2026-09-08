import { useState } from "react";
import { voidAuthorize } from "../lib/api.js";

export default function VoidAuthorizePanel({ orderNo, onVoided, storeId }) {
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
      const res = await voidAuthorize({
        orderNo,
        amount: amount ? Number(amount) : undefined,
        storeId,
      });
      setResult(res);
      if (res.retCode === "00") onVoided?.(orderNo);
    } catch (err) {
      setError({ title: "呼叫 /api/payment/void-authorize 失敗", detail: err.message });
    } finally {
      setSubmitting(false);
    }
  }

  return (
    <section className="panel">
      <h2 className="panel-title">取消授權</h2>
      <p className="panel-hint">
        對上方「操作中的訂單編號」取消授權，僅適用於尚未請款的訂單。金額欄位一般不用填，
        <strong>但若該筆訂單的收單銀行為台北富邦，取消授權必須填入金額</strong>，其他銀行留空即可。
      </p>
      <form onSubmit={handleSubmit}>
        <div className="field">
          <label htmlFor="voidAmount">金額（元，選填，僅台北富邦需要）</label>
          <input
            id="voidAmount"
            className="tk-input amount"
            type="number"
            min="0.01"
            step="0.01"
            value={amount}
            onChange={(e) => setAmount(e.target.value)}
            placeholder="非台北富邦可留空"
          />
        </div>
        <button className="submit-btn" type="submit" disabled={submitting || !orderNo}>
          {submitting ? "取消授權中…" : "送出取消授權"}
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