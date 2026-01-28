import { useDashboard } from "@/hooks/useDashboard";
import { useTransactions } from "@/hooks/useTransactions";
import Navbar from "@/components/Navbar";
import DashboardCard from "@/components/DashboardCard";
import { formatCurrency } from "@/utils/formatCurrency";
import RecentTransactions from "@/components/RecentTransactions";

function DashboardPage() {
  const {
    data: dashboard,
    isLoading: dashboardLoading,
    error: dashboardError,
  } = useDashboard();

  const { data: transactionsPage, isLoading: transactionsLoading } =
    useTransactions(0, 5);

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

  const currentMonth = dashboard.currentMonth;
  const netAmount = currentMonth.totalIncome + currentMonth.totalExpenses;

  return (
    <div>
      <Navbar />

      <main className="main-content">
        <h2>Dashboard</h2>

        <div className="dashboard-grid">
          <DashboardCard
            title="Total Income"
            value={formatCurrency(currentMonth.totalIncome)}
            subtitle="This month"
            type="income"
          />
          <DashboardCard
            title="Total Expenses"
            value={formatCurrency(Math.abs(currentMonth.totalExpenses))}
            subtitle="This month"
            type="expense"
          />
          <DashboardCard
            title="Net Balance"
            value={formatCurrency(netAmount)}
            subtitle="This month"
            type="balance"
          />
          <DashboardCard
            title="Transactions"
            value={currentMonth.transactionCount.toString()}
            subtitle="This month"
            type="default"
          />
        </div>

        <div className="dashboard-section">
          <h3>Recent Transactions</h3>
          <RecentTransactions
            transactionsPage={transactionsPage}
            isLoading={transactionsLoading}
          />
        </div>
      </main>
    </div>
  );
}

export default DashboardPage;
