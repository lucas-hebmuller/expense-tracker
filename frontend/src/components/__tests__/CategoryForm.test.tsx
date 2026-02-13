import { describe, it, expect, vi, beforeAll } from "vitest";
import { render, screen, waitFor } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import CategoryForm from "../CategoryForm";
import { beforeEach } from "node:test";

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
    const initialData = { id: 1, name: "Groceries" };

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
});
