function formatDate(value) {
  return new Date(value).toLocaleString();
}

export default function TransactionHistoryTable({ items, loading, error, emptyMessage }) {
  if (loading) {
    return <p className="empty-state">Loading transactions…</p>;
  }

  if (error) {
    return (
      <div className="error" role="alert">
        {error}
      </div>
    );
  }

  if (!items || items.length === 0) {
    return <p className="empty-state">{emptyMessage || "No transactions found."}</p>;
  }

  return (
    <ul className="transaction-list">
      {items.map((item) => (
        <li className="transaction-row" key={item.id}>
          <div className="transaction-info">
            <span className="transaction-type">
              {item.counterpartyName}
              <span className="muted-inline"> · {item.type}</span>
            </span>
            <span className="transaction-date">{formatDate(item.createdAt)}</span>
          </div>
          <span
            className={`transaction-amount ${item.direction === "CREDIT" ? "credit" : "debit"}`}
          >
            {item.direction === "CREDIT" ? "+" : "-"}
            {Number(item.amount).toFixed(2)}
          </span>
        </li>
      ))}
    </ul>
  );
}
