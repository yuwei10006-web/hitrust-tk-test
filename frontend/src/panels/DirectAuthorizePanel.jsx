import { useState } from "react";
import { directAuthorize } from "../lib/api.js";
import { genOrderNo } from "../lib/orderNo.js";

export default function DirectAuthorizePanel({ orderNo, onOrderNoChange, onSuccess, storeId }) {
  const [amount, setAmount] = useState("100");
  const [orderDesc, setOrderDesc] = useState("測試訂單（直接授權）");
  const [pan, setPan] = useState("");
  const [expiry, setExpiry] = useState("");
  const [cvv, setCvv] = useState("");
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
      const res = await directAuthorize({
        orderNo,
        amount: Number(amount),
        orderDesc,
        pan,
        expiry,
        cvv,
        depositFlag,
        storeId,
      });
      setResult(res);
      if (res.retCode === "00") onSuccess?.(orderNo);
    } catch (err) {
      setError({ title: "呼叫 /api/payment/direct-authorize 失敗", detail: err.message });
    } finally {
      setSubmitting(false);
    }
  }

  return (
    <section className="panel">
      <h2 className="panel-title">直接授權</h2>
      <p className="panel-hint">
        卡號、有效期限直接在這頁輸入送出，不會導去 TrustPay 付款頁。僅供測試環境使用測試卡號。
      </p>
      <form onSubmit={handleSubmit}>
        <div className="field">
          <label htmlFor="daOrderNo">訂單編號</label>
          <div className="field-row">
            <input
              id="daOrderNo"
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
          <label htmlFor="daAmount">金額（元）</label>
          <input
            id="daAmount"
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
          <label htmlFor="daOrderDesc">訂單描述</label>
          <textarea
            id="daOrderDesc"
            className="tk-input"
            value={orderDesc}
            onChange={(e) => setOrderDesc(e.target.value)}
            maxLength={40}
          />
        </div>
        <div className="field">
          <label htmlFor="daPan">卡號</label>
          <input
            id="daPan"
            className="tk-input"
            value={pan}
            onChange={(e) => setPan(e.target.value.replace(/\D/g, ""))}
            maxLength={19}
            required
          />
        </div>
        <div className="field">
          <label htmlFor="daExpiry">有效期限（YYMM）</label>
          <input
            id="daExpiry"
            className="tk-input"
            value={expiry}
            onChange={(e) => setExpiry(e.target.value.replace(/\D/g, ""))}
            maxLength={4}
            placeholder="例如 2812"
            required
          />
        </div>
        <div className="field">
          <label htmlFor="daCvv">CVC2/CVV2（選填，商代有開啟才需要）</label>
          <input
            id="daCvv"
            className="tk-input"
            value={cvv}
            onChange={(e) => setCvv(e.target.value.replace(/\D/g, ""))}
            maxLength={4}
          />
        </div>
        <div className="field">
          <label htmlFor="daDepositFlag">自動請款標記（選填）</label>
          <select
            id="daDepositFlag"
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