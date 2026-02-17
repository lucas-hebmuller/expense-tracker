import { describe, it, expect, vi, beforeEach } from "vitest";
import { render, screen, waitFor } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import TransactionForm from "../TransactionForm";

vi.mock("@/hooks/useCategories", () => ({
  useCategories: () => ({
    data: [
      {
        id: 1,
        name: "Groceries",
        user: {
          id: 1,
          name: "Test",
          email: "test@test.com",
          createdAt: "2025-01-01",
        },
        version: 1,
      },
      {
        id: 2,
        name: "Transport",
        user: {
          id: 1,
          name: "Test",
          email: "test@test.com",
          createdAt: "2025-01-01",
        },
        version: 1,
      },
    ],
    isLoading: false,
  }),
}));

describe("TransactionForm", () => {
  const mockOnSubmit = vi.fn();
  const mockOnCancel = vi.fn();

  beforeEach(() => {
    vi.clearAllMocks();
  });

  it("renders all form fields", () => {
    render(<TransactionForm onSubmit={mockOnSubmit} onCancel={mockOnCancel} />);

    expect(screen.getByLabelText(/description/i)).toBeInTheDocument();
    expect(screen.getByLabelText(/amount/i)).toBeInTheDocument();
    expect(screen.getByLabelText(/date/i)).toBeInTheDocument();
    expect(screen.getByLabelText(/category/i)).toBeInTheDocument();
    expect(screen.getByRole("button", { name: /create/i })).toBeInTheDocument();
    expect(screen.getByRole("button", { name: /cancel/i })).toBeInTheDocument();
  });

  it("renders category options from hook", () => {
    render(<TransactionForm onSubmit={mockOnSubmit} onCancel={mockOnCancel} />);

    expect(
      screen.getByRole("option", { name: /groceries/i }),
    ).toBeInTheDocument();
    expect(
      screen.getByRole("option", { name: /transport/i }),
    ).toBeInTheDocument();
  });

  it("shows validation error when description is empty", async () => {
    const user = userEvent.setup();

    render(<TransactionForm onSubmit={mockOnSubmit} onCancel={mockOnCancel} />);

    const submitButton = screen.getByRole("button", { name: /create/i });
    await user.click(submitButton);

    await waitFor(() => {
      expect(screen.getByText(/description is required/i)).toBeInTheDocument();
    });

    expect(mockOnSubmit).not.toHaveBeenCalled();
  });

  it("shows validation error when category is not selected", async () => {
    const user = userEvent.setup();

    render(<TransactionForm onSubmit={mockOnSubmit} onCancel={mockOnCancel} />);

    const descriptionInput = screen.getByLabelText(/description/i);
    const amountInput = screen.getByLabelText(/amount/i);
    const submitButton = screen.getByRole("button", { name: /create/i });

    await user.type(descriptionInput, "Test transaction");
    await user.type(amountInput, "-50");
    await user.click(submitButton);

    await waitFor(() => {
      expect(screen.getByText(/invalid input/i)).toBeInTheDocument();
    });

    expect(mockOnSubmit).not.toHaveBeenCalled();
  });

  it("calls onSubmit with valid data", async () => {
    const user = userEvent.setup();

    render(<TransactionForm onSubmit={mockOnSubmit} onCancel={mockOnCancel} />);

    const descriptionInput = screen.getByLabelText(/description/i);
    const amountInput = screen.getByLabelText(/amount/i);
    const categorySelect = screen.getByLabelText(/category/i);
    const submitButton = screen.getByRole("button", { name: /create/i });

    await user.type(descriptionInput, "Weekly groceries");
    await user.type(amountInput, "-75.50");
    await user.selectOptions(categorySelect, "1");
    await user.click(submitButton);

    await waitFor(() => {
      expect(mockOnSubmit).toHaveBeenCalledWith({
        description: "Weekly groceries",
        amount: -75.5,
        transactionDate: expect.any(String),
        category: { id: 1 },
      });
    });
  });

  it("calls onCancel when cancel button is clicked", async () => {
    const user = userEvent.setup();

    render(<TransactionForm onSubmit={mockOnSubmit} onCancel={mockOnCancel} />);

    const cancelButton = screen.getByRole("button", { name: /cancel/i });
    await user.click(cancelButton);

    expect(mockOnCancel).toHaveBeenCalled();
  });

  it("shows loading state when isLoading is true", () => {
    render(
      <TransactionForm
        onSubmit={mockOnSubmit}
        onCancel={mockOnCancel}
        isLoading={true}
      />,
    );

    expect(screen.getByRole("button", { name: /saving/i })).toBeDisabled();
  });
});
