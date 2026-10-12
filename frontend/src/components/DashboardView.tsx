import ComparisonCard from "@/components/ComparisonCard";
import MonthlyTrendChart from "@/components/MonthlyTrendChart";
import CategoryPieChart from "@/components/CategoryPieChart";
import TopCategoriesWidget from "@/components/TopCategoriesWidget";
import RecentTransactions from "@/components/RecentTransactions";
import type {
  CategorySummary,
  Dashboard,
  MonthlySummary,
  PaginatedResponse,
  Transaction,
} from "@/types/transaction.types";

interface DashboardViewProps {
  dashboard: Dashboard;
  trendData: MonthlySummary[] | undefined;
  trendLoading: boolean;
  categorySummary: CategorySummary[] | undefined;
  monthlyCategorySummary: CategorySummary[] | undefined;
  transactionsPage: PaginatedResponse<Transaction> | undefined;
  transactionsLoading: boolean;
}

function DashboardView({
  dashboard,
  trendData,
  trendLoading,
  categorySummary,
  monthlyCategorySummary,
  transactionsPage,
  transactionsLoading,
}: DashboardViewProps) {
  const currentMonth = dashboard.currentMonth;
  const lastMonth = dashboard.lastMonth;
  const monthLabel = new Date().toLocaleString("default", { month: "long", year: "numeric" });

  return (
    <main className="main-content">
      <h2>Dashboard</h2>

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
          <h3>Spending by Category - {monthLabel}</h3>
          {monthlyCategorySummary && monthlyCategorySummary.length > 0 ? (
            <CategoryPieChart data={monthlyCategorySummary} />
          ) : (
            <p className="section-empty">No spending recorded this month</p>
          )}
        </div>
      </div>

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
  );
}

export default DashboardView;
