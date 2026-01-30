import { useNavigate, Link } from "react-router-dom";
import { useAuthStore } from "@/stores/authStore";

function Navbar() {
  const navigate = useNavigate();
  const { name, logout } = useAuthStore();

  const handleLogout = () => {
    logout();
    navigate("/login");
  };

  return (
    <nav className="navbar">
      <div className="navbar-left">
        <h1>Expense Tracker</h1>
        <div className="navbar-links">
          <Link to="/">Dashboard</Link>
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
