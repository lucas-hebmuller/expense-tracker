package com.expensetracker.service;

import com.expensetracker.dto.CategorySummaryDTO;
import com.expensetracker.dto.DashboardDTO;
import com.expensetracker.dto.DateRangeSummaryDTO;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Year;
import java.util.*;

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

    public Page<Transaction> getAllTransactions(Pageable pageable) {
        return transactionRepository.findAll(pageable);
    }

    public Page<Transaction> getTransactionsByUserId(Long userId, Pageable pageable) {
        return transactionRepository.findByUser_Id(userId, pageable);
    }

    public Page<Transaction> getTransactionsByCategoryId(Long categoryId, Pageable pageable) {
        return transactionRepository.findByCategory_Id(categoryId, pageable);
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

    public DateRangeSummaryDTO getDateRangeSummary(Long userId, LocalDate startDate, LocalDate endDate) {
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null.");
        }

        if (startDate == null || startDate.isAfter(LocalDate.now()) ||
                endDate == null || endDate.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Dates cannot be null or in the future.");
        }

        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Start date cannot be after end date.");
        }

        DateRangeSummaryDTO summary = transactionRepository.getDateRangeSummary(userId, startDate, endDate);

        if (summary == null) {
            summary = new DateRangeSummaryDTO(
                    startDate,
                    endDate,
                    BigDecimal.ZERO,
                    BigDecimal.ZERO,
                    0L
            );
        }

        return summary;
    }

    public DashboardDTO getDashboardStats(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null.");
        }

        int currentYear = LocalDate.now().getYear();
        int currentMonth = LocalDate.now().getMonthValue();

        int lastYear;
        int lastMonth;

        if (currentMonth == 1) {
            lastYear = currentYear - 1;
            lastMonth = 12;
        }
        else {
            lastYear = currentYear;
            lastMonth = currentMonth - 1;
        }

        MonthlySummaryDTO currentMonthSummary = getMonthlySummary(userId, currentYear, currentMonth);
        MonthlySummaryDTO lastMonthSummary = getMonthlySummary(userId, lastYear, lastMonth);

        List<CategorySummaryDTO> categoriesOfThisMonth = transactionRepository.getCategorySummaryByMonth(userId, currentYear, currentMonth);
        CategorySummaryDTO topCategory = categoriesOfThisMonth.isEmpty() ? null : categoriesOfThisMonth.get(0);

        Integer transactionCount = currentMonthSummary.getTransactionCount();

        return new DashboardDTO(
                currentMonthSummary,
                lastMonthSummary,
                topCategory,
                transactionCount
        );
    }

}