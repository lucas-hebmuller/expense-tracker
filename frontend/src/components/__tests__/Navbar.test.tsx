import { describe, it, expect } from "vitest";
import { render, screen } from "@testing-library/react";
import { BrowserRouter as Router } from "react-router-dom";
import Navbar from "../Navbar";

const renderWithRouter = (component: React.ReactNode) => {
  return render(<Router>{component}</Router>);
};

describe("Navbar", () => {
  it("renders the app title", () => {
    renderWithRouter(<Navbar />);

    expect(screen.getByText(/expense tracker/i)).toBeInTheDocument();
  });

  it("renders navigtion links", () => {
    renderWithRouter(<Navbar />);

    expect(screen.getByText(/dashboard/i)).toBeInTheDocument();
    expect(screen.getByText(/transactions/i)).toBeInTheDocument();
    expect(screen.getByText(/categories/i)).toBeInTheDocument();
  });
});
