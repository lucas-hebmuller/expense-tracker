import { useQuery } from "@tanstack/react-query";
import { transactionApi } from "@/api/transactionApi";
import type { Dashboard } from "@/types/transaction.types";
import { AxiosError } from "axios";
import type { ApiError } from "@/types/api.types";

export const useDashboard = () => {
  return useQuery<Dashboard, AxiosError<ApiError>>({
    queryKey: ["dashboard"],
    queryFn: () => transactionApi.getDashboard(),
  });
};
