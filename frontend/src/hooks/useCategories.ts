import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import { categoryApi } from "@/api/categoryApi";
import type { Category, CategoryCreateRequest } from "@/types/category.types";
import { AxiosError } from "axios";
import type { ApiError } from "@/types/api.types";

export const useCategories = () => {
  return useQuery<Category[], AxiosError<ApiError>>({
    queryKey: ["categories"],
    queryFn: () => categoryApi.getAll(),
  });
};

export const useCreateCategory = () => {
  const queryClient = useQueryClient();

  return useMutation<Category, AxiosError<ApiError>, CategoryCreateRequest>({
    mutationFn: (data) => categoryApi.create(data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["categories"] });
    },
  });
};

export const useUpdateCategory = () => {
  const queryClient = useQueryClient();

  return useMutation<
    Category,
    AxiosError<ApiError>,
    { id: number; data: CategoryCreateRequest }
  >({
    mutationFn: ({ id, data }) => categoryApi.update(id, data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["categories"] });
    },
  });
};

export const useDeleteCategory = () => {
  const queryClient = useQueryClient();

  return useMutation<void, AxiosError<ApiError>, number>({
    mutationFn: (id) => categoryApi.delete(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["categories"] });
    },
  });
};
