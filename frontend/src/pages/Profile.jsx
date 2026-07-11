import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import client from "../api/client";
import { clearToken } from "../auth/auth";

export default function Profile() {
  const navigate = useNavigate();
  const [profile, setProfile] = useState(null);
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    async function load() {
      try {
        // matches AuthController: @RequestMapping("/api/auth") + @GetMapping("/user")
        const res = await client.get("/api/auth/user");
        setProfile(res.data.data);
      } catch (err) {
        setError("Could not load your profile.");
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
      <h1>Your profile</h1>

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

      <dl className="details">
        <dt>Name</dt>
        <dd>{profile.name}</dd>
        <dt>Email</dt>
        <dd>{profile.email}</dd>
        <dt>Phone</dt>
        <dd>{profile.phone}</dd>
        <dt>Role</dt>
        <dd>{profile.role}</dd>
      </dl>

      <button className="secondary" onClick={handleLogout}>
        Log out
      </button>
    </div>
  );
}