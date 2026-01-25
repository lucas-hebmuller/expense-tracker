import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import { transactionApi } from "@/api/transactionApi";
import type {
  Transaction,
  TransactionCreateRequest,
  PaginatedResponse,
} from "@/types/transaction.types";
import { AxiosError } from "axios";
import type { ApiError } from "@/types/api.types";

export const useTransactions = (page: number = 0, size: number = 10) => {
  return useQuery<PaginatedResponse<Transaction>, AxiosError<ApiError>>({
    queryKey: ["transactions", page, size],
    queryFn: () => transactionApi.getAll(page, size),
  });
};

export const useCreateTransaction = () => {
  const queryClient = useQueryClient();

  return useMutation<
    Transaction,
    AxiosError<ApiError>,
    TransactionCreateRequest
  >({
    mutationFn: (data) => transactionApi.create(data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["transactions"] });
      queryClient.invalidateQueries({ queryKey: ["dashboard"] });
    },
  });
};

export const useDeleteTransaction = () => {
  const queryClient = useQueryClient();

  return useMutation<void, AxiosError<ApiError>, number>({
    mutationFn: (id) => transactionApi.delete(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["transactions"] });
      queryClient.invalidateQueries({ queryKey: ["dashboard"] });
    },
  });
};
