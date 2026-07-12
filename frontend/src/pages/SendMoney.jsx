import { useState } from "react";
import { Link } from "react-router-dom";
import client from "../api/client";

export default function SendMoney() {
  const [recipient, setRecipient] = useState("");
  const [amount, setAmount] = useState("");
  const [referenceId, setReferenceId] = useState(() => crypto.randomUUID());
  const [error, setError] = useState("");
  const [result, setResult] = useState(null);
  const [loading, setLoading] = useState(false);

  function validate() {
    if (!recipient.trim()) {
      return "Enter the recipient's email or phone number.";
    }
    const numeric = Number(amount);
    if (!amount || Number.isNaN(numeric) || numeric <= 0) {
      return "Enter a valid amount.";
    }
    return "";
  }

  async function handleSubmit() {
    const validationError = validate();
    if (validationError) {
      setError(validationError);
      return;
    }

    setError("");
    setResult(null);
    setLoading(true);
    try {
      const res = await client.post("/api/transfers", { recipient, amount, referenceId });
      setResult(res.data.data);
      setReferenceId(crypto.randomUUID());
      setRecipient("");
      setAmount("");
    } catch (err) {
      setError(err.response?.data?.error || "Transfer failed. Please try again.");
    } finally {
      setLoading(false);
    }
  }

  return (
    <div className="card">
      <h1>Send money</h1>
      <p className="subtitle">Transfer funds to another user by email or phone.</p>

      <label>
        Recipient
        <input
          value={recipient}
          onChange={(e) => setRecipient(e.target.value)}
          placeholder="name@example.com or 0100000000"
        />
      </label>

      <label>
        Amount (EGP)
        <input
          type="number"
          min="0.01"
          step="0.01"
          value={amount}
          onChange={(e) => setAmount(e.target.value)}
          placeholder="50.00"
        />
      </label>

      {error && (
        <div className="error" role="alert">
          {error}
        </div>
      )}

      {result && (
        <div className="notice" role="status">
          Sent {Number(result.amount).toFixed(2)} EGP successfully.
        </div>
      )}

      <button onClick={handleSubmit} disabled={loading}>
        {loading ? "Sending…" : "Send"}
      </button>

      <p className="muted">
        <Link to="/dashboard">Back to dashboard</Link>
      </p>
    </div>
  );
}
