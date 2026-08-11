import type {
  Dashboard,
  MonthlySummary,
  CategorySummary,
  PaginatedResponse,
  Transaction,
} from "@/types/transaction.types";
import type { User } from "@/types/auth.types";
import type { Category } from "@/types/category.types";

const demoUser: User = {
  id: 1,
  name: "Alex Morgan",
  email: "alex@example.com",
  createdAt: "2026-01-15T09:00:00Z",
};

const cat = (id: number, name: string): Category => ({
  id,
  name,
  user: demoUser,
  version: 0,
});

const categories = {
  income: cat(1, "Income"),
  rent: cat(2, "Rent"),
  groceries: cat(3, "Groceries"),
  transportation: cat(4, "Transportation"),
  dining: cat(5, "Dining"),
  entertainment: cat(6, "Entertainment"),
  miscellaneous: cat(7, "Miscellaneous"),
};

export const demoDashboard: Dashboard = {
  currentMonth: {
    year: 2026,
    month: 8,
    totalIncome: 4200,
    totalExpenses: -2590,
    netAmount: 1610,
    transactionCount: 23,
  },
  lastMonth: {
    year: 2026,
    month: 7,
    totalIncome: 4200,
    totalExpenses: -2890,
    netAmount: 1310,
    transactionCount: 27,
  },
  topCategory: {
    categoryId: 2,
    categoryName: "Rent",
    totalAmount: -1400,
    transactionCount: 1,
  },
  transactionCountThisMonth: 23,
};

export const demoTrend: MonthlySummary[] = [
  {
    year: 2026,
    month: 3,
    totalIncome: 4200,
    totalExpenses: -2700,
    netAmount: 1500,
    transactionCount: 25,
  },
  {
    year: 2026,
    month: 4,
    totalIncome: 4200,
    totalExpenses: -3100,
    netAmount: 1100,
    transactionCount: 29,
  },
  {
    year: 2026,
    month: 5,
    totalIncome: 4500,
    totalExpenses: -2600,
    netAmount: 1900,
    transactionCount: 22,
  },
  {
    year: 2026,
    month: 6,
    totalIncome: 4200,
    totalExpenses: -2950,
    netAmount: 1250,
    transactionCount: 26,
  },
  {
    year: 2026,
    month: 7,
    totalIncome: 4200,
    totalExpenses: -2890,
    netAmount: 1310,
    transactionCount: 27,
  },
  {
    year: 2026,
    month: 8,
    totalIncome: 4200,
    totalExpenses: -2590,
    netAmount: 1610,
    transactionCount: 23,
  },
];

export const demoCategorySummary: CategorySummary[] = [
  {
    categoryId: 1,
    categoryName: "Income",
    totalAmount: 4200,
    transactionCount: 1,
  },
  {
    categoryId: 2,
    categoryName: "Rent",
    totalAmount: -1400,
    transactionCount: 1,
  },
  {
    categoryId: 3,
    categoryName: "Groceries",
    totalAmount: -520,
    transactionCount: 8,
  },
  {
    categoryId: 5,
    categoryName: "Dining",
    totalAmount: -260,
    transactionCount: 6,
  },
  {
    categoryId: 4,
    categoryName: "Transportation",
    totalAmount: -180,
    transactionCount: 4,
  },
  {
    categoryId: 6,
    categoryName: "Entertainment",
    totalAmount: -140,
    transactionCount: 3,
  },
  {
    categoryId: 7,
    categoryName: "Miscellaneous",
    totalAmount: -90,
    transactionCount: 1,
  },
];

const demoTransactions: Transaction[] = [
  {
    id: 105,
    description: "Dinner with friends",
    amount: -63.75,
    transactionDate: "2026-08-09",
    user: demoUser,
    category: categories.dining,
    createdAt: "2026-08-09T20:15:00Z",
    version: 0,
  },
  {
    id: 104,
    description: "Gas station",
    amount: -52.1,
    transactionDate: "2026-08-07",
    user: demoUser,
    category: categories.transportation,
    createdAt: "2026-08-07T08:30:00Z",
    version: 0,
  },
  {
    id: 103,
    description: "Whole Foods",
    amount: -86.4,
    transactionDate: "2026-08-05",
    user: demoUser,
    category: categories.groceries,
    createdAt: "2026-08-05T17:45:00Z",
    version: 0,
  },
  {
    id: 102,
    description: "Rent payment",
    amount: -1400,
    transactionDate: "2026-08-02",
    user: demoUser,
    category: categories.rent,
    createdAt: "2026-08-02T09:00:00Z",
    version: 0,
  },
  {
    id: 101,
    description: "Monthly salary",
    amount: 4200,
    transactionDate: "2026-08-01",
    user: demoUser,
    category: categories.income,
    createdAt: "2026-08-01T09:00:00Z",
    version: 0,
  },
];

export const demoTransactionsPage: PaginatedResponse<Transaction> = {
  content: demoTransactions,
  totalElements: 23,
  totalPages: 5,
  size: 5,
  number: 0,
  first: true,
  last: false,
  empty: false,
};
