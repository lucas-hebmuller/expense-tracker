import API from "./axiosConfig";
import {
  type Category,
  type CategoryCreateRequest,
} from "../types/category.types";

export const categoryApi = {
  getAll: async (): Promise<Category[]> => {
    const response = await API.get<Category[]>("/categories");
    return response.data;
  },

  getById: async (id: number): Promise<Category> => {
    const response = await API.get<Category>(`/categories/${id}`);
    return response.data;
  },

  create: async (data: CategoryCreateRequest): Promise<Category> => {
    const response = await API.post<Category>("/categories", data);
    return response.data;
  },

  update: async (
    id: number,
    data: CategoryCreateRequest,
  ): Promise<Category> => {
    const response = await API.put<Category>(`/categories/${id}`, data);
    return response.data;
  },

  delete: async (id: number): Promise<void> => {
    await API.delete(`/categories/${id}`);
  },
};
