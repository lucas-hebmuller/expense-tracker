import { Pie } from "react-chartjs-2";
import { Chart as ChartJS, ArcElement, Tooltip, Legend } from "chart.js";
import type { CategorySummary } from "@/types/transaction.types";

ChartJS.register(ArcElement, Tooltip, Legend);

interface CategoryChartProps {
  data: CategorySummary[];
}

function CategoryChart({ data }: CategoryChartProps) {
  const expenses = data.filter((item) => item.totalAmount < 0);

  if (expenses.length === 0) {
    return (
      <div className="chart-container">
        <p className="chart-empty">No expense data to display</p>
      </div>
    );
  }

  const chartData = {
    labels: expenses.map((item) => item.categoryName),
    datasets: [
      {
        label: "Spending by Category",
        data: expenses.map((item) => Math.abs(item.totalAmount)),
        backgroundColor: [
          "#3b82f6",
          "#ef4444",
          "#10b981",
          "#f59e0b",
          "#8b5cf6",
          "#ec4899",
          "#06b6d4",
          "#f97316",
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
      },
      tooltip: {
        callbacks: {
          label: (context: any) => {
            const label = context.label || "";
            const value = context.parsed || 0;
            return `${label}: $${value.toFixed(2)}`;
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

export default CategoryChart;
