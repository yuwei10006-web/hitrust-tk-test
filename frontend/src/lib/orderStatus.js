// 依手冊 Chapter 7、參考資料 II「訂單狀態總表」整理，之後手冊改版有新代碼直接加在這裡。
export const ORDER_STATUS = {
  "02": { label: "已授權", tone: "info" },
  "03": { label: "已請款", tone: "info" },
  "04": { label: "請款已清算", tone: "success" },
  "06": { label: "已退款", tone: "neutral" },
  "08": { label: "退款已清算", tone: "neutral" },
  "12": { label: "訂單已取消", tone: "neutral" },
  ZY: { label: "授權失敗", tone: "error" },
  ZV: { label: "此筆請退款已送至銀行", tone: "info" },
  ZU: { label: "系統正在跟銀行清算中", tone: "info" },
  ZT: { label: "此筆請款或退款交易動作已被取消", tone: "neutral" },
  ZZ: { label: "此筆授權交易動作已被取消", tone: "neutral" },
  ZX: { label: "銀行未回應，此筆交易失敗", tone: "error" },
  ZS: { label: "無效交易", tone: "error" },
  ZP: { label: "訂單付款/審核中", tone: "info" },
  ZF: { label: "交易失敗", tone: "error" },
  ZO: { label: "付款期限截止", tone: "error" },
};

export function describeOrderStatus(code) {
  return ORDER_STATUS[code] || { label: code || "未知狀態", tone: "neutral" };
}

// TrustPay 金額字串是「元 * 100」，例如 "10000" 代表 100.00 元
export function formatAmount(raw) {
  if (raw == null || raw === "") return "—";
  const n = Number(raw);
  if (Number.isNaN(n)) return raw;
  return (n / 100).toLocaleString("zh-Hant-TW", { minimumFractionDigits: 2, maximumFractionDigits: 2 });
}
