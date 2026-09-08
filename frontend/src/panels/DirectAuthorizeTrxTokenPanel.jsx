import { useState } from "react";
import { directAuthorizeTrxToken } from "../lib/api.js";
import { genOrderNo } from "../lib/orderNo.js";

export default function DirectAuthorizeTrxTokenPanel({ orderNo, onOrderNoChange, onSuccess, storeId }) {
  const [amount, setAmount] = useState("100");
  const [orderDesc, setOrderDesc] = useState("測試訂單（序號標記）");
  const [trxToken, setTrxToken] = useState("");
  const [expiry, setExpiry] = useState("");
  const [depositFlag, setDepositFlag] = useState("");
  const [submitting, setSubmitting] = useState(false);
  const [result, setResult] = useState(null);
  const [error, setError] = useState(null);

  async function handleSubmit(e) {
    e.preventDefault();
    setError(null);
    setResult(null);
    setSubmitting(true);
    try {
      const res = await directAuthorizeTrxToken({
        orderNo,
        amount: Number(amount),
        orderDesc,
        trxToken,
        expiry,
        depositFlag,
        storeId,
      });
      setResult(res);
      if (res.retCode === "00") onSuccess?.(orderNo);
    } catch (err) {
      setError({ title: "呼叫 /api/payment/direct-authorize-trxtoken 失敗", detail: err.message });
    } finally {
      setSubmitting(false);
    }
  }

  return (
    <section className="panel">
      <h2 className="panel-title">直接授權含交易序號標記</h2>
      <p className="panel-hint">
        不輸入卡號，改用先前綁卡成功拿到的 trxToken + 到期日代表這張卡，一次呼叫拿到結果。
      </p>
      <form onSubmit={handleSubmit}>
        <div className="field">
          <label htmlFor="dtOrderNo">訂單編號</label>
          <div className="field-row">
            <input
              id="dtOrderNo"
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
          <label htmlFor="dtAmount">金額（元）</label>
          <input
            id="dtAmount"
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
          <label htmlFor="dtOrderDesc">訂單描述</label>
          <textarea
            id="dtOrderDesc"
            className="tk-input"
            value={orderDesc}
            onChange={(e) => setOrderDesc(e.target.value)}
            maxLength={40}
          />
        </div>
        <div className="field">
          <label htmlFor="dtTrxToken">交易序號 trxToken</label>
          <input
            id="dtTrxToken"
            className="tk-input"
            value={trxToken}
            onChange={(e) => setTrxToken(e.target.value)}
            required
          />
        </div>
        <div className="field">
          <label htmlFor="dtExpiry">到期日（YYMM）</label>
          <input
            id="dtExpiry"
            className="tk-input"
            value={expiry}
            onChange={(e) => setExpiry(e.target.value.replace(/\D/g, ""))}
            maxLength={4}
            placeholder="例如 2812"
            required
          />
        </div>
        <div className="field">
          <label htmlFor="dtDepositFlag">自動請款標記（選填）</label>
          <select
            id="dtDepositFlag"
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
          {submitting ? "授權中…" : "送出直接授權"}
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