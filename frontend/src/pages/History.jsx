import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import client from "../api/client";
import TransactionHistoryTable from "../components/TransactionHistoryTable";

const PAGE_SIZE = 15;

export default function History() {
  const [type, setType] = useState("");
  const [from, setFrom] = useState("");
  const [to, setTo] = useState("");
  const [page, setPage] = useState(0);

  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    let cancelled = false;

    async function load() {
      setLoading(true);
      setError("");
      try {
        const params = { page, size: PAGE_SIZE };
        if (type) params.type = type;
        if (from) params.from = from;
        if (to) params.to = to;

        const res = await client.get("/api/history", { params });
        if (!cancelled) {
          setData(res.data.data);
        }
      } catch {
        if (!cancelled) {
          setError("Could not load transaction history.");
        }
      } finally {
        if (!cancelled) {
          setLoading(false);
        }
      }
    }

    load();
    return () => {
      cancelled = true;
    };
  }, [type, from, to, page]);

  function handleFilterChange(setter) {
    return (event) => {
      setter(event.target.value);
      setPage(0);
    };
  }

  return (
    <div className="card card-wide">
      <h1>Transaction history</h1>
      <p className="subtitle">All your top-ups, transfers, and payments.</p>
      <Link to="/dashboard" className="nav-link nav-link-top">
        Back to dashboard
      </Link>

      <div className="filters">
        <label>
          Type
          <select value={type} onChange={handleFilterChange(setType)}>
            <option value="">All</option>
            <option value="TOPUP">Top-up</option>
            <option value="TRANSFER">Transfer</option>
            <option value="PAYMENT">Payment</option>
            <option value="REFUND">Refund</option>
          </select>
        </label>

        <label>
          From
          <input type="date" value={from} onChange={handleFilterChange(setFrom)} />
        </label>

        <label>
          To
          <input type="date" value={to} onChange={handleFilterChange(setTo)} />
        </label>
      </div>

      <TransactionHistoryTable
        items={data?.content}
        loading={loading}
        error={error}
        emptyMessage="No transactions match these filters."
      />

      {data && data.totalElements > 0 && (
        <div className="pagination">
          <button
            className="secondary"
            disabled={page === 0}
            onClick={() => setPage((p) => Math.max(0, p - 1))}
          >
            Previous
          </button>
          <span className="pagination-status">
            Page {data.page + 1} of {data.totalPages}
          </span>
          <button
            className="secondary"
            disabled={data.last}
            onClick={() => setPage((p) => p + 1)}
          >
            Next
          </button>
        </div>
      )}
    </div>
  );
}
