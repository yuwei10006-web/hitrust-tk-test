import { useState } from "react";
import AuthorizePanel from "./AuthorizePanel.jsx";
import VoidAuthorizePanel from "./VoidAuthorizePanel.jsx";
import CapturePanel from "./CapturePanel.jsx";
import RefundPanel from "./RefundPanel.jsx";
import QueryPanel from "./QueryPanel.jsx";

const SUB_OPERATIONS = [
  { key: "authorize", label: "授權" },
  { key: "voidAuthorize", label: "取消授權" },
  { key: "capture", label: "請款" },
  { key: "refund", label: "退款" },
  { key: "query", label: "查詢" },
];

/**
 * LINE Pay。手冊 XV~XVIII 章確認：
 * - 授權/取消授權/請款/退款/查詢，用的 class 跟欄位都跟信用卡完全一樣
 *   （B2CPayAuth、B2CPayOther type=8/3/5/7），沒有任何 LINE Pay 專屬欄位
 * - 唯一差異是 storeId 要換成網際威信核發的「LINE Pay 專用商店代號」，
 *   所以這裡直接複用信用卡那 5 個元件，不重寫任何呼叫邏輯
 */
export default function LinePayPanel({ orderNo, onOrderNoChange, onUsed, storeId }) {
  const [sub, setSub] = useState("authorize");

  return (
    <section className="panel">
      <h2 className="panel-title">LINE Pay</h2>
      <p className="panel-hint">
        跟信用卡共用同一套授權/取消授權/請款/退款/查詢邏輯，差別只在上方「商店代號」要換成
        LINE Pay 專用的測試商店代號。
      </p>

      <nav className="op-nav" style={{ marginBottom: 16 }}>
        {SUB_OPERATIONS.map((op) => (
          <button
            key={op.key}
            type="button"
            className={`op-nav-item ${sub === op.key ? "active" : ""}`}
            onClick={() => setSub(op.key)}
          >
            <span>{op.label}</span>
          </button>
        ))}
      </nav>

      {sub === "authorize" && (
        <AuthorizePanel
          orderNo={orderNo}
          onOrderNoChange={onOrderNoChange}
          onSuccess={onUsed}
          storeId={storeId}
        />
      )}
      {sub === "voidAuthorize" && (
        <VoidAuthorizePanel orderNo={orderNo} onVoided={onUsed} storeId={storeId} />
      )}
      {sub === "capture" && (
        <CapturePanel orderNo={orderNo} onCaptured={onUsed} storeId={storeId} />
      )}
      {sub === "refund" && (
        <RefundPanel orderNo={orderNo} onRefunded={onUsed} storeId={storeId} />
      )}
      {sub === "query" && <QueryPanel orderNo={orderNo} storeId={storeId} />}
    </section>
  );
}