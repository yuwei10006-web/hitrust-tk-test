import { useEffect, useState } from "react";
import { Link, useSearchParams } from "react-router-dom";
import { getOrder } from "../lib/api.js";
import { describeOrderStatus, formatAmount } from "../lib/orderStatus.js";

function StatusIcon({ success }) {
  if (success) {
    return (
      <svg className="status-icon success" viewBox="0 0 76 76">
        <circle className="ring" cx="38" cy="38" r="34" />
        <path className="mark" d="M23 39 L34 50 L54 27" />
      </svg>
    );
  }
  return (
    <svg className="status-icon error" viewBox="0 0 76 76">
      <circle className="ring" cx="38" cy="38" r="34" />
      <path className="mark" d="M27 27 L49 49 M49 27 L27 49" />
    </svg>
  );
}

export default function Result() {
  const [params] = useSearchParams();
  const orderNo = params.get("orderNo");
  const storeId = params.get("storeId");

  const [state, setState] = useState({ loading: true, order: null, error: null });

  useEffect(() => {
    if (!orderNo) {
      setState({ loading: false, order: null, error: "網址上沒有 orderNo 參數" });
      return;
    }
    let cancelled = false;
    getOrder(orderNo, storeId)
      .then((order) => {
        if (!cancelled) setState({ loading: false, order, error: null });
      })
      .catch((err) => {
        if (!cancelled) setState({ loading: false, order: null, error: err.message });
      });
    return () => {
      cancelled = true;
    };
  }, [orderNo, storeId]);



  return (
    <div className="result-shell">
      <Link to="/" className="back-link">
        ← 返回主控台
      </Link>

      {state.loading && <div className="loading-line">查詢訂單狀態中…</div>}

      {!state.loading && state.error && (
        <div className="status-hero">
          <StatusIcon success={false} />
          <h1 className="status-heading">查詢失敗</h1>
          <p className="status-sub">{state.error}</p>
        </div>
      )}

      {!state.loading && state.order && (
        <ResultDetail order={state.order} />
      )}
    </div>
  );
}

function ResultDetail({ order }) {
  const success = order.retCode === "00";
  const status = describeOrderStatus(order.orderStatus);
  const isBindCard = Boolean(order.trxToken);

  return (
    <>
      <div className="status-hero">
        <StatusIcon success={success} />
        <h1 className="status-heading">
          {isBindCard ? (success ? "綁卡成功" : "綁卡失敗") : (success ? "授權成功" : "授權失敗")}
        </h1>
        <p className="status-sub">retCode {order.retCode || "—"}</p>
        <span className={`badge tone-${status.tone} status-order-badge`}>{status.label}</span>
      </div>

      <div className="detail-grid">
        <div className="detail-cell">
          <div className="detail-label">訂單編號</div>
          <div className="detail-value">{order.orderNo || "—"}</div>
        </div>
        <div className="detail-cell">
          <div className="detail-label">核准金額</div>
          <div className="detail-value">{formatAmount(order.approveAmount)} {order.currency}</div>
        </div>
        <div className="detail-cell">
          <div className="detail-label">銀行授權碼</div>
          <div className="detail-value">{order.authCode || "—"}</div>
        </div>
        <div className="detail-cell">
          <div className="detail-label">銀行調單編號</div>
          <div className="detail-value">{order.authRRN || "—"}</div>
        </div>
        <div className="detail-cell">
          <div className="detail-label">授權方式</div>
          <div className="detail-value">{order.eci || "—"}</div>
        </div>
        <div className="detail-cell">
          <div className="detail-label">訂單日期</div>
          <div className="detail-value">{order.orderDate || "—"}</div>
        </div>
        <div className="detail-cell wide">
          <div className="detail-label">交易序號 trxToken</div>
          <div className="detail-value">{order.trxToken || "—"}</div>
        </div>
        <div className="detail-cell">
          <div className="detail-label">trxToken 有效期限</div>
          <div className="detail-value">{order.expiry || "—"}</div>
        </div>
        <div className="detail-cell wide">
          <div className="detail-label">請款 / 退款</div>
          <div className="detail-value">
            請款 {formatAmount(order.captureAmount)}　·　退款 {formatAmount(order.refundAmount)}
          </div>
        </div>
      </div>

      <details className="raw-toggle">
        <summary>查看完整回應 JSON</summary>
        <pre className="raw-json">{JSON.stringify(order, null, 2)}</pre>
      </details>
    </>
  );
}