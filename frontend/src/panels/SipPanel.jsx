import { useState } from "react";
import { createSipOrder } from "../lib/api.js";
import { genOrderNo } from "../lib/orderNo.js";

export default function SipPanel({ orderNo, onOrderNoChange, onSuccess, storeId }) {
  const [amount, setAmount] = useState("100");
  const [orderDesc, setOrderDesc] = useState("定期定額測試訂單");
  const [periodCount, setPeriodCount] = useState("12");
  const [periodCycle, setPeriodCycle] = useState("1");
  const [periodUnit, setPeriodUnit] = useState("M");
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState(null);

  async function handleSubmit(e) {
    e.preventDefault();
    setError(null);
    setSubmitting(true);
    try {
      const result = await createSipOrder({
        orderNo,
        amount: Number(amount),
        orderDesc,
        periodCount,
        periodCycle,
        periodUnit,
        storeId,
      });
      if (result.retCode === "00" && result.redirectUrl) {
        onSuccess(orderNo);
        window.location.href = result.redirectUrl;
        return;
      }
      setError({ title: "後端拒絕了這筆定期定額申請", detail: `retCode: ${result.retCode}` });
    } catch (err) {
      setError({ title: "呼叫 /api/payment/sip-authorize 失敗", detail: err.message });
    } finally {
      setSubmitting(false);
    }
  }

  return (
    <section className="panel">
      <h2 className="panel-title">定期定額</h2>
      <p className="panel-hint">
        送出後會導去 TrustPay 付款頁完成首期 3D 驗證，之後的扣款由 TrustLink 依週期自動執行。
      </p>
      <form onSubmit={handleSubmit}>
        <div className="field">
          <label htmlFor="sipOrderNo">主訂單編號</label>
          <div className="field-row">
            <input
              id="sipOrderNo"
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
          <label htmlFor="sipAmount">每期金額（元）</label>
          <input
            id="sipAmount"
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
          <label htmlFor="sipOrderDesc">訂單描述</label>
          <textarea
            id="sipOrderDesc"
            className="tk-input"
            value={orderDesc}
            onChange={(e) => setOrderDesc(e.target.value)}
            maxLength={40}
          />
        </div>

        <div className="field">
          <label htmlFor="periodCount">扣款期數</label>
          <input
            id="periodCount"
            className="tk-input"
            type="number"
            min="1"
            max="9999"
            value={periodCount}
            onChange={(e) => setPeriodCount(e.target.value)}
            required
          />
        </div>

        <div className="field">
          <label htmlFor="periodCycle">扣款週期數值</label>
          <input
            id="periodCycle"
            className="tk-input"
            type="number"
            min="1"
            max="9999"
            value={periodCycle}
            onChange={(e) => setPeriodCycle(e.target.value)}
            required
          />
        </div>

        <div className="field">
          <label htmlFor="periodUnit">週期單位</label>
          <select
            id="periodUnit"
            className="tk-input"
            value={periodUnit}
            onChange={(e) => setPeriodUnit(e.target.value)}
          >
            <option value="D">天</option>
            <option value="W">週</option>
            <option value="M">月</option>
            <option value="Y">年</option>
          </select>
        </div>

        <p className="panel-hint">
          例：期數 12、週期數值 1、單位「月」＝每月扣款一次，共 12 期。
        </p>

        <button className="submit-btn" type="submit" disabled={submitting}>
          {submitting ? "建立定期定額中…" : "送出並前往付款"}
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