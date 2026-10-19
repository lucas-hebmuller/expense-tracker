import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import { transactionApi } from "@/api/transactionApi";
import type {
  Transaction,
  TransactionCreateRequest,
  PaginatedResponse,
  TransactionFilters,
  FilteredSummary,
} from "@/types/transaction.types";
import { AxiosError } from "axios";
import type { ApiError } from "@/types/api.types";

export const useTransactions = (
  page: number = 0,
  size: number = 10,
  filters: TransactionFilters = {},
) => {
  return useQuery<PaginatedResponse<Transaction>, AxiosError<ApiError>>({
    queryKey: ["transactions", page, size, filters],
    queryFn: () => transactionApi.getAll(page, size, filters),
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
      queryClient.invalidateQueries({ queryKey: ["monthly-trend"] });
      queryClient.invalidateQueries({ queryKey: ["category-summary"] });
      queryClient.invalidateQueries({ queryKey: ["category-summary-monthly"] });
    },
  });
};

export const useUpdateTransaction = () => {
  const queryClient = useQueryClient();

  return useMutation<
    Transaction,
    AxiosError<ApiError>,
    { id: number; data: TransactionCreateRequest }
  >({
    mutationFn: ({ id, data }) => transactionApi.update(id, data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["transactions"] });
      queryClient.invalidateQueries({ queryKey: ["dashboard"] });
      queryClient.invalidateQueries({ queryKey: ["monthly-trend"] });
      queryClient.invalidateQueries({ queryKey: ["category-summary"] });
      queryClient.invalidateQueries({ queryKey: ["category-summary-monthly"] });
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
      queryClient.invalidateQueries({ queryKey: ["monthly-trend"] });
      queryClient.invalidateQueries({ queryKey: ["category-summary"] });
      queryClient.invalidateQueries({ queryKey: ["category-summary-monthly"] });
    },
  });
};

export const useFilteredSummary = (filters: TransactionFilters = {}) => {
    return useQuery<FilteredSummary, AxiosError<ApiError>>({
      queryKey: ["transaction-summary", filters],
      queryFn: () => transactionApi.getFilteredSummary(filters)
    })
  }
