import { Pie } from "react-chartjs-2";
import { Chart as ChartJS, ArcElement, Tooltip, Legend } from "chart.js";
import type { CategorySummary } from "@/types/transaction.types";
import { formatCurrency } from "@/utils/formatCurrency";

ChartJS.register(ArcElement, Tooltip, Legend);

interface CategoryPieChartProps {
  data: CategorySummary[];
}

function CategoryPieChart({ data }: CategoryPieChartProps) {
  const expenses = data.filter((item) => item.totalAmount < 0);

  if (expenses.length === 0) {
    return (
      <div className="chart-empty">
        <p>No expense data available</p>
      </div>
    );
  }

  const chartData = {
    labels: expenses.map((item) => item.categoryName),
    datasets: [
      {
        label: "Spending",
        data: expenses.map((item) => Math.abs(item.totalAmount)),
        backgroundColor: [
          "#3b82f6", // Blue
          "#ef4444", // Red
          "#10b981", // Green
          "#f59e0b", // Amber
          "#8b5cf6", // Purple
          "#ec4899", // Pink
          "#06b6d4", // Cyan
          "#f97316", // Orange
          "#6366f1", // Indigo
          "#14b8a6", // Teal
        ],
        borderWidth: 2,
        borderColor: "#fff",
      },
    ],
  };

  const options = {
    responsive: true,
    maintainAspectRatio: false,
    plugins: {
      legend: {
        position: "bottom" as const,
        labels: {
          padding: 15,
          font: {
            size: 12,
          },
        },
      },
      tooltip: {
        callbacks: {
          label: (context: any) => {
            const label = context.label || "";
            const value = context.parsed || 0;
            const total = context.dataset.data.reduce(
              (a: number, b: number) => a + b,
              0,
            );
            const percentage = ((value / total) * 100).toFixed(1);
            return `${label}: ${formatCurrency(value)} (${percentage}%)`;
          },
        },
      },
    },
  };

  return (
    <div className="chart-container">
      <Pie data={chartData} options={options} />
    </div>
  );
}

export default CategoryPieChart;
