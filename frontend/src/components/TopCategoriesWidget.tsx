import type { CategorySummary } from "@/types/transaction.types";
import { formatCurrency } from "@/utils/formatCurrency";

interface TopCategoriesWidgetProps {
  data: CategorySummary[];
  limit?: number;
}

function TopCategoriesWidget({ data, limit = 5 }: TopCategoriesWidgetProps) {
  const topCategories = data
    .filter((item) => item.totalAmount < 0)
    .sort((a, b) => a.totalAmount - b.totalAmount)
    .slice(0, limit);

  if (topCategories.length === 0) {
    return <p className="section-empty">No spending data</p>;
  }

  const totalExpenses = topCategories.reduce(
    (sum, item) => sum + Math.abs(item.totalAmount),
    0,
  );

  return (
    <div className="top-categories">
      {topCategories.map((category, index) => {
        const percentage =
          (Math.abs(category.totalAmount) / totalExpenses) * 100;

        return (
          <div key={category.categoryId} className="top-category-item">
            <div className="category-rank">{index + 1}</div>
            <div className="category-details">
              <div className="category-header">
                <span className="category-name">{category.categoryName}</span>
                <span className="category-amount">
                  {formatCurrency(Math.abs(category.totalAmount))}
                </span>
              </div>
              <div className="category-bar">
                <div
                  className="category-bar-fill"
                  style={{ width: `${percentage}%` }}
                />
              </div>
              <div className="category-meta">
                {category.transactionCount} transaction
                {category.transactionCount !== 1 ? "s" : ""} • {" "}
                {percentage.toFixed(1)}% of total
              </div>
            </div>
          </div>
        );
      })}
    </div>
  );
}

export default TopCategoriesWidget;
