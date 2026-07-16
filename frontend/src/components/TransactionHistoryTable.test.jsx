import { describe, expect, it } from "vitest";
import { render, screen } from "@testing-library/react";
import TransactionHistoryTable from "./TransactionHistoryTable";

describe("TransactionHistoryTable", () => {
  it("shows a loading message while loading", () => {
    render(<TransactionHistoryTable loading />);
    expect(screen.getByText(/loading transactions/i)).toBeInTheDocument();
  });

  it("shows the error message when an error is passed", () => {
    render(<TransactionHistoryTable error="Could not load transaction history." />);
    expect(screen.getByRole("alert")).toHaveTextContent("Could not load transaction history.");
  });

  it("shows the empty message when there are no items", () => {
    render(<TransactionHistoryTable items={[]} emptyMessage="No transactions match these filters." />);
    expect(screen.getByText("No transactions match these filters.")).toBeInTheDocument();
  });

  it("renders a credit transaction with a plus sign and the counterparty name", () => {
    render(
      <TransactionHistoryTable
        items={[
          {
            id: 1,
            type: "TRANSFER",
            direction: "CREDIT",
            amount: 42.5,
            counterpartyName: "Jane Doe",
            createdAt: "2026-01-01T00:00:00Z",
          },
        ]}
      />
    );
    expect(screen.getByText(/Jane Doe/)).toBeInTheDocument();
    expect(screen.getByText("+42.50")).toBeInTheDocument();
  });

  it("renders a debit transaction with a minus sign", () => {
    render(
      <TransactionHistoryTable
        items={[
          {
            id: 2,
            type: "PAYMENT",
            direction: "DEBIT",
            amount: 10,
            counterpartyName: "Coffee Shop",
            createdAt: "2026-01-01T00:00:00Z",
          },
        ]}
      />
    );
    expect(screen.getByText("-10.00")).toBeInTheDocument();
  });
});
