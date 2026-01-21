import API from "./axiosConfig";
import {
    type Transaction,
    type TransactionCreateRequest.
    type PaginatedResponse,
    type Dashboard,
    type MonthlySummary,
    type CategorySummary,
} from "../types/transaction.types";

export const transactionApi = {
    getAll: async (page: number = 0, size: number = 10): Promise<PaginatedResponse<Transaction>> => {
        
    }
}