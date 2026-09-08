# TK 授權測試主控台

給 HiTRUST TK 串接用的測試前端，不是正式結帳頁，純粹方便你自己測授權流程用。

## 啟動

```bash
npm install
npm run dev
```

會固定跑在 `http://localhost:5173`（跟後端 `application-dev.yml` 的 `frontend-result-url` 對應，不要改 port）。

## 前提

1. 後端 tk-demo 要先在 `http://localhost:8080` 跑起來。
2. 後端要加上 CORS 設定（放行 `http://localhost:5173`），不然 fetch 會被瀏覽器擋掉。
3. 如果後端網址不是 `localhost:8080`，複製 `.env.example` 成 `.env`，改 `VITE_API_BASE`。

## 流程

1. 首頁建立一筆測試訂單（訂單編號可以按 ↻ 重新產生），送出後會整頁導去 TrustPay 付款頁。
2. 輸入測試卡號完成付款。
3. TrustPay 會把你導回後端的 `/api/payment/return`，後端再轉導回這裡的 `/order/result?orderNo=...`。
4. 結果頁會自動打 `GET /api/payment/orders/{orderNo}` 查真正的交易結果並顯示。

測試紀錄存在瀏覽器的 localStorage，換瀏覽器/清快取會不見，純粹方便你自己回頭查，不是真的訂單資料庫。
