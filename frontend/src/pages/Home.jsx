import { useEffect, useState } from "react";
import OperationNav from "../components/OperationNav.jsx";
import OrderContextBar from "../components/OrderContextBar.jsx";
import AuthorizePanel from "../panels/AuthorizePanel.jsx";
import CapturePanel from "../panels/CapturePanel.jsx";
import QueryPanel from "../panels/QueryPanel.jsx";
import VoidAuthorizePanel from "../panels/VoidAuthorizePanel.jsx";
import { genOrderNo } from "../lib/orderNo.js";
import { loadRecentOrders, pushRecentOrder } from "../lib/recentOrders.js";
import { loadStoreId, saveStoreId } from "../lib/storeId.js";
import VoidCapturePanel from "../panels/VoidCapturePanel.jsx";
import RefundPanel from "../panels/RefundPanel.jsx";
import VoidRefundPanel from "../panels/VoidRefundPanel.jsx";
import DirectAuthorizePanel from "../panels/DirectAuthorizePanel.jsx";
import BindCardPanel from "../panels/BindCardPanel.jsx";
import DirectAuthorizeTrxTokenPanel from "../panels/DirectAuthorizeTrxTokenPanel.jsx";
import AuthorizeTrxTokenPanel from "../panels/AuthorizeTrxTokenPanel.jsx";
import SipPanel from "../panels/SipPanel.jsx";
import SipQueryPanel from "../panels/SipQueryPanel.jsx";
import SipCancelPanel from "../panels/SipCancelPanel.jsx";
import FollowPayPanel from "../panels/FollowPayPanel.jsx";
import ApplePayPanel from "../panels/ApplePayPanel.jsx";
import LinePayPanel from "../panels/LinePayPanel.jsx";

export default function Home() {
  const [active, setActive] = useState("authorize");
  const [orderNo, setOrderNo] = useState(genOrderNo());
  const [recent, setRecent] = useState([]);
  const [storeId, setStoreId] = useState("");

  useEffect(() => {
    setRecent(loadRecentOrders());
    setStoreId(loadStoreId());
  }, []);

  function recordUsed(no) {
    setRecent(pushRecentOrder(no));
  }

  function handleStoreIdChange(next) {
    setStoreId(next);
    saveStoreId(next);
  }

  return (
    <div className="shell">
      <header className="masthead">
        <div>
          <h1 className="masthead-title">交易測試主控台</h1>
          <p className="masthead-sub">左側選擇要測試的交易類型，對同一筆訂單依序操作</p>
        </div>
        <span className="masthead-tag">TrustPay TK · 測試環境</span>
      </header>

      <div className="console-layout">
        <OperationNav active={active} onSelect={setActive} />
        <div>
          <OrderContextBar
            orderNo={orderNo}
            onChange={setOrderNo}
            recent={recent}
            onPick={setOrderNo}
            storeId={storeId}
            onStoreIdChange={handleStoreIdChange}
          />
          {active === "authorize" && (
            <AuthorizePanel
              orderNo={orderNo}
              onOrderNoChange={setOrderNo}
              onSuccess={recordUsed}
              storeId={storeId}
            />
          )}
          {active === "voidAuthorize" && (
            <VoidAuthorizePanel orderNo={orderNo} onVoided={recordUsed} storeId={storeId} />
          )}
          {active === "capture" && (
            <CapturePanel orderNo={orderNo} onCaptured={recordUsed} storeId={storeId} />
          )}
          {active === "query" && <QueryPanel orderNo={orderNo} storeId={storeId} />}
          {active === "voidCapture" && (
            <VoidCapturePanel orderNo={orderNo} onVoided={recordUsed} storeId={storeId} />
          )}
          {active === "refund" && (
            <RefundPanel orderNo={orderNo} onRefunded={recordUsed} storeId={storeId} />
          )}
          {active === "voidRefund" && (
            <VoidRefundPanel orderNo={orderNo} onVoided={recordUsed} storeId={storeId} />
          )}
          {active === "directAuthorize" && (
            <DirectAuthorizePanel
              orderNo={orderNo}
              onOrderNoChange={setOrderNo}
              onSuccess={recordUsed}
              storeId={storeId}
            />
          )}
          {active === "bindCard" && (
            <BindCardPanel
              orderNo={orderNo}
              onOrderNoChange={setOrderNo}
              onSuccess={recordUsed}
              storeId={storeId}
            />
          )}
          {active === "directAuthorizeTrxToken" && (
            <DirectAuthorizeTrxTokenPanel
              orderNo={orderNo}
              onOrderNoChange={setOrderNo}
              onSuccess={recordUsed}
              storeId={storeId}
            />
          )}
          {active === "authorizeTrxToken" && (
            <AuthorizeTrxTokenPanel
              orderNo={orderNo}
              onOrderNoChange={setOrderNo}
              onSuccess={recordUsed}
              storeId={storeId}
            />
          )}
          {active === "sip" && (
            <SipPanel
              orderNo={orderNo}
              onOrderNoChange={setOrderNo}
              onSuccess={recordUsed}
              storeId={storeId}
            />
          )}
          {active === "sipQuery" && <SipQueryPanel orderNo={orderNo} storeId={storeId} />}
          {active === "sipCancel" && (
            <SipCancelPanel orderNo={orderNo} onCancelled={recordUsed} storeId={storeId} />
          )}
          {active === "followPay" && (
            <FollowPayPanel orderNo={orderNo} onOrderNoChange={setOrderNo} storeId={storeId} />
          )}
          {active === "applePay" && (
            <ApplePayPanel orderNo={orderNo} onOrderNoChange={setOrderNo} storeId={storeId} />
          )}
          {active === "linePay" && (
            <LinePayPanel
              orderNo={orderNo}
              onOrderNoChange={setOrderNo}
              onUsed={recordUsed}
              storeId={storeId}
            />
          )}
        </div>
      </div>
    </div>
  );
}