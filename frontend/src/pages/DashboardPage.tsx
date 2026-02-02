import { useDashboard } from "@/hooks/useDashboard";
import { useTransactions } from "@/hooks/useTransactions";
import Navbar from "@/components/Navbar";
import RecentTransactions from "@/components/RecentTransactions";
import { useMonthlyTrend } from "@/hooks/useMonthlyTrend";
import { useQuery } from "@tanstack/react-query";
import { transactionApi } from "@/api/transactionApi";
import ComparisonCard from "@/components/ComparisonCard";
import MonthlyTrendChart from "@/components/MonthlyTrendChart";
import CategoryPieChart from "@/components/CategoryPieChart";
import TopCategoriesWidget from "@/components/TopCategoriesWidget";

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

  const currentMonth = dashboard.currentMonth;
  const lastMonth = dashboard.lastMonth;

  return (
    <div>
      <Navbar />

      <main className="main-content">
        <h2>Dashboard</h2>

        {/* Comparison Cards */}
        <div className="dashboard-grid">
          <ComparisonCard
            title="Total Income"
            currentValue={currentMonth.totalIncome}
            previousValue={lastMonth.totalIncome}
            type="income"
          />
          <ComparisonCard
            title="Total Expenses"
            currentValue={Math.abs(currentMonth.totalExpenses)}
            previousValue={Math.abs(lastMonth.totalExpenses)}
            type="expense"
          />
          <ComparisonCard
            title="Net Balance"
            currentValue={currentMonth.netAmount}
            previousValue={lastMonth.netAmount}
            type="balance"
          />
          <div className="dashboard-card">
            <h3>Transactions</h3>
            <p className="card-value">{currentMonth.transactionCount}</p>
            <p className="card-subtitle">This month</p>
          </div>
        </div>

        {/* Charts Section */}
        <div className="charts-grid">
          <div className="dashboard-section chart-section-large">
            <h3>6-Month Trend</h3>
            {trendLoading ? (
              <p>Loading trend data...</p>
            ) : trendData && trendData.length > 0 ? (
              <MonthlyTrendChart data={trendData} />
            ) : (
              <p className="section-empty">No trend data available</p>
            )}
          </div>

          <div className="dashboard-section">
            <h3>Spending by Category</h3>
            {categorySummary && categorySummary.length > 0 ? (
              <CategoryPieChart data={categorySummary} />
            ) : (
              <p className="section-empty">No spending data yet</p>
            )}
          </div>
        </div>

        {/* Bottom Section */}
        <div className="dashboard-bottom">
          <div className="dashboard-section">
            <h3>Top 5 Spending Categories</h3>
            {categorySummary && categorySummary.length > 0 ? (
              <TopCategoriesWidget data={categorySummary} limit={5} />
            ) : (
              <p className="section-empty">No spending data yet</p>
            )}
          </div>

          <div className="dashboard-section">
            <h3>Recent Transactions</h3>
            <RecentTransactions
              transactionsPage={transactionsPage}
              isLoading={transactionsLoading}
            />
          </div>
        </div>
      </main>
    </div>
  );
}

export default DashboardPage;
