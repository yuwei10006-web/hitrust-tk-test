export const OPERATIONS = [
  { key: "authorize", label: "授權" },
  { key: "directAuthorize", label: "直接授權" },
  { key: "voidAuthorize", label: "取消授權" },
  { key: "capture", label: "請款" },
  { key: "query", label: "查詢" },
  { key: "voidCapture", label: "取消請款"},
  { key: "refund", label: "退款"},
  { key: "voidRefund", label: "取消退款"},
  { key: "bindCard", label: "信用卡綁卡"},
  { key: "directAuthorizeTrxToken", label: "直接授權(序號)"},
  { key: "authorizeTrxToken", label: "授權(序號)"},
  { key: "sip", label: "創建定期定額"},
  { key: "sipQuery", label: "定期定額查詢"},
  { key: "sipCancel", label: "定期定額取消"},
  { key: "followPay", label: "隨身付"},
  { key: "applePay", label: "Apple Pay"},
  { key: "linePay", label: "LINE Pay"},
];

export default function OperationNav({ active, onSelect }) {
  return (
    <nav className="op-nav">
      {OPERATIONS.map((op) => (
        <button
          key={op.key}
          type="button"
          className={`op-nav-item ${active === op.key ? "active" : ""}`}
          disabled={op.soon}
          onClick={() => onSelect(op.key)}
        >
          <span>{op.label}</span>
          {op.soon && <span className="op-nav-soon">籌備中</span>}
        </button>
      ))}
    </nav>
  );
}