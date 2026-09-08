const API_BASE = import.meta.env.VITE_API_BASE || "http://localhost:8080";

async function parseOrThrow(res) {
  const text = await res.text();
  let body;
  try {
    body = text ? JSON.parse(text) : null;
  } catch {
    body = { raw: text };
  }
  if (!res.ok) {
    const message = body?.error || `HTTP ${res.status}`;
    const err = new Error(message);
    err.status = res.status;
    err.body = body;
    throw err;
  }
  return body;
}

export async function authorize({ orderNo, amount, orderDesc, storeId }) {
  const res = await fetch(`${API_BASE}/api/payment/authorize`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({
      orderNo,
      amount,
      orderDesc,
      storeId: storeId || undefined,
    }),
  });
  return parseOrThrow(res);
}

export async function getOrder(orderNo, storeId) {
  const url = new URL(
    `${API_BASE}/api/payment/orders/${encodeURIComponent(orderNo)}`,
  );
  if (storeId) url.searchParams.set("storeId", storeId);
  const res = await fetch(url);
  return parseOrThrow(res);
}

export async function capture({ orderNo, amount, storeId }) {
  const res = await fetch(`${API_BASE}/api/payment/capture`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ orderNo, amount, storeId: storeId || undefined }),
  });
  return parseOrThrow(res);
}

export async function voidAuthorize({ orderNo, amount, storeId }) {
  const res = await fetch(`${API_BASE}/api/payment/void-authorize`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({
      orderNo,
      amount: amount || undefined,
      storeId: storeId || undefined,
    }),
  });
  return parseOrThrow(res);
}

export async function voidCapture({ orderNo, amount, storeId }) {
  const res = await fetch(`${API_BASE}/api/payment/void-capture`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({
      orderNo,
      amount: amount || undefined,
      storeId: storeId || undefined,
    }),
  });
  return parseOrThrow(res);
}

export async function refund({ orderNo, amount, storeId }) {
  const res = await fetch(`${API_BASE}/api/payment/refund`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ orderNo, amount, storeId: storeId || undefined }),
  });
  return parseOrThrow(res);
}

export async function voidRefund({ orderNo, amount, storeId }) {
  const res = await fetch(`${API_BASE}/api/payment/void-refund`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({
      orderNo,
      amount: amount || undefined,
      storeId: storeId || undefined,
    }),
  });
  return parseOrThrow(res);
}

export async function directAuthorize({
  orderNo,
  amount,
  orderDesc,
  pan,
  expiry,
  cvv,
  depositFlag,
  storeId,
}) {
  const res = await fetch(`${API_BASE}/api/payment/direct-authorize`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({
      orderNo,
      amount,
      orderDesc,
      pan,
      expiry,
      cvv: cvv || undefined,
      depositFlag: depositFlag || undefined,
      storeId: storeId || undefined,
    }),
  });
  return parseOrThrow(res);
}

export async function bindCard({ orderNo, amount, orderDesc, storeId }) {
  const res = await fetch(`${API_BASE}/api/payment/bind-card`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({
      orderNo,
      amount,
      orderDesc,
      storeId: storeId || undefined,
    }),
  });
  return parseOrThrow(res);
}

export async function directAuthorizeTrxToken({
  orderNo,
  amount,
  orderDesc,
  trxToken,
  expiry,
  depositFlag,
  storeId,
}) {
  const res = await fetch(`${API_BASE}/api/payment/direct-authorize-trxtoken`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({
      orderNo,
      amount,
      orderDesc,
      trxToken,
      expiry,
      depositFlag: depositFlag || undefined,
      storeId: storeId || undefined,
    }),
  });
  return parseOrThrow(res);
}

export async function authorizeTrxToken({
  orderNo,
  amount,
  orderDesc,
  trxToken,
  expiry,
  cvv,
  depositFlag,
  storeId,
}) {
  const res = await fetch(`${API_BASE}/api/payment/authorize-trxtoken`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({
      orderNo,
      amount,
      orderDesc,
      trxToken,
      expiry,
      cvv: cvv || undefined,
      depositFlag: depositFlag || undefined,
      storeId: storeId || undefined,
    }),
  });
  return parseOrThrow(res);
}

export async function createSipOrder({
  orderNo,
  amount,
  orderDesc,
  periodCount,
  periodCycle,
  periodUnit,
  storeId,
}) {
  const res = await fetch(`${API_BASE}/api/payment/sip-authorize`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({
      orderNo,
      amount,
      orderDesc,
      periodCount,
      periodCycle,
      periodUnit,
      storeId: storeId || undefined,
    }),
  });
  return parseOrThrow(res);
}

export async function getSipOrder(orderNo, storeId) {
  const url = new URL(
    `${API_BASE}/api/payment/sip-orders/${encodeURIComponent(orderNo)}`,
  );
  if (storeId) url.searchParams.set("storeId", storeId);
  const res = await fetch(url);
  return parseOrThrow(res);
}

export async function cancelSipOrder({ orderNo, storeId }) {
  const res = await fetch(`${API_BASE}/api/payment/sip-cancel`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ orderNo, storeId: storeId || undefined }),
  });
  return parseOrThrow(res);
}

export async function createFollowPayToken({
  orderNo,
  amount,
  orderDesc,
  storeId,
}) {
  const res = await fetch(`${API_BASE}/api/payment/follow-pay-token`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({
      orderNo,
      amount,
      orderDesc,
      storeId: storeId || undefined,
    }),
  });
  return parseOrThrow(res);
}

export async function chargeApplePay({ orderNo, amount, orderDesc, paymentData, storeId }) {
  const res = await fetch(`${API_BASE}/api/payment/apple-pay/charge`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({
      orderNo,
      amount,
      orderDesc,
      paymentData,
      storeId: storeId || undefined,
    }),
  });
  return parseOrThrow(res);
}
