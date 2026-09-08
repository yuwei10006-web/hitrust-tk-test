export default function OrderContextBar({
  orderNo,
  onChange,
  recent,
  onPick,
  storeId,
  onStoreIdChange,
}) {
  return (
    <div className="order-bar">
      <div className="field">
        <label htmlFor="ctxStoreId">商店代號（StoreID）</label>
        <input
          id="ctxStoreId"
          className="tk-input"
          value={storeId}
          onChange={(e) => onStoreIdChange(e.target.value.toUpperCase())}
          maxLength={19}
          pattern="[A-Za-z0-9]*"
          placeholder="留空 = 用後端預設值"
        />
      </div>
      <div className="field">
        <label htmlFor="ctxOrderNo">操作中的訂單編號</label>
        <input
          id="ctxOrderNo"
          className="tk-input"
          value={orderNo}
          onChange={(e) => onChange(e.target.value.toUpperCase())}
          maxLength={19}
          pattern="[A-Za-z0-9]+"
        />
        {recent.length > 0 && (
          <div className="recent-orders">
            {recent.map((o) => (
              <button type="button" key={o} className="recent-chip" onClick={() => onPick(o)}>
                {o}
              </button>
            ))}
          </div>
        )}
      </div>
    </div>
  );
}