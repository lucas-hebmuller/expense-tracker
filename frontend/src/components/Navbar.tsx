import { useNavigate, Link } from "react-router-dom";
import { useQueryClient } from "@tanstack/react-query";
import { useAuthStore } from "@/stores/authStore";

function Navbar() {
  const navigate = useNavigate();
  const queryClient = useQueryClient();
  const { name, logout } = useAuthStore();

  const handleLogout = () => {
    queryClient.clear();
    logout();
    navigate("/login");
  };

  return (
    <nav className="navbar">
      <div className="navbar-left">
        <h1>
          <Link to="/dashboard" className="navbar-brand">Expense Tracker</Link>
        </h1>
        <div className="navbar-links">
          <Link to="/dashboard">Dashboard</Link>
          <Link to="/transactions">Transactions</Link>
          <Link to="/categories">Categories</Link>
        </div>
      </div>

      <div className="navbar-user">
        <span>Welcome, {name}</span>
        <button onClick={handleLogout}>Logout</button>
      </div>
    </nav>
  );
}

export default Navbar;
