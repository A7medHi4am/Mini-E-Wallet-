import { useState } from "react";
import { PaymentElement, useElements, useStripe } from "@stripe/react-stripe-js";
import client from "../api/client";

const POLL_ATTEMPTS = 10;
const POLL_DELAY_MS = 1500;

function wait(ms) {
  return new Promise((resolve) => setTimeout(resolve, ms));
}

// Stripe's own confirmPayment() only tells us the card was charged — it says
// nothing about whether our webhook has actually credited the wallet yet
// (that happens async, server-to-server, on its own timeline). This polls
// our backend, which only reports "succeeded" once the webhook has landed.
async function waitForWebhookConfirmation(paymentIntentId) {
  for (let attempt = 0; attempt < POLL_ATTEMPTS; attempt++) {
    const res = await client.get(`/api/wallet/topup/checkout/${paymentIntentId}/status`);
    if (res.data.data.status === "succeeded") {
      return "succeeded";
    }
    await wait(POLL_DELAY_MS);
  }
  return "pending";
}

export default function StripeCheckoutForm({ onResult }) {
  const stripe = useStripe();
  const elements = useElements();
  const [submitting, setSubmitting] = useState(false);
  const [confirming, setConfirming] = useState(false);
  const [error, setError] = useState("");

  async function handleSubmit(event) {
    event.preventDefault();
    if (!stripe || !elements) {
      return;
    }

    setSubmitting(true);
    setError("");

    // redirect: "if_required" keeps us on this page unless the card
    // genuinely needs an off-site step (e.g. 3D Secure) — that's what lets
    // us show our own result popup instead of a redirect for the common case.
    const { error: confirmError, paymentIntent } = await stripe.confirmPayment({
      elements,
      redirect: "if_required",
    });

    setSubmitting(false);

    if (confirmError) {
      setError(confirmError.message || "Payment failed.");
      onResult({ status: "failed" });
      return;
    }

    if (paymentIntent.status !== "succeeded") {
      // Nothing to wait for — the card itself hasn't cleared yet
      // (still processing, needs 3DS, etc.), so no webhook has fired.
      onResult({ status: paymentIntent.status });
      return;
    }

    setConfirming(true);
    const webhookStatus = await waitForWebhookConfirmation(paymentIntent.id);
    setConfirming(false);

    onResult({ status: webhookStatus });
  }

  return (
    <form onSubmit={handleSubmit}>
      <PaymentElement />

      {error && (
        <div className="error" role="alert" style={{ marginTop: 14 }}>
          {error}
        </div>
      )}

      <button type="submit" disabled={!stripe || submitting || confirming} style={{ marginTop: 16 }}>
        {confirming ? "Confirming with server…" : submitting ? "Processing…" : "Pay"}
      </button>
    </form>
  );
}
