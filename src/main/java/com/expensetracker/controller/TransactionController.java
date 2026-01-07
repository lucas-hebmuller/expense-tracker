package com.expensetracker.controller;

import com.expensetracker.dto.CategorySummaryDTO;
import com.expensetracker.dto.DashboardDTO;
import com.expensetracker.dto.DateRangeSummaryDTO;
import com.expensetracker.dto.MonthlySummaryDTO;
import com.expensetracker.exception.TransactionNotFoundException;
import com.expensetracker.model.Transaction;
import com.expensetracker.security.SecurityUtil;
import com.expensetracker.service.TransactionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @GetMapping
    public ResponseEntity<Page<Transaction>> getMyTransactions(
            @PageableDefault(size = 10, sort = "transactionDate", direction = Sort.Direction.DESC)
            Pageable pageable) {
        Long userId = SecurityUtil.getCurrentUserId();
        Page<Transaction> transactions = transactionService.getTransactionsByUserId(userId, pageable);
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Transaction> getTransactionById(@PathVariable Long id) {
        Long userId = SecurityUtil.getCurrentUserId();
        Transaction transaction = transactionService.getTransactionById(id)
                .orElseThrow(() -> new TransactionNotFoundException(id));

        // Verify ownership
        if (!transaction.getUser().getId().equals(userId)) {
            throw new TransactionNotFoundException(id);
        }

        return ResponseEntity.ok(transaction);
    }

    @PostMapping
    public ResponseEntity<Transaction> createTransaction(@Valid @RequestBody Transaction transaction) {
        Transaction createdTransaction = transactionService.createTransaction(transaction);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTransaction);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Transaction> updateTransaction(
            @PathVariable Long id,
            @Valid @RequestBody Transaction transactionDetails) {
        Long userId = SecurityUtil.getCurrentUserId();
        Transaction updatedTransaction = transactionService.updateTransaction(id, transactionDetails, userId);
        return ResponseEntity.ok(updatedTransaction);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTransaction(@PathVariable Long id) {
        Long userId = SecurityUtil.getCurrentUserId();
        transactionService.deleteTransaction(id, userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/summary/monthly")
    public ResponseEntity<MonthlySummaryDTO> getMonthlySummary(
            @RequestParam Integer year,
            @RequestParam Integer month) {
        Long userId = SecurityUtil.getCurrentUserId();
        MonthlySummaryDTO summary = transactionService.getMonthlySummary(userId, year, month);
        return ResponseEntity.ok(summary);
    }

    @GetMapping("/summary/by-category")
    public ResponseEntity<List<CategorySummaryDTO>> getCategorySummary() {
        Long userId = SecurityUtil.getCurrentUserId();
        List<CategorySummaryDTO> summaries = transactionService.getCategorySummary(userId);
        return ResponseEntity.ok(summaries);
    }

    @GetMapping("/summary/date-range")
    public ResponseEntity<DateRangeSummaryDTO> getDateRangeSummary(
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate) {
        Long userId = SecurityUtil.getCurrentUserId();
        DateRangeSummaryDTO summary = transactionService.getDateRangeSummary(userId, startDate, endDate);
        return ResponseEntity.ok(summary);
    }

    @GetMapping("/dashboard")
    public ResponseEntity<DashboardDTO> getDashboardStats() {
        Long userId = SecurityUtil.getCurrentUserId();
        DashboardDTO dashboard = transactionService.getDashboardStats(userId);
        return ResponseEntity.ok(dashboard);
    }
}