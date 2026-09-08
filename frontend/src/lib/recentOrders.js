const KEY = "tk-console-recent-orders";

export function loadRecentOrders() {
  try {
    return JSON.parse(localStorage.getItem(KEY)) || [];
  } catch {
    return [];
  }
}

export function pushRecentOrder(orderNo) {
  const next = [
    orderNo,
    ...loadRecentOrders().filter((o) => o !== orderNo),
  ].slice(0, 8);
  localStorage.setItem(KEY, JSON.stringify(next));
  return next;
}
