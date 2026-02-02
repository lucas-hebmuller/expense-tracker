import { formatCurrency } from "@/utils/formatCurrency";

interface ComparisonCardProps {
  title: string;
  currentValue: number;
  previousValue: number;
  type?: "income" | "expense" | "balance" | "default";
}

function ComparisonCard({
  title,
  currentValue,
  previousValue,
  type = "default",
}: ComparisonCardProps) {
  const difference = currentValue - previousValue;
  const percentageChange =
    previousValue !== 0
      ? ((difference / Math.abs(previousValue)) * 100).toFixed(1)
      : "0.0";

  const isIncrease = difference > 0;
  const isPositive =
    (type === "income" && isIncrease) ||
    (type === "expense" && !isIncrease) ||
    (type === "balance" && isIncrease);

  const getColorClass = () => {
    switch (type) {
      case "income":
        return "card-income";
      case "expense":
        return "card-expense";
      case "balance":
        return "card-balance";
      default:
        return "card-default";
    }
  };

  return (
    <div className={`dashboard-card ${getColorClass()}`}>
      <h3>{title}</h3>
      <p className="card-value">{formatCurrency(currentValue)}</p>
      <div className="card-comparison">
        <span
          className={`comparison-badge ${isPositive ? "positive" : "negative"}`}
        >
          {isIncrease ? "↑" : "↓"} {Math.abs(parseFloat(percentageChange))}%
        </span>
        <span className="comparison-text">vs last month</span>
      </div>
    </div>
  );
}

export default ComparisonCard;
