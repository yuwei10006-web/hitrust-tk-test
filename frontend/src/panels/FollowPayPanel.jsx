import { useEffect, useRef, useState } from "react";
import { createFollowPayToken } from "../lib/api.js";
import { genOrderNo } from "../lib/orderNo.js";

// 測試環境。正式環境要換成 https://trustpay.hitrust.com.tw/TRUSTPAY/js/hitrust-fp.js?v=1.1
const FP_JS_TEST = "https://testtrustpay.hitrust.com.tw/TRUSTPAY/js/hitrust-fp.js?v=1.1";
const JQUERY_SRC = "https://code.jquery.com/jquery-1.10.2.js";

function loadScriptOnce(src) {
  return new Promise((resolve, reject) => {
    if (document.querySelector(`script[src="${src}"]`)) {
      resolve();
      return;
    }
    const script = document.createElement("script");
    script.src = src;
    script.onload = () => resolve();
    script.onerror = () => reject(new Error(`載入 ${src} 失敗`));
    document.body.appendChild(script);
  });
}

export default function FollowPayPanel({ orderNo, onOrderNoChange, storeId }) {
  const [amount, setAmount] = useState("100");
  const [orderDesc, setOrderDesc] = useState("隨身付測試訂單");
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState(null);
  const [token, setToken] = useState(null);
  const [widgetReady, setWidgetReady] = useState(false);
  const loadedForOrderNo = useRef(null);

  async function handleGetToken(e) {
    e.preventDefault();
    setError(null);
    setToken(null);
    setWidgetReady(false);
    setSubmitting(true);
    try {
      const result = await createFollowPayToken({
        orderNo,
        amount: Number(amount),
        orderDesc,
        storeId,
      });
      if (result.retCode === "00" && result.token) {
        setToken(result.token);
      } else {
        setError({ title: "後端拒絕了這筆隨身付申請", detail: `retCode: ${result.retCode}` });
      }
    } catch (err) {
      setError({ title: "呼叫 /api/payment/follow-pay-token 失敗", detail: err.message });
    } finally {
      setSubmitting(false);
    }
  }

  useEffect(() => {
    if (!token || loadedForOrderNo.current === orderNo) return;
    loadedForOrderNo.current = orderNo;
    loadScriptOnce(JQUERY_SRC)
      .then(() => loadScriptOnce(FP_JS_TEST))
      .then(() => setWidgetReady(true))
      .catch((err) => setError({ title: "載入隨身付元件失敗", detail: err.message }));
  }, [token, orderNo]);

  return (
    <section className="panel">
      <h2 className="panel-title">隨身付（FOLLOW_PAY）</h2>
      <p className="panel-hint">
        跟「授權」是同一支 API，差別在拿到 token 後不整頁導頁，而是把 token 當成 iframe 的網址，
        搭配 hitrust-fp.js 在同一頁完成付款。
      </p>

      {!token && (
        <form onSubmit={handleGetToken}>
          <div className="field">
            <label htmlFor="fpOrderNo">訂單編號</label>
            <div className="field-row">
              <input
                id="fpOrderNo"
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
            <label htmlFor="fpAmount">金額（元）</label>
            <input
              id="fpAmount"
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
            <label htmlFor="fpOrderDesc">訂單描述</label>
            <textarea
              id="fpOrderDesc"
              className="tk-input"
              value={orderDesc}
              onChange={(e) => setOrderDesc(e.target.value)}
              maxLength={40}
            />
          </div>

          <button className="submit-btn" type="submit" disabled={submitting}>
            {submitting ? "取得 Token 中…" : "取得付款連結"}
          </button>
          {error && (
            <div className="form-error">
              {error.title}
              <br />
              <strong>{error.detail}</strong>
            </div>
          )}
        </form>
      )}

      {token && (
        <div>
          {!widgetReady && <p className="panel-hint">載入隨身付元件中…</p>}

          <iframe
            id="abgne_iframe"
            name="abgne_iframe"
            frameBorder="0"
            scrolling="no"
            width="500px"
            height="250px"
            src={token}
            title="hitrust-followpay"
          />
          <input type="hidden" id="followPayToken" name="followPayToken" value={token} readOnly />
          <div>
            <button type="button" id="btn-hitrustpay" name="btn-hitrustpay" className="submit-btn">
              Go
            </button>
          </div>

          {error && (
            <div className="form-error">
              {error.title}
              <br />
              <strong>{error.detail}</strong>
            </div>
          )}

          <button
            type="button"
            className="icon-btn"
            style={{ marginTop: 12 }}
            onClick={() => {
              setToken(null);
              loadedForOrderNo.current = null;
            }}
          >
            重新開始
          </button>
        </div>
      )}
    </section>
  );
}