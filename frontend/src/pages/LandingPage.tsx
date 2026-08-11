import DashboardView from "@/components/DashboardView";
import {
  demoCategorySummary,
  demoDashboard,
  demoTransactionsPage,
  demoTrend,
} from "@/data/demoDashboardData";
import { useAuthStore } from "@/stores/authStore";
import { isTokenValid } from "@/utils/token";
import { Link, Navigate } from "react-router-dom";

function LandingPage() {
  const token = useAuthStore((state) => state.token);

  if (isTokenValid(token)) {
    return <Navigate to="/dashboard" replace/>;
  }

  return (
    <div>
      <div className="demo-banner">
        <span>You're viewing a sample dashboard</span>
        <div>
          <Link to="/login">Log in</Link>
          <Link to="/register">Register</Link>
        </div>
      </div>
      <DashboardView
        dashboard={demoDashboard}
        trendData={demoTrend}
        trendLoading={false}
        categorySummary={demoCategorySummary}
        transactionsPage={demoTransactionsPage}
        transactionsLoading={false}
      />
    </div>
  );
}

export default LandingPage;
