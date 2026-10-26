import { describe, it, expect, vi, beforeEach } from "vitest";
import { render, screen, fireEvent } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import TransactionPage from "@/pages/TransactionsPage";
import {
  useTransactions,
  useFilteredSummary,
  useCreateTransaction,
  useUpdateTransaction,
  useDeleteTransaction,
} from "@/hooks/useTransactions";

// Child components aren't under test — stub them out.
vi.mock("@/components/Navbar", () => ({ default: () => <nav /> }));
vi.mock("@/components/TransactionForm", () => ({ default: () => <div /> }));
vi.mock("@/components/ConfirmDialog", () => ({ default: () => <div /> }));

vi.mock("@/hooks/useCategories", () => ({
  useCategories: () => ({
    data: [
      { id: 1, name: "Groceries" },
      { id: 2, name: "Transport" },
    ],
    isLoading: false,
  }),
}));

vi.mock("@/hooks/useTransactions", () => ({
  useTransactions: vi.fn(),
  useFilteredSummary: vi.fn(),
  useCreateTransaction: vi.fn(),
  useUpdateTransaction: vi.fn(),
  useDeleteTransaction: vi.fn(),
}));

const txPage = {
  content: [
    {
      id: 1,
      description: "Test tx",
      amount: -50,
      transactionDate: "2026-01-01",
      category: { id: 1, name: "Groceries" },
    },
  ],
  totalElements: 1,
  totalPages: 3,
  size: 10,
  number: 0,
  first: true,
  last: false,
  empty: false,
};

const summary = {
  totalIncome: 0,
  totalExpenses: -50,
  netAmount: -50,
  transactionCount: 1,
};

// Grab the last argument the hook was called with, by position.
const lastArg = (fn: unknown, index: number) => {
  const { calls } = (fn as { mock: { calls: unknown[][] } }).mock;
  return calls[calls.length - 1][index];
};

describe("TransactionsPage filters", () => {
  beforeEach(() => {
    vi.clearAllMocks();
    vi.mocked(useTransactions).mockReturnValue({
      data: txPage,
      isLoading: false,
      error: null,
    } as never);
    vi.mocked(useFilteredSummary).mockReturnValue({ data: summary } as never);
    vi.mocked(useCreateTransaction).mockReturnValue({
      mutate: vi.fn(),
      isPending: false,
    } as never);
    vi.mocked(useUpdateTransaction).mockReturnValue({
      mutate: vi.fn(),
      isPending: false,
    } as never);
    vi.mocked(useDeleteTransaction).mockReturnValue({
      mutate: vi.fn(),
      isPending: false,
    } as never);
  });

  it("renders the filter controls with category options", () => {
    render(<TransactionPage />);

    expect(screen.getByLabelText("Category")).toBeInTheDocument();
    expect(screen.getByLabelText("From")).toBeInTheDocument();
    expect(screen.getByLabelText("To")).toBeInTheDocument();
    expect(
      screen.getByRole("option", { name: /all categories/i }),
    ).toBeInTheDocument();
    expect(
      screen.getByRole("option", { name: /groceries/i }),
    ).toBeInTheDocument();
    expect(
      screen.getByRole("option", { name: /transport/i }),
    ).toBeInTheDocument();
  });

  it("passes a numeric categoryId to both hooks when a category is picked", async () => {
    const user = userEvent.setup();
    render(<TransactionPage />);

    await user.selectOptions(screen.getByLabelText("Category"), "1");

    // "" from the <select> was coerced to the number 1, not the string "1".
    expect(lastArg(useTransactions, 2)).toEqual({ categoryId: 1 });
    expect(
      typeof (lastArg(useTransactions, 2) as { categoryId: unknown })
        .categoryId,
    ).toBe("number");
    // The summary hook gets the SAME filters — that's what keeps the net in sync with the list.
    expect(lastArg(useFilteredSummary, 0)).toEqual({ categoryId: 1 });
  });

  it("coerces a cleared category back to undefined", async () => {
    const user = userEvent.setup();
    render(<TransactionPage />);
    const select = screen.getByLabelText("Category");

    await user.selectOptions(select, "1");
    await user.selectOptions(
      select,
      screen.getByRole("option", { name: /all categories/i }),
    );

    expect(
      (lastArg(useTransactions, 2) as { categoryId: unknown }).categoryId,
    ).toBeUndefined();
  });

  it("coerces date inputs: string when set, undefined when cleared", () => {
    render(<TransactionPage />);
    const from = screen.getByLabelText("From");

    fireEvent.change(from, { target: { value: "2026-02-01" } });
    expect(
      (lastArg(useTransactions, 2) as { startDate: unknown }).startDate,
    ).toBe("2026-02-01");

    fireEvent.change(from, { target: { value: "" } });
    expect(
      (lastArg(useTransactions, 2) as { startDate: unknown }).startDate,
    ).toBeUndefined();
  });

  it("resets to page 0 when a filter changes", async () => {
    const user = userEvent.setup();
    render(<TransactionPage />);

    await user.click(screen.getByRole("button", { name: /next/i }));
    expect(lastArg(useTransactions, 0)).toBe(1); // advanced to page 1

    await user.selectOptions(screen.getByLabelText("Category"), "2");
    expect(lastArg(useTransactions, 0)).toBe(0); // filter change sent us back to 0
  });

  it("renders the filtered net from the summary hook", () => {
    render(<TransactionPage />);

    const banner = screen
      .getByText(/net for current filter/i)
      .closest(".filter-summary");
    expect(banner).toHaveTextContent("-$50.00");
  });
});
