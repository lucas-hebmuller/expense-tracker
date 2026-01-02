package com.expensetracker.controller;

import com.expensetracker.dto.CategorySummaryDTO;
import com.expensetracker.dto.DashboardDTO;
import com.expensetracker.dto.DateRangeSummaryDTO;
import com.expensetracker.dto.MonthlySummaryDTO;
import com.expensetracker.exception.TransactionNotFoundException;
import com.expensetracker.model.Transaction;
import com.expensetracker.service.TransactionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
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
    public ResponseEntity<List<Transaction>> getAllTransactions() {
        List<Transaction> transactions = transactionService.getAllTransactions();
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Transaction>> getTransactionsByUserId(@PathVariable Long userId) {
        List<Transaction> transactions = transactionService.getTransactionsByUserId(userId);
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<Transaction>> getTransactionsByCategoryId(@PathVariable Long categoryId) {
        List<Transaction> transactions = transactionService.getTransactionsByCategoryId(categoryId);
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Transaction> getTransactionById(@PathVariable Long id) {
        Transaction transaction = transactionService.getTransactionById(id)
                .orElseThrow(() -> new TransactionNotFoundException(id));
        return ResponseEntity.ok(transaction);
    }

    @PostMapping
    public ResponseEntity<Transaction> createTransaction(@Valid @RequestBody Transaction transaction) {
        Transaction createdTransaction = transactionService.createTransaction(transaction);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTransaction);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Transaction> updateTransaction(@PathVariable Long id, @Valid @RequestBody Transaction transactionDetails) {
        Transaction updatedTransaction = transactionService.updateTransaction(id, transactionDetails);
        return ResponseEntity.ok(updatedTransaction);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTransaction(@PathVariable Long id) {
        transactionService.deleteTransaction(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/summary/monthly")
    public ResponseEntity<MonthlySummaryDTO> getMonthlySummary(
            @RequestParam Long userId,
            @RequestParam Integer year,
            @RequestParam Integer month) {
        MonthlySummaryDTO summary = transactionService.getMonthlySummary(userId, year, month);
        return ResponseEntity.ok(summary);
    }

    @GetMapping("/summary/by-category")
    public ResponseEntity<List<CategorySummaryDTO>> getCategorySummary(@RequestParam Long userId) {
        List<CategorySummaryDTO> summaries = transactionService.getCategorySummary(userId);
        return ResponseEntity.ok(summaries);
    }

    @GetMapping("/summary/date-range")
    public ResponseEntity<DateRangeSummaryDTO> getDateRangeSummary(
            @RequestParam Long userId,
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate) {
        DateRangeSummaryDTO summary = transactionService.getDateRangeSummary(userId, startDate, endDate);
        return ResponseEntity.ok(summary);
    }

    @GetMapping("/dashboard")
    public ResponseEntity<DashboardDTO> getDashboardStats(@RequestParam Long userId) {
        DashboardDTO dashboard = transactionService.getDashboardStats(userId);
        return ResponseEntity.ok(dashboard);
    }
}