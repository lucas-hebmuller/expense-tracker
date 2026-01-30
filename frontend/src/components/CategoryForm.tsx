import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { z } from "zod";
import type { Category } from "@/types/category.types";

const categorySchema = z.object({
  name: z
    .string()
    .min(2, "Name must be at least 2 characters")
    .max(50, "Name must be less than 50 characters"),
});

type CategoryFormData = z.infer<typeof categorySchema>;

interface CategoryFormProps {
  onSubmit: (data: { name: string }) => void;
  onCancel: () => void;
  initialData?: Category;
  isLoading?: boolean;
}

function CategoryForm({
  onSubmit,
  onCancel,
  initialData,
  isLoading,
}: CategoryFormProps) {
  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<CategoryFormData>({
    resolver: zodResolver(categorySchema),
    defaultValues: initialData ? { name: initialData.name } : {},
  });

  return (
    <form onSubmit={handleSubmit(onSubmit)} className="category-form">
      <div className="form-group">
        <label htmlFor="name">Category Name</label>
        <input
          type="text"
          id="name"
          {...register("name")}
          placeholder="e.g. Groceries"
        />
        {errors.name && (
          <span className="field-error">{errors.name.message}</span>
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

export default CategoryForm;
