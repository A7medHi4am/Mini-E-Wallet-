import { useState } from "react";
import { useNavigate, useLocation, Link } from "react-router-dom";
import client from "../api/client";
import { saveToken } from "../auth/auth";

export default function Login() {
  const navigate = useNavigate();
  const location = useLocation();
  const justRegistered = location.state?.justRegistered;

  const [form, setForm] = useState({ email: "", password: "" });
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  function update(field, value) {
    setForm((f) => ({ ...f, [field]: value }));
  }

  async function handleSubmit() {
    setError("");
    if (!form.email || !form.password) {
      setError("Email and password are required.");
      return;
    }

    setLoading(true);
    try {
    
      const res = await client.post("/api/auth/login", form);

      // backend wraps responses in ApiResponse { success, data, error }
      const token = res.data.data?.token;
      if (!token) {
        throw new Error("No token in response");
      }

      saveToken(token);
      navigate("/dashboard");
    } catch (err) {
      setError(
        err.response?.data?.error || "Login failed. Check your credentials."
      );
    } finally {
      setLoading(false);
    }
  }

  return (
    <div className="card">
      <h1>Welcome back</h1>
      <p className="subtitle">Log in to your wallet.</p>

      {justRegistered && (
        <div className="notice">Account created. Please log in.</div>
      )}

      <label>
        Email
        <input
          type="email"
          value={form.email}
          onChange={(e) => update("email", e.target.value)}
          placeholder="name@example.com"
        />
      </label>

      <label>
        Password
        <input
          type="password"
          value={form.password}
          onChange={(e) => update("password", e.target.value)}
          placeholder="Your password"
        />
      </label>

      {error && <div className="error">{error}</div>}

      <button onClick={handleSubmit} disabled={loading}>
        {loading ? "Logging in…" : "Log in"}
      </button>

      <p className="muted">
        No account yet? <Link to="/register">Sign up</Link>
      </p>
    </div>
  );
}