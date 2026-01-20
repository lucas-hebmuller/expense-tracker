import type { Category } from "./category.types";
import type { User } from "./auth.types";

export interface Transaction {
  id: number;
  description: string | null;
  amount: number;
  transactionDate: string;
  user: User;
  category: Category;
  createdAt: string;
  version: number;
}

export interface TransactionCreateRequest {
  description?: string;
  amount: number;
  transactionDate: string;
  category: {
    id: number;
  };
}

export interface MonthlySummary {
  year: number;
  month: number;
  totalIncome: number;
  totalExpenses: number;
  netAmount: number;
  transactionCount: number;
}

export interface CategorySummary {
  categoryId: number;
  categoryName: string;
  totalAmount: number;
  transactionCount: number;
}

export interface Dashboard {
  currentMonth: MonthlySummary;
  lastMonth: MonthlySummary;
  topCategory: CategorySummary | null;
  transactionCountThisMonth: number;
}

export interface PaginatedResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
  first: boolean;
  last: boolean;
  empty: boolean;
}
