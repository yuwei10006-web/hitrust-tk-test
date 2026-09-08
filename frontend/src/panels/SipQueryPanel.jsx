import { useState } from "react";
import { getSipOrder } from "../lib/api.js";

const STATUS_LABEL = {
  "00": "已扣款",
  "01": "待扣款",
  "02": "扣款失敗",
  "03": "已終止定期定額",
};

const PERIOD_TYPE_LABEL = { D: "天", W: "週", M: "月", Y: "年" };

export default function SipQueryPanel({ orderNo, storeId }) {
  const [submitting, setSubmitting] = useState(false);
  const [result, setResult] = useState(null);
  const [error, setError] = useState(null);

  async function handleQuery() {
    setError(null);
    setResult(null);
    setSubmitting(true);
    try {
      setResult(await getSipOrder(orderNo, storeId));
    } catch (err) {
      setError({ title: "查詢失敗", detail: err.message });
    } finally {
      setSubmitting(false);
    }
  }

  return (
    <section className="panel">
      <h2 className="panel-title">定期定額查詢</h2>
      <p className="panel-hint">
        用上方「主訂單編號」查詢首期建立資訊，以及各期已扣款/待扣款/失敗狀態。
      </p>
      <button
        className="submit-btn"
        type="button"
        onClick={handleQuery}
        disabled={submitting || !orderNo}
      >
        {submitting ? "查詢中…" : "查詢這筆定期定額"}
      </button>

      {error && (
        <div className="form-error">
          {error.title}
          <br />
          <strong>{error.detail}</strong>
        </div>
      )}

      {result && (
        <>
          <div className={`inline-result ${result.retCode === "00" ? "tone-success" : "tone-error"}`}>
            <div className="inline-result-row">
              <span>retCode</span>
              <span>{result.retCode}</span>
            </div>
            {result.sipInfo && (
              <>
                <div className="inline-result-row">
                  <span>建立日期</span>
                  <span>{result.sipInfo.createTime ?? "-"}</span>
                </div>
                <div className="inline-result-row">
                  <span>扣款週期</span>
                  <span>
                    每 {result.sipInfo.deductFreq ?? "-"} {PERIOD_TYPE_LABEL[result.sipInfo.periodType] ?? result.sipInfo.periodType} 一期
                  </span>
                </div>
                <div className="inline-result-row">
                  <span>期數進度</span>
                  <span>{result.sipInfo.deductChargedNum ?? "-"} / {result.sipInfo.deductTotalNum ?? "-"}</span>
                </div>
                <div className="inline-result-row">
                  <span>卡號 / 效期</span>
                  <span>{result.sipInfo.maskPan ?? "-"} / {result.sipInfo.expire ?? "-"}</span>
                </div>
              </>
            )}
          </div>

          {result.sipDetail && result.sipDetail.length > 0 && (
            <div className="panel" style={{ marginTop: 16 }}>
              <h3 className="panel-title" style={{ fontSize: "1rem" }}>各期明細</h3>
              {result.sipDetail.map((d) => (
                <div
                  key={d.periodNumber}
                  className={`inline-result ${d.status === "02" ? "tone-error" : "tone-success"}`}
                  style={{ marginBottom: 8 }}
                >
                  <div className="inline-result-row">
                    <span>第 {d.periodNumber} 期</span>
                    <span>{STATUS_LABEL[d.status] ?? d.status}</span>
                  </div>
                  <div className="inline-result-row">
                    <span>訂單編號</span>
                    <span>{d.orderNumber ?? "-"}</span>
                  </div>
                  <div className="inline-result-row">
                    <span>預計扣款日</span>
                    <span>{d.estimatedDeductDate ?? "-"}</span>
                  </div>
                  {d.trxStatus && (
                    <div className="inline-result-row">
                      <span>失敗代碼</span>
                      <span>{d.trxStatus}</span>
                    </div>
                  )}
                </div>
              ))}
            </div>
          )}
        </>
      )}
    </section>
  );
}