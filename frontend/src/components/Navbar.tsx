import { useNavigate } from "react-router-dom";
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
      <h1>Expense Tracker</h1>
      <div className="navbar-user">
        <span>Welcome, {name}</span>
        <button onClick={handleLogout}>Logout</button>
      </div>
    </nav>
  );
}

export default Navbar;
