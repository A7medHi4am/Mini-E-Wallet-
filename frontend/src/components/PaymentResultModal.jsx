export default function PaymentResultModal({ result, onClose }) {
  if (!result) {
    return null;
  }

  const isSuccess = result.status === "succeeded";

  return (
    <div className="modal-overlay" role="dialog" aria-modal="true">
      <div className="modal-card">
        <h2>Thank you</h2>
        <p className={isSuccess ? "notice" : "error"}>Status: {result.status}</p>
        <button onClick={onClose}>Close</button>
      </div>
    </div>
  );
}
