import { useState } from "react";
import { Link } from "react-router-dom";
import { Elements } from "@stripe/react-stripe-js";
import { loadStripe } from "@stripe/stripe-js";
import client from "../api/client";
import StripeCheckoutForm from "../components/StripeCheckoutForm";
import PaymentResultModal from "../components/PaymentResultModal";

const MIN_AMOUNT = 10;
const MAX_AMOUNT = 5000;

const stripePromise = loadStripe(import.meta.env.VITE_STRIPE_PUBLISHABLE_KEY);

function validateAmount(value) {
  const numeric = Number(value);
  if (!value || Number.isNaN(numeric)) {
    return "Enter an amount.";
  }
  if (numeric < MIN_AMOUNT || numeric > MAX_AMOUNT) {
    return `Amount must be between ${MIN_AMOUNT} and ${MAX_AMOUNT}.`;
  }
  return "";
}

export default function TopUp() {
  const [mode, setMode] = useState("simulated");

  // simulated flow
  const [amount, setAmount] = useState("");
  const [referenceId, setReferenceId] = useState(() => crypto.randomUUID());
  const [error, setError] = useState("");
  const [result, setResult] = useState(null);
  const [loading, setLoading] = useState(false);

  // card (Stripe) flow
  const [cardAmount, setCardAmount] = useState("");
  const [cardError, setCardError] = useState("");
  const [clientSecret, setClientSecret] = useState("");
  const [startingCheckout, setStartingCheckout] = useState(false);
  const [paymentResult, setPaymentResult] = useState(null);

  function switchMode(next) {
    setMode(next);
    setError("");
    setCardError("");
  }

  async function handleSimulatedSubmit() {
    const validationError = validateAmount(amount);
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

  async function handleStartCheckout() {
    const validationError = validateAmount(cardAmount);
    if (validationError) {
      setCardError(validationError);
      return;
    }

    setCardError("");
    setStartingCheckout(true);
    try {
      const res = await client.post("/api/wallet/topup/checkout", { amount: cardAmount });
      setClientSecret(res.data.data.clientSecret);
    } catch (err) {
      setCardError(err.response?.data?.error || "Could not start checkout. Please try again.");
    } finally {
      setStartingCheckout(false);
    }
  }

  function closePaymentResult() {
    setPaymentResult(null);
    setClientSecret("");
    setCardAmount("");
  }

  return (
    <div className="card">
      <h1>Top up your wallet</h1>
      <p className="subtitle">Add money via a simulated top-up or a real card payment.</p>

      <div className="tabs">
        <button
          type="button"
          className={`tab ${mode === "simulated" ? "active" : ""}`}
          onClick={() => switchMode("simulated")}
        >
          Simulated
        </button>
        <button
          type="button"
          className={`tab ${mode === "card" ? "active" : ""}`}
          onClick={() => switchMode("card")}
        >
          Card (Stripe)
        </button>
      </div>

      {mode === "simulated" && (
        <>
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
            Minimum {MIN_AMOUNT}, maximum {MAX_AMOUNT} EGP per request. No real payment is charged.
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

          <button onClick={handleSimulatedSubmit} disabled={loading}>
            {loading ? "Processing…" : "Top up"}
          </button>
        </>
      )}

      {mode === "card" && !clientSecret && (
        <>
          <label>
            Amount ({MIN_AMOUNT}–{MAX_AMOUNT} EGP)
            <input
              type="number"
              min={MIN_AMOUNT}
              max={MAX_AMOUNT}
              step="0.01"
              value={cardAmount}
              onChange={(e) => setCardAmount(e.target.value)}
              placeholder="100.00"
            />
          </label>
          <p className="hint">
            Test card: 4242 4242 4242 4242, any future expiry, any CVC.
          </p>

          {cardError && (
            <div className="error" role="alert">
              {cardError}
            </div>
          )}

          <button onClick={handleStartCheckout} disabled={startingCheckout}>
            {startingCheckout ? "Preparing…" : "Continue to payment"}
          </button>
        </>
      )}

      {mode === "card" && clientSecret && (
        <Elements stripe={stripePromise} options={{ clientSecret }}>
          <StripeCheckoutForm onResult={setPaymentResult} />
        </Elements>
      )}

      <p className="muted">
        <Link to="/dashboard">Back to dashboard</Link>
      </p>

      <PaymentResultModal result={paymentResult} onClose={closePaymentResult} />
    </div>
  );
}
