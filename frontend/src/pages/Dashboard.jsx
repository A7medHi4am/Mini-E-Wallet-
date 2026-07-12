import { useEffect, useState } from "react";
import { useNavigate, Link } from "react-router-dom";
import client from "../api/client";
import { clearToken } from "../auth/auth";

function describeTransaction(transaction, walletId) {
  switch (transaction.type) {
    case "TOPUP":
      return { label: "Top-up", isCredit: true };
    case "PAYMENT":
      return { label: "Payment", isCredit: false };
    case "TRANSFER":
      return transaction.senderWalletId === walletId
        ? { label: "Sent to user", isCredit: false }
        : { label: "Received from user", isCredit: true };
    case "REFUND":
      return transaction.receiverWalletId === walletId
        ? { label: "Refund", isCredit: true }
        : { label: "Refund issued", isCredit: false };
    default:
      return { label: transaction.type, isCredit: true };
  }
}

export default function Dashboard() {
  const navigate = useNavigate();
  const [profile, setProfile] = useState(null);
  const [walletId, setWalletId] = useState(null);
  const [transactions, setTransactions] = useState([]);
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    async function load() {
      try {
        const [profileRes, recentRes] = await Promise.all([
          client.get("/api/auth/user"),
          client.get("/api/wallet/transactions/recent"),
        ]);
        setProfile(profileRes.data.data);
        setWalletId(recentRes.data.data.walletId);
        setTransactions(recentRes.data.data.transactions);
      } catch {
        setError("Could not load your dashboard.");
      } finally {
        setLoading(false);
      }
    }
    load();
  }, []);

  function handleLogout() {
    clearToken();
    navigate("/login");
  }

  if (loading) return <div className="card">Loading…</div>;
  if (error) return <div className="card error">{error}</div>;

  const wallet = profile.wallet || {};

  return (
    <div className="card">
      <h1>Dashboard</h1>
      <p className="subtitle">Your wallet at a glance.</p>

      <div className="balance">
        <span className="balance-label">Wallet balance</span>
        <span className="balance-amount">
          {wallet.balance != null ? Number(wallet.balance).toFixed(2) : "0.00"}{" "}
          {wallet.currency || "EGP"}
        </span>
        {wallet.status && (
          <span className={`badge ${wallet.status.toLowerCase()}`}>
            {wallet.status}
          </span>
        )}
      </div>

      <div className="actions">
        <Link to="/topup" className="button-link primary">
          Top up
        </Link>
        <Link to="/profile" className="button-link secondary">
          Profile
        </Link>
      </div>

      <h2 className="section-title">Recent transactions</h2>

      {transactions.length === 0 ? (
        <p className="empty-state">No transactions yet — top up to get started.</p>
      ) : (
        <ul className="transaction-list">
          {transactions.map((transaction) => {
            const { label, isCredit } = describeTransaction(transaction, walletId);
            return (
              <li className="transaction-row" key={transaction.id}>
                <div className="transaction-info">
                  <span className="transaction-type">{label}</span>
                  <span className="transaction-date">
                    {new Date(transaction.createdAt).toLocaleString()}
                  </span>
                </div>
                <span className={`transaction-amount ${isCredit ? "credit" : "debit"}`}>
                  {isCredit ? "+" : "-"}
                  {Number(transaction.amount).toFixed(2)}
                </span>
              </li>
            );
          })}
        </ul>
      )}

      <button className="secondary" onClick={handleLogout}>
        Log out
      </button>
    </div>
  );
}
