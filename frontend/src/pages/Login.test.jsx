import { describe, expect, it } from "vitest";
import { render, screen, fireEvent } from "@testing-library/react";
import { MemoryRouter } from "react-router-dom";
import Login from "./Login";

describe("Login", () => {
  it("shows a validation error when submitting without email or password", () => {
    render(
      <MemoryRouter>
        <Login />
      </MemoryRouter>
    );

    fireEvent.click(screen.getByRole("button", { name: /log in/i }));

    expect(screen.getByText("Email and password are required.")).toBeInTheDocument();
  });
});
