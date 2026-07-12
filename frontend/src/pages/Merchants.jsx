import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import client from "../api/client";

export default function Merchants() {
  const [query, setQuery] = useState("");
  const [merchants, setMerchants] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    let cancelled = false;

    async function load() {
      setLoading(true);
      setError("");
      try {
        const res = await client.get("/api/merchants", { params: { query, size: 30 } });
        if (!cancelled) {
          setMerchants(res.data.data.content);
        }
      } catch {
        if (!cancelled) {
          setError("Could not load merchants.");
        }
      } finally {
        if (!cancelled) {
          setLoading(false);
        }
      }
    }

    const timeout = setTimeout(load, 250); // debounce search input
    return () => {
      cancelled = true;
      clearTimeout(timeout);
    };
  }, [query]);

  return (
    <div className="card card-wide">
      <h1>Pay a merchant</h1>
      <p className="subtitle">Search for a merchant to send a payment.</p>
      <Link to="/dashboard" className="nav-link nav-link-top">
        Back to dashboard
      </Link>

      <div className="search-row">
        <input
          value={query}
          onChange={(e) => setQuery(e.target.value)}
          placeholder="Search merchants by name…"
        />
      </div>

      {error && (
        <div className="error" role="alert">
          {error}
        </div>
      )}

      {loading ? (
        <p className="empty-state">Loading merchants…</p>
      ) : merchants.length === 0 ? (
        <p className="empty-state">No merchants found.</p>
      ) : (
        <ul className="merchant-list">
          {merchants.map((merchant) => (
            <li className="merchant-row" key={merchant.id}>
              <div className="merchant-info">
                <span className="merchant-name">{merchant.name}</span>
                <span className="merchant-category">{merchant.category}</span>
              </div>
              <Link to={`/pay/${merchant.id}`} className="button-link">
                Pay
              </Link>
            </li>
          ))}
        </ul>
      )}
    </div>
  );
}
