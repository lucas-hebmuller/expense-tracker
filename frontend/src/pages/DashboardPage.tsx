import { useDashboard } from "@/hooks/useDashboard";
import { useTransactions } from "@/hooks/useTransactions";
import Navbar from "@/components/Navbar";
import { useMonthlyTrend } from "@/hooks/useMonthlyTrend";
import { useQuery } from "@tanstack/react-query";
import { transactionApi } from "@/api/transactionApi";
import DashboardView from "@/components/DashboardView";

function DashboardPage() {
  const {
    data: dashboard,
    isLoading: dashboardLoading,
    error: dashboardError,
  } = useDashboard();

  const { data: transactionsPage, isLoading: transactionsLoading } =
    useTransactions(0, 5);

  const { data: trendData, isLoading: trendLoading } = useMonthlyTrend(6);

  const { data: categorySummary } = useQuery({
    queryKey: ["category-summary"],
    queryFn: () => transactionApi.getCategorySummary(),
  });

  if (dashboardLoading) {
    return (
      <div>
        <Navbar />
        <main className="main-content">
          <p>Loading dashboard...</p>
        </main>
      </div>
    );
  }

  if (dashboardError) {
    return (
      <div>
        <Navbar />
        <main className="main-content">
          <div className="error-message">
            Failed to load dashboard. Please try again.
          </div>
        </main>
      </div>
    );
  }

  if (!dashboard) {
    return (
      <div>
        <Navbar />
        <main className="main-content">
          <p>No dashboard data available.</p>
        </main>
      </div>
    );
  }

  return (
    <div>
      <Navbar />

      <DashboardView
        dashboard={dashboard}
        trendData={trendData}
        trendLoading={trendLoading}
        categorySummary={categorySummary}
        transactionsPage={transactionsPage}
        transactionsLoading={transactionsLoading}
      />
    </div>
  );
}

export default DashboardPage;
