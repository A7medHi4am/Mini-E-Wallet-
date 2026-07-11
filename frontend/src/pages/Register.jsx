import { useState } from "react";
import { useNavigate, Link } from "react-router-dom";
import client from "../api/client";

export default function Register() {
  const navigate = useNavigate();
  const [form, setForm] = useState({
    name: "",
    email: "",
    phone: "",
    password: "",
  });
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  function update(field, value) {
    setForm((f) => ({ ...f, [field]: value }));
  }

  async function handleSubmit() {
    setError("");

    if (!form.name || !form.email || !form.phone || !form.password) {
      setError("All fields are required.");
      return;
    }
    if (form.password.length < 8) {
      setError("Password must be at least 8 characters.");
      return;
    }

    setLoading(true);
    try {
      
      await client.post("/api/auth/register", form);
      navigate("/login", { state: { justRegistered: true } });
    } catch (err) {
      setError(
        err.response?.data?.error ||
          "Registration failed. That email may already be in use."
      );
    } finally {
      setLoading(false);
    }
  }

  return (
    <div className="card">
      <h1>Create your wallet</h1>
      <p className="subtitle">Sign up to get started.</p>

      <label>
        Name
        <input
          value={form.name}
          onChange={(e) => update("name", e.target.value)}
          placeholder="John Doe"
        />
      </label>

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
        Phone
        <input
          value={form.phone}
          onChange={(e) => update("phone", e.target.value)}
          placeholder="01000000000"
        />
      </label>

      <label>
        Password
        <input
          type="password"
          value={form.password}
          onChange={(e) => update("password", e.target.value)}
          placeholder="At least 8 characters"
        />
      </label>

      {error && <div className="error">{error}</div>}

      <button onClick={handleSubmit} disabled={loading}>
        {loading ? "Creating account…" : "Create account"}
      </button>

      <p className="muted">
        Already have an account? <Link to="/login">Log in</Link>
      </p>
    </div>
  );
}