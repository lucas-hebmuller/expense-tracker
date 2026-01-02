package com.expensetracker.service;

import com.expensetracker.dto.CategorySummaryDTO;
import com.expensetracker.dto.MonthlySummaryDTO;
import com.expensetracker.exception.CategoryNotFoundException;
import com.expensetracker.exception.TransactionNotFoundException;
import com.expensetracker.exception.UnauthorizedCategoryAccessException;
import com.expensetracker.model.Category;
import com.expensetracker.model.Transaction;
import com.expensetracker.repository.CategoryRepository;
import com.expensetracker.repository.TransactionRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Year;
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
            throw new IllegalArgumentException("Transaction must belong to a user!");
        }

        if (transaction.getCategory() == null || transaction.getCategory().getId() == null) {
            throw new IllegalArgumentException("Transaction must belong to a category!");
        }

        Category category = categoryRepository.findById(transaction.getCategory().getId())
                .orElseThrow(() -> new CategoryNotFoundException(transaction.getCategory().getId()));

        if (!Objects.equals(transaction.getUser().getId(), category.getUser().getId())) {
            throw new UnauthorizedCategoryAccessException(
                    transaction.getCategory().getId(),
                    transaction.getUser().getId());
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
                .orElseThrow(() -> new TransactionNotFoundException(id));

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
                .orElseThrow(() -> new TransactionNotFoundException(id));

        transactionRepository.delete(transaction);
    }

    public MonthlySummaryDTO getMonthlySummary(Long userId, Integer year, Integer month) {
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null.");
        }

        if (year == null || year < Year.now().getValue() - 100 || year > Year.now().getValue() + 100) {
            throw new IllegalArgumentException("Year must be within a range of 100 years from now.");
        }

        if (month == null || month < 1 || month > 12) {
            throw new IllegalArgumentException("Month must be between 1 and 12.");
        }

        MonthlySummaryDTO summary = transactionRepository.getMonthlySummary(userId, year, month);

        if (summary == null) {
            summary = new MonthlySummaryDTO(
                    year,
                    month,
                    BigDecimal.ZERO,
                    BigDecimal.ZERO,
                    0
            );
        }

        return summary;
    }

    public List<CategorySummaryDTO> getCategorySummary(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null.");
        }

        List<CategorySummaryDTO> summaries = transactionRepository.getCategorySummary(userId);

        return summaries;
    }
}