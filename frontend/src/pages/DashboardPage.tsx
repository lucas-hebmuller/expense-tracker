import { useNavigate } from "react-router-dom";
import { useAuthStore } from "@/stores/authStore";

function DashboardPage() {
  const navigate = useNavigate();
  const { name, email, logout } = useAuthStore();

  const handleLogout = () => {
    logout();
    navigate("/login");
  };

  return (
    <div>
      <nav className="navbar">
        <h1>Expense Tracker</h1>
        <div>
          <span>Welcome, {name}</span>
          <button onClick={handleLogout}>Logout</button>
        </div>
      </nav>

      <main className="main-content">
        <h2>Dashboard</h2>
        <p>Email: {email}</p>
        <p>Dashboard content</p>
      </main>
    </div>
  );
}

export default DashboardPage;
