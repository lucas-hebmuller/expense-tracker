import { useQuery } from "@tanstack/react-query";
import { transactionApi } from "@/api/transactionApi";
import type { MonthlySummary } from "@/types/transaction.types";
import { AxiosError } from "axios";
import type { ApiError } from "@/types/api.types";

export const useMonthlyTrend = (months: number = 6) => {
  return useQuery<MonthlySummary[], AxiosError<ApiError>>({
    queryKey: ["monthly-trend", months],
    queryFn: async () => {
      const now = new Date();
      const results: MonthlySummary[] = [];

      for (let i = months - 1; i >= 0; i--) {
        const date = new Date(now.getFullYear(), now.getMonth() - i, 1);
        const year = date.getFullYear();
        const month = date.getMonth() + 1;

        try {
          const data = await transactionApi.getMonthlySummary(year, month);
          results.push(data);
        } catch (error) {
          results.push({
            year,
            month,
            totalIncome: 0,
            totalExpenses: 0,
            netAmount: 0,
            transactionCount: 0,
          });
        }
      }

      return results;
    },
  });
};
