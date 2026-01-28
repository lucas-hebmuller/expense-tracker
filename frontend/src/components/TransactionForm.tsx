import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { z } from "zod";
import { useCategories } from "@/hooks/useCategories";
import type {
  Transaction,
  TransactionCreateRequest,
} from "@/types/transaction.types";

const transactionSchema = z.object({
  description: z.string().min(1, "Description is required").max(255),
  amount: z.number().min(-1000000, "Amount too low").max(1000000, "Amount too high"),
  transactionDate: z.string().min(1, "Date is required"),
  categoryId: z.number().min(1, "Please select a category"),
});

type TransactionFormData = z.infer<typeof transactionSchema>;

interface TransactionFormProps {
  onSubmit: (data: TransactionCreateRequest) => void;
  onCancel: () => void;
  initialData?: Transaction;
  isLoading?: boolean;
}

function TransactionForm({
  onSubmit,
  onCancel,
  initialData,
  isLoading,
}: TransactionFormProps) {
  const { data: categories, isLoading: categoriesLoading } = useCategories();

  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<TransactionFormData>({
    resolver: zodResolver(transactionSchema),
    defaultValues: initialData
      ? {
          description: initialData.description || "",
          amount: initialData.amount,
          transactionDate: initialData.transactionDate,
          categoryId: initialData.category.id,
        }
      : {
          transactionDate: new Date().toISOString().split("T")[0], // Today's date
        },
  });

  const handleFormSubmit = (data: TransactionFormData) => {
    onSubmit({
      description: data.description,
      amount: data.amount,
      transactionDate: data.transactionDate,
      category: { id: data.categoryId },
    });
  };

  if (categoriesLoading) {
    return <p>Loading categories...</p>;
  }

  return (
    <form
      onSubmit={handleSubmit(handleFormSubmit)}
      className="transaction-form"
    >
      <div className="form-group">
        <label htmlFor="description">Description</label>
        <input
          type="text"
          id="description"
          {...register("description")}
          placeholder="e.g., Weekly groceries"
        />
        {errors.description && (
          <span className="field-error">{errors.description.message}</span>
        )}
      </div>

      <div className="form-group">
        <label htmlFor="amount">
          Amount (use negative for expenses, positive for income)
        </label>
        <input
          type="number"
          id="amount"
          step="0.01"
          {...register("amount", { valueAsNumber: true })}
          placeholder="e.g., -50.00"
        />
        {errors.amount && (
          <span className="field-error">{errors.amount.message}</span>
        )}
      </div>

      <div className="form-group">
        <label htmlFor="transactionDate">Date</label>
        <input
          type="date"
          id="transactionDate"
          {...register("transactionDate")}
        />
        {errors.transactionDate && (
          <span className="field-error">{errors.transactionDate.message}</span>
        )}
      </div>

      <div className="form-group">
        <label htmlFor="categoryId">Category</label>
        <select
          id="categoryId"
          {...register("categoryId", { valueAsNumber: true })}
        >
          <option value="">Select a category</option>
          {categories?.map((category) => (
            <option key={category.id} value={category.id}>
              {category.name}
            </option>
          ))}
        </select>
        {errors.categoryId && (
          <span className="field-error">{errors.categoryId.message}</span>
        )}
      </div>

      <div className="form-actions">
        <button type="button" onClick={onCancel} className="btn-secondary">
          Cancel
        </button>
        <button type="submit" disabled={isLoading}>
          {isLoading ? "Saving..." : initialData ? "Update" : "Create"}
        </button>
      </div>
    </form>
  );
}

export default TransactionForm;
