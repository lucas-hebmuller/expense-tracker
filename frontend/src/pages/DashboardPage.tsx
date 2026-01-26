import { useDashboard } from "@/hooks/useDashboard";
import { useTransactions } from "@/hooks/useTransaction";
import Navbar from "@/components/Navbar";
import DashboardCard from "@/components/DashboardCard";
import CategoryChart from "@/components/CategoryChart";
import { formatCurrency } from "@/utils/formatCurrency";
import { formatDate } from "@/utils/dateHelpers";

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
          {transactionsLoading ? (
            <p>Loading transactions...</p>
          ) : transactionsPage && transactionsPage.content.length > 0 ? (
            <div className="transaction-list">
              {transactionsPage.content.map((transaction) => (
                <div key={transaction.id} className="transaction-item">
                  <div className="transaction-info">
                    <p className="transaction-description">
                      {transaction.description || "No description"}
                    </p>
                    <p className="transaction-meta">
                      {transaction.category.name} •{" "}
                      {formatDate(transaction.transactionDate)}
                    </p>
                  </div>
                  <p
                    className={`transaction-amount ${
                      transaction.amount >= 0
                        ? "amount-positive"
                        : "amount-negative"
                    }`}
                  >
                    {formatCurrency(transaction.amount)}
                  </p>
                </div>
              ))}
            </div>
          ) : (
            <p className="section-empty">No transactions yet</p>
          )}
        </div>
      </main>
    </div>
  );
}

export default DashboardPage;
