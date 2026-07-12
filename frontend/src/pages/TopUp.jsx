import { useState } from "react";
import { Link } from "react-router-dom";
import client from "../api/client";

const MIN_AMOUNT = 10;
const MAX_AMOUNT = 5000;

export default function TopUp() {
  const [amount, setAmount] = useState("");
  const [referenceId, setReferenceId] = useState(() => crypto.randomUUID());
  const [error, setError] = useState("");
  const [result, setResult] = useState(null);
  const [loading, setLoading] = useState(false);

  function validate(value) {
    const numeric = Number(value);
    if (!value || Number.isNaN(numeric)) {
      return "Enter an amount.";
    }
    if (numeric < MIN_AMOUNT || numeric > MAX_AMOUNT) {
      return `Amount must be between ${MIN_AMOUNT} and ${MAX_AMOUNT}.`;
    }
    return "";
  }

  async function handleSubmit() {
    const validationError = validate(amount);
    if (validationError) {
      setError(validationError);
      return;
    }

    setError("");
    setLoading(true);
    try {
      const res = await client.post("/api/wallet/topup", { amount, referenceId });
      setResult(res.data.data);
      setReferenceId(crypto.randomUUID());
      setAmount("");
    } catch (err) {
      setError(err.response?.data?.error || "Top-up failed. Please try again.");
    } finally {
      setLoading(false);
    }
  }

  return (
    <div className="card">
      <h1>Top up your wallet</h1>
      <p className="subtitle">Simulated top-up — no real payment is charged.</p>

      <label>
        Amount ({MIN_AMOUNT}–{MAX_AMOUNT} EGP)
        <input
          type="number"
          min={MIN_AMOUNT}
          max={MAX_AMOUNT}
          step="0.01"
          value={amount}
          onChange={(e) => setAmount(e.target.value)}
          placeholder="100.00"
        />
      </label>
      <p className="hint">
        Minimum {MIN_AMOUNT}, maximum {MAX_AMOUNT} EGP per request.
      </p>

      {error && (
        <div className="error" role="alert">
          {error}
        </div>
      )}

      {result && (
        <div className="notice" role="status">
          Top-up successful. New balance will show on your dashboard.
        </div>
      )}

      <button onClick={handleSubmit} disabled={loading}>
        {loading ? "Processing…" : "Top up"}
      </button>

      <p className="muted">
        <Link to="/dashboard">Back to dashboard</Link>
      </p>
    </div>
  );
}
