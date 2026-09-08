# TK Demo — HiTRUST TrustPay 串接測試專案

給 HiTRUST（網際威信）B2C Toolkit / TrustPay 串接用的測試專案，包含 Spring Boot 後端與 Vite/React 前端，涵蓋授權、直接授權、請款、退款、取消授權/請款/退款、綁卡、交易序號標記、定期定額（SIP）、隨身付、Apple Pay 等功能。純測試用途，不是正式結帳頁。

## 專案結構

\`\`\`
backend/    Spring Boot 後端，實際呼叫 HiTRUST toolkit（libs/toolkit.jar）
frontend/   Vite + React 測試主控台，對應各項付款功能的操作面板
\`\`\`

各自的啟動方式請看：
- 後端：`backend/HELP.md`
- 前端：`frontend/README.md`

## 快速開始

1. 後端先跑起來（預設 `http://localhost:8080`）：
   \`\`\`bash
   cd backend
   ./mvnw spring-boot:run
   \`\`\`
2. 前端另開一個終端機：
   \`\`\`bash
   cd frontend
   npm install
   npm run dev
   \`\`\`
   預設跑在 `http://localhost:5173`，對應後端 `application-dev.yml` 的 `frontend-result-url`，兩邊 port 不要隨意改。

3. 商店代號（storeId）設定對應 `backend/src/main/resources/*.conf`，每個 `.conf` 都指向 `backend/keys/` 下對應的 RSA 私鑰。新增商店時要同時準備好 `.conf` + 私鑰檔，並跟網際威信客服登記對應的 Return/Update URL 白名單。

## 完整請求/回應 Log

`CreditCardPaymentService` 所有交易（authorize、directAuthorize、capture、refund、voidAuthorize、voidCapture、voidRefund、query、decryptUpdate、bindCard、directAuthorizeTrxToken、authorizeTrxToken、createSipOrder、querySip、cancelSip、followPayToken、applePay）都統一透過內部的 `transact(action, trx)` 方法呼叫 toolkit，執行完後會把 toolkit 內建的 `getRequestMessage()` / `getResponseMessage()`（送出去的完整封包、收到的完整回應）印到 log。

之後新增新的交易類型時，只要用 `transact("動作名稱", trxObject)` 取代直接呼叫 `trxObject.transaction()`，就會自動有完整 log，不用在每個方法裡各自寫。

Log 設定在 `backend/src/main/resources/logback.xml`：INFO 等級，同時輸出到 console 跟 `backend/logs/toolkit.log`（依日期滾動）。

**注意**：`directAuthorize`、`authorizeTrxToken` 等功能的請求封包裡會帶卡號（PAN）、CVV，上線前請實際跑一筆測試交易、檢查 log 內容，確認沒有明文卡片資訊落地；正式環境的 log 存取權限、保留天數也建議一併盤點。

## 安全性提醒

- `backend/keys/` 底下是各商店的 RSA 私鑰（`.DER`），**不要進版控**，已加進 `.gitignore`。
- `backend/logs/` 因為改動後會記錄完整交易封包，同樣不進版控。
- `application-prod.yml` 所有設定都吃環境變數（`TK_STORE_ID`、`TK_RETURN_URL`…），沒有寫死任何機密值，部署時透過環境變數或 secret 管理工具帶入即可。