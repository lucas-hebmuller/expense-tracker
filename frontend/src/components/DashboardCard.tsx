interface DashboardCardProps {
  title: string;
  value: string;
  subtitle?: string;
  type?: "default" | "income" | "expense" | "balance";
}

function DashboardCard({
  title,
  value,
  subtitle,
  type = "default",
}: DashboardCardProps) {
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
      <p className="card-value">{value}</p>
      {subtitle && <p className="card-subtitle">{subtitle}</p>}
    </div>
  );
}

export default DashboardCard;
