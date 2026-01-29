import API from "./axiosConfig";
import {
    type Transaction,
    type TransactionCreateRequest,
    type PaginatedResponse,
    type Dashboard,
    type MonthlySummary,
    type CategorySummary,
} from "@/types/transaction.types";

export const transactionApi = {
    getAll: async (page: number = 0, size: number = 10): Promise<PaginatedResponse<Transaction>> => {
        const response = await API.get<PaginatedResponse<Transaction>>("/transactions", {
            params: {page, size, sort: "transactionDate,desc"},
        });
        return response.data;    
    },

    getById: async (id: number): Promise<Transaction> => {
        const response = await API.get<Transaction>(`/transactions/${id}`);
        return response.data;
    },

    create: async (data: TransactionCreateRequest): Promise<Transaction> => {
        const response = await API.post<Transaction>("/transactions", data);
        return response.data;
    },

    update: async (id: number, data: TransactionCreateRequest): Promise<Transaction> => {
        const response = await API.put<Transaction>(`/transactions/${id}`, data);
        return response.data;
    },

    delete: async (id:number): Promise<void> => {
        await API.delete(`/transactions/${id}`);
    },

    getDashboard: async (): Promise<Dashboard> => {
        const response = await API.get<Dashboard>("/transactions/dashboard");
        return response.data;
    },

    getMonthlySummary: async (year: number, month: number): Promise<MonthlySummary> => { 
        const response = await API.get<MonthlySummary>("/transactions/summary/monthly", { 
            params: {year, month},
        });
        return response.data;
    },

    getCategorySummary: async (): Promise<CategorySummary[]> => {
        const response = await API.get<CategorySummary[]>("/transactions/summary/by-category");
        return response.data;
    },
};
    