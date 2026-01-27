import { formatCurrency } from "@/utils/formatCurrency";
import { formatDate } from "@/utils/dateHelpers";
import type { PaginatedResponse, Transaction } from "@/types/transaction.types";

interface RecentTransactionsProps {
  transactionsPage: PaginatedResponse<Transaction> | undefined;
  isLoading: boolean;
}

function RecentTransactions({
  transactionsPage,
  isLoading,
}: RecentTransactionsProps) {
  if (isLoading) {
    return <p>Loading transactions...</p>;
  }

  if (!transactionsPage || transactionsPage.content.length === 0) {
    return <p className="section-empty">No transactions yet</p>;
  }

  return (
    <div className="transaction-list">
      {transactionsPage.content.map((transaction) => (
        <div key={transaction.id} className="transaction-item">
          <div className="transaction-info">
            <p className="transaction-description">
              {transaction.description || "No description"}
            </p>
            <p className="transaction-meta">
              {transaction.category.name} •
              {formatDate(transaction.transactionDate)}
            </p>
          </div>
          <p
            className={`transaction-amount ${transaction.amount >= 0 ? "amount-positive" : "amount-negative"}`}
          >
            {formatCurrency(transaction.amount)}
          </p>
        </div>
      ))}
    </div>
  );
}

export default RecentTransactions;
