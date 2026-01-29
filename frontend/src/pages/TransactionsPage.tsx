import { useState } from "react";
import Navbar from "@/components/Navbar";
import TransactionForm from "@/components/TransactionForm";
import ConfirmDialog from "@/components/ConfirmDialog";
import {
  useTransactions,
  useCreateTransaction,
  useUpdateTransaction,
  useDeleteTransaction,
} from "@/hooks/useTransactions";
import { formatCurrency } from "@/utils/formatCurrency";
import { formatDate } from "@/utils/dateHelpers";
import type { Transaction } from "@/types/transaction.types";

function TransactionPage() {
  const [page, setPage] = useState(0);
  const [showAddModal, setShowAddModal] = useState(false);
  const [editingTransaction, setEditingTransaction] =
    useState<Transaction | null>(null);
  const [deletingTransaction, setDeletingTransaction] =
    useState<Transaction | null>(null);

  const { data: transactionsPage, isLoading } = useTransactions(page, 10);
  const createMutation = useCreateTransaction();
  const updateMutation = useUpdateTransaction();
  const deleteMutation = useDeleteTransaction();

  const handleCreate = (data: any) => {
    createMutation.mutate(data, {
      onSuccess: () => {
        setShowAddModal(false);
      },
    });
  };

  const handleUpdate = (data: any) => {
    if (editingTransaction) {
      updateMutation.mutate(
        { id: editingTransaction.id, data },
        {
          onSuccess: () => {
            setEditingTransaction(null);
          },
        },
      );
    }
  };

  const handleDelete = () => {
    if (deletingTransaction) {
      deleteMutation.mutate(deletingTransaction.id, {
        onSuccess: () => {
          setDeletingTransaction(null);
        },
      });
    }
  };

  return (
    <div>
      <Navbar />

      <main className="main-content">
        <div className="page-header">
          <h2>Transactions</h2>
          <button onClick={() => setShowAddModal(true)} className="btn-primary">
            + Add Transaction
          </button>
        </div>

        {isLoading ? (
          <p>Loading transactions...</p>
        ) : transactionsPage && transactionsPage.content.length > 0 ? (
          <>
            <div className="transaction-list">
              {transactionsPage.content.map((transaction) => (
                <div key={transaction.id} className="transaction-item">
                  <div className="transaction-info">
                    <p className="transaction-description">
                      {transaction.description || "No description"}
                    </p>
                    <p className="transaction-meta">
                      {transaction.category.name} •
                      {formatDate(transaction.transactionDate)}
                    </p>
                  </div>
                  <div className="transaction-actions">
                    <p
                      className={`transaction-amount ${
                        transaction.amount >= 0
                          ? "amount-positive"
                          : "amount-negative"
                      }`}
                    >
                      {formatCurrency(transaction.amount)}
                    </p>
                    <div className="action-buttons">
                      <button
                        onClick={() => setEditingTransaction(transaction)}
                        className="btn-small"
                      >
                        Edit
                      </button>
                      <button
                        onClick={() => setDeletingTransaction(transaction)}
                        className="btn-small btn-danger"
                      >
                        Delete
                      </button>
                    </div>
                  </div>
                </div>
              ))}
            </div>

            {/* Pagination */}
            <div className="pagination">
              <button
                onClick={() => setPage(page - 1)}
                disabled={page === 0}
                className="btn-secondary"
              >
                Previous
              </button>
              <span className="pagination-info">
                Page {page + 1} of {transactionsPage.totalPages}
              </span>
              <button
                onClick={() => setPage(page + 1)}
                disabled={page >= transactionsPage.totalPages - 1}
                className="btn-secondary"
              >
                Next
              </button>
            </div>
          </>
        ) : (
          <div className="empty-state">
            <p>No transactions yet. Add your first one!</p>
          </div>
        )}
      </main>

      {/* Add Transaction Modal */}
      {showAddModal && (
        <div className="modal-overlay" onClick={() => setShowAddModal(false)}>
          <div className="modal-content" onClick={(e) => e.stopPropagation()}>
            <h2>Add Transaction</h2>
            <TransactionForm
              onSubmit={handleCreate}
              onCancel={() => setShowAddModal(false)}
              isLoading={createMutation.isPending}
            />
          </div>
        </div>
      )}

      {/* Edit Transaction Modal */}
      {editingTransaction && (
        <div
          className="modal-overlay"
          onClick={() => setEditingTransaction(null)}
        >
          <div className="modal-content" onClick={(e) => e.stopPropagation()}>
            <h2>Edit Transaction</h2>
            <TransactionForm
              onSubmit={handleUpdate}
              onCancel={() => setEditingTransaction(null)}
              initialData={editingTransaction}
              isLoading={updateMutation.isPending}
            />
          </div>
        </div>
      )}

      {/* Delete Confirmation */}
      {deletingTransaction && (
        <ConfirmDialog 
          title="Delete Transaction"
          message={`Are you sure you want to delete ${deletingTransaction.description || "this transaction"}?`}
          onConfirm={handleDelete}
          onCancel={() => setDeletingTransaction(null)}
          isLoading={deleteMutation.isPending}
        />
      )}
    </div>
  );
}

export default TransactionPage;
