import { useEffect, useState } from "react";
import { Link, useParams } from "react-router-dom";
import client from "../api/client";

export default function PayMerchant() {
  const { merchantId } = useParams();

  const [merchant, setMerchant] = useState(null);
  const [loadError, setLoadError] = useState("");
  const [loading, setLoading] = useState(true);

  const [amount, setAmount] = useState("");
  const [referenceId, setReferenceId] = useState(() => crypto.randomUUID());
  const [error, setError] = useState("");
  const [result, setResult] = useState(null);
  const [paying, setPaying] = useState(false);

  useEffect(() => {
    let cancelled = false;

    async function load() {
      setLoading(true);
      setLoadError("");
      try {
        const res = await client.get(`/api/merchants/${merchantId}`);
        if (!cancelled) {
          setMerchant(res.data.data);
        }
      } catch {
        if (!cancelled) {
          setLoadError("Merchant not found.");
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
  }, [merchantId]);

  async function handleSubmit() {
    const numeric = Number(amount);
    if (!amount || Number.isNaN(numeric) || numeric <= 0) {
      setError("Enter a valid amount.");
      return;
    }

    setError("");
    setResult(null);
    setPaying(true);
    try {
      const res = await client.post("/api/payments", { merchantId: Number(merchantId), amount, referenceId });
      setResult(res.data.data);
      setReferenceId(crypto.randomUUID());
      setAmount("");
    } catch (err) {
      setError(err.response?.data?.error || "Payment failed. Please try again.");
    } finally {
      setPaying(false);
    }
  }

  if (loading) return <div className="card">Loading…</div>;
  if (loadError) return <div className="card error">{loadError}</div>;

  return (
    <div className="card">
      <h1>Pay {merchant.name}</h1>
      <p className="subtitle">{merchant.category}</p>

      {!merchant.active && (
        <div className="error" role="alert">
          This merchant is not currently accepting payments.
        </div>
      )}

      <label>
        Amount (EGP)
        <input
          type="number"
          min="0.01"
          step="0.01"
          value={amount}
          onChange={(e) => setAmount(e.target.value)}
          placeholder="20.00"
          disabled={!merchant.active}
        />
      </label>

      {error && (
        <div className="error" role="alert">
          {error}
        </div>
      )}

      {result && (
        <div className="notice" role="status">
          Paid {Number(result.amount).toFixed(2)} EGP to {merchant.name}.
        </div>
      )}

      <button onClick={handleSubmit} disabled={paying || !merchant.active}>
        {paying ? "Paying…" : "Pay"}
      </button>

      <p className="muted">
        <Link to="/merchants">Back to merchants</Link>
      </p>
    </div>
  );
}
