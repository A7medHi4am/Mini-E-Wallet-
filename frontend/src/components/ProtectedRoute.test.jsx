import { afterEach, describe, expect, it } from "vitest";
import { render, screen } from "@testing-library/react";
import { MemoryRouter, Route, Routes } from "react-router-dom";
import ProtectedRoute from "./ProtectedRoute";

const TOKEN_KEY = "wallet_jwt";

function renderProtectedRoute() {
  return render(
    <MemoryRouter initialEntries={["/dashboard"]}>
      <Routes>
        <Route
          path="/dashboard"
          element={
            <ProtectedRoute>
              <div>Secret dashboard</div>
            </ProtectedRoute>
          }
        />
        <Route path="/login" element={<div>Login page</div>} />
      </Routes>
    </MemoryRouter>
  );
}

describe("ProtectedRoute", () => {
  afterEach(() => {
    localStorage.removeItem(TOKEN_KEY);
  });

  it("redirects to /login when there is no token", () => {
    renderProtectedRoute();
    expect(screen.getByText("Login page")).toBeInTheDocument();
    expect(screen.queryByText("Secret dashboard")).not.toBeInTheDocument();
  });

  it("renders the protected content when a token is present", () => {
    localStorage.setItem(TOKEN_KEY, "a-fake-jwt");
    renderProtectedRoute();
    expect(screen.getByText("Secret dashboard")).toBeInTheDocument();
  });
});
