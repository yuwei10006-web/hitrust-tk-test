const KEY = "tk-console-store-id";

/** 空字串代表「不覆蓋，用後端設定檔的預設 StoreID」 */
export function loadStoreId() {
  try {
    return localStorage.getItem(KEY) || "";
  } catch {
    return "";
  }
}

export function saveStoreId(storeId) {
  try {
    localStorage.setItem(KEY, storeId || "");
  } catch {
    // localStorage 不可用就算了，純粹是方便記住上次用哪個商店代號
  }
}
