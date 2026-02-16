import { describe, it, expect, vi, beforeEach } from "vitest";
import { render, screen, waitFor } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import CategoryForm from "../CategoryForm";

describe("CategoryForm", () => {
  const mockOnSubmit = vi.fn();
  const mockOnCancel = vi.fn();

  beforeEach(() => {
    // Clear mock calls between tests
    vi.clearAllMocks();
  });

  it("renders the form with empty input", () => {
    render(<CategoryForm onSubmit={mockOnSubmit} onCancel={mockOnCancel} />);

    expect(screen.getByLabelText(/category name/i)).toBeInTheDocument();
    expect(screen.getByPlaceholderText(/e.g. groceries/i)).toBeInTheDocument();
    expect(screen.getByRole("button", { name: /create/i })).toBeInTheDocument();
    expect(screen.getByRole("button", { name: /cancel/i })).toBeInTheDocument();
  });

  it("renders with intial data for editing", () => {
    const initialData = {
      id: 1,
      name: "Groceries",
      user: {
        id: 1,
        name: "Test User",
        email: "test@example.com",
        createdAt: "2025-01-01T00:00:00Z",
      },
      version: 1,
    };

    render(
      <CategoryForm
        onSubmit={mockOnSubmit}
        onCancel={mockOnCancel}
        initialData={initialData}
      />,
    );

    expect(screen.getByDisplayValue("Groceries")).toBeInTheDocument();
    expect(screen.getByRole("button", { name: /update/i })).toBeInTheDocument();
  });

  it("shows validation error when name is too short", async () => {
    const user = userEvent.setup();

    render(<CategoryForm onSubmit={mockOnSubmit} onCancel={mockOnCancel} />);

    const input = screen.getByLabelText(/category name/i);
    const submitButton = screen.getByRole("button", { name: /create/i });

    await user.type(input, "A");
    await user.click(submitButton);

    await waitFor(() => {
      expect(screen.getByText(/at least 2 characters/i)).toBeInTheDocument();
    });

    expect(mockOnSubmit).not.toHaveBeenCalled();
  });

  it("calls onSubmit with valid data", async () => {
    const user = userEvent.setup();

    render(<CategoryForm onSubmit={mockOnSubmit} onCancel={mockOnCancel} />);

    const input = screen.getByLabelText(/category name/i);
    const submitButton = screen.getByRole("button", { name: /create/i });

    await user.type(input, "Groceries");
    await user.click(submitButton);

    await waitFor(() => {
      expect(mockOnSubmit).toHaveBeenCalledWith(
        { name: "Groceries" },
        expect.anything(),
      );
    });
  });

  it("calls onCancel when cancel button is clicked", async () => {
    const user = userEvent.setup();

    render(<CategoryForm onSubmit={mockOnSubmit} onCancel={mockOnCancel} />);

    const cancelButton = screen.getByRole("button", { name: /cancel/i });
    await user.click(cancelButton);

    expect(mockOnCancel).toHaveBeenCalled();
  });

  it("shows loading state when isLoading is true", () => {
    render(
      <CategoryForm
        onSubmit={mockOnSubmit}
        onCancel={mockOnCancel}
        isLoading={true}
      />,
    );

    expect(screen.getByRole("button", { name: /saving/i })).toBeDisabled();
  });
});
