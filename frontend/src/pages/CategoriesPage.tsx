import { useState } from "react";
import Navbar from "@/components/Navbar";
import CategoryForm from "@/components/CategoryForm";
import ConfirmDialog from "@/components/ConfirmDialog";
import {
  useCategories,
  useCreateCategory,
  useUpdateCategory,
  useDeleteCategory,
} from "@/hooks/useCategories";
import type { Category } from "@/types/category.types";

function CategoriesPage() {
  const [showAddModal, setShowAddModal] = useState(false);
  const [editingCategory, setEditingCategory] = useState<Category | null>(null);
  const [deletingCategory, setDeletingCategory] = useState<Category | null>(
    null,
  );

  const { data: categories, isLoading } = useCategories();
  const createMutation = useCreateCategory();
  const updateMutation = useUpdateCategory();
  const deleteMutation = useDeleteCategory();

  const handleCreate = (data: { name: string }) => {
    createMutation.mutate(data, {
      onSuccess: () => {
        setShowAddModal(false);
      },
    });
  };

  const handleUpdate = (data: { name: string }) => {
    if (editingCategory) {
      updateMutation.mutate(
        { id: editingCategory.id, data },
        {
          onSuccess: () => {
            setEditingCategory(null);
          },
        },
      );
    }
  };

  const handleDelete = () => {
    if (deletingCategory) {
      deleteMutation.mutate(deletingCategory.id, {
        onSuccess: () => {
          setDeletingCategory(null);
        },
        onError: (error: any) => {
          console.error(
            "Cannot delete category: ",
            error.response?.data?.message,
          );
          alert(
            error.response?.data?.message ||
              "Cannot delete category with existing transactions",
          );
        },
      });
    }
  };

  return (
    <div>
      <Navbar />

      <main className="main-content">
        <div className="page-header">
          <h2>Categories</h2>
          <button onClick={() => setShowAddModal(true)} className="btn-primary">
            + Add Category
          </button>
        </div>

        {isLoading ? (
          <p>Loading categories...</p>
        ) : categories && categories.length > 0 ? (
          <div className="category-grid">
            {categories.map((category) => (
              <div key={category.id} className="category-card">
                <div className="category-info">
                  <h3>{category.name}</h3>
                </div>
                <div className="category-actions">
                  <button
                    onClick={() => setEditingCategory(category)}
                    className="btn-small"
                  >
                    Edit
                  </button>
                  <button
                    onClick={() => setDeletingCategory(category)}
                    className="btn-small btn-danger"
                  >
                    Delete
                  </button>
                </div>
              </div>
            ))}
          </div>
        ) : (
          <div className="empty-state">
            <p>No categories yet. Add your first one!</p>
          </div>
        )}
      </main>

      {/* Add Category Modal */}
      {showAddModal && (
        <div className="modal-overlay" onClick={() => setShowAddModal(false)}>
          <div className="modal-content" onClick={(e) => e.stopPropagation()}>
            <h2>Add Category</h2>
            <CategoryForm
              onSubmit={handleCreate}
              onCancel={() => setShowAddModal(false)}
              isLoading={createMutation.isPending}
            />
          </div>
        </div>
      )}

      {/* Edit Category Modal */}
      {editingCategory && (
        <div className="modal-overlay" onClick={() => setEditingCategory(null)}>
          <div className="modal-content" onClick={(e) => e.stopPropagation()}>
            <h2>Edit Category</h2>
            <CategoryForm
              onSubmit={handleUpdate}
              onCancel={() => setEditingCategory(null)}
              initialData={editingCategory}
              isLoading={updateMutation.isPending}
            />
          </div>
        </div>
      )}

      {/* Delete Confirmation */}
      {deletingCategory && (
        <ConfirmDialog
          title="Delete Category"
          message={`Are you sure you want to delete "${deletingCategory.name}"?`}
          onConfirm={handleDelete}
          onCancel={() => setDeletingCategory(null)}
          isLoading={deleteMutation.isPending}
        />
      )}
    </div>
  );
}

export default CategoriesPage;
