package com.expensetracker.service;

import com.expensetracker.model.Category;
import com.expensetracker.model.Transaction;
import com.expensetracker.repository.CategoryRepository;
import com.expensetracker.repository.TransactionRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    private void validateTransaction(Transaction transaction) {
        if (transaction.getUser() == null || transaction.getUser().getId() == null) {
            throw new RuntimeException("Transaction must belong to a user!");
        }

        if (transaction.getCategory() == null || transaction.getCategory().getId() == null) {
            throw new RuntimeException("Transaction must belong to a category!");
        }

        Category category = categoryRepository.findById(transaction.getCategory().getId())
                .orElseThrow(() -> new RuntimeException("Category not found!"));

        if (!Objects.equals(transaction.getUser().getId(), category.getUser().getId())) {
            throw new RuntimeException("Cannot create transaction: Category (ID: " +
                    transaction.getCategory().getId() + ") does not belong to User (ID: " +
                    transaction.getUser().getId() + ")");
        }

        transaction.setCategory(category);
    }

    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

    public List<Transaction> getTransactionsByUserId(Long userId) {
        return transactionRepository.findByUser_Id(userId);
    }

    public List<Transaction> getTransactionsByCategoryId(Long categoryId) {
        return transactionRepository.findByCategory_Id(categoryId);
    }

    public Optional<Transaction> getTransactionById(Long id) {
        return transactionRepository.findById(id);
    }

    @Transactional
    public Transaction createTransaction(Transaction transaction) {
        validateTransaction(transaction);

        return transactionRepository.save(transaction);
    }

    @Transactional
    public Transaction updateTransaction(Long id, Transaction transactionDetails) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction not found with id: " + id));

        validateTransaction(transactionDetails);

        transaction.setDescription(transactionDetails.getDescription());
        transaction.setAmount(transactionDetails.getAmount());
        transaction.setTransactionDate(transactionDetails.getTransactionDate());
        transaction.setUser(transactionDetails.getUser());
        transaction.setCategory(transactionDetails.getCategory());

        return transactionRepository.save(transaction);
    }

    @Transactional
    public void deleteTransaction(Long id) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction not found with id: " + id));

        transactionRepository.delete(transaction);
    }
}