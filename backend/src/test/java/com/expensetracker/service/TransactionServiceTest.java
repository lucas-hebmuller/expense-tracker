package com.expensetracker.service;

import com.expensetracker.dto.CategorySummaryDTO;
import com.expensetracker.dto.DateRangeSummaryDTO;
import com.expensetracker.dto.MonthlySummaryDTO;
import com.expensetracker.exception.CategoryNotFoundException;
import com.expensetracker.exception.TransactionNotFoundException;
import com.expensetracker.exception.UnauthorizedCategoryAccessException;
import com.expensetracker.model.Category;
import com.expensetracker.model.Transaction;
import com.expensetracker.model.User;
import com.expensetracker.repository.CategoryRepository;
import com.expensetracker.repository.TransactionRepository;
import com.expensetracker.security.SecurityUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private  TransactionService transactionService;

    @Test
    void createTransaction_WithValidData_ShouldReturnSavedTransaction() {
        Long userId = 1L;
        Long categoryId = 1L;

        User user = new User();
        user.setId(userId);

        Category category = new Category();
        category.setId(categoryId);
        category.setName("Groceries");
        category.setUser(user);

        Transaction inputTransaction = new Transaction();
        inputTransaction.setDescription("Weekly groceries");
        inputTransaction.setAmount(new BigDecimal("-50.00"));
        inputTransaction.setTransactionDate(LocalDate.now());
        inputTransaction.setCategory(category);

        Transaction savedTransaction = new Transaction();
        savedTransaction.setId(1L);
        savedTransaction.setDescription("Weekly groceries");
        savedTransaction.setAmount(new BigDecimal("-50.00"));
        savedTransaction.setTransactionDate(LocalDate.now());
        savedTransaction.setCategory(category);
        savedTransaction.setUser(user);

        try (MockedStatic<SecurityUtil> securityUtil = mockStatic(SecurityUtil.class)) {
            securityUtil.when(SecurityUtil::getCurrentUserId).thenReturn(userId);

            when(categoryRepository.findById(categoryId))
                    .thenReturn(Optional.of(category));

            when(transactionRepository.save(any(Transaction.class)))
                    .thenReturn(savedTransaction);

            Transaction result = transactionService.createTransaction(inputTransaction);

            assertNotNull(result);
            assertEquals(1L, result.getId());
            assertEquals(categoryId, result.getCategory().getId());
            assertEquals("Groceries", result.getCategory().getName());
            assertEquals(userId, result.getUser().getId());
            assertEquals("Weekly groceries", result.getDescription());
            assertEquals(new BigDecimal("-50.00"), result.getAmount());
            assertEquals(LocalDate.now(), result.getTransactionDate());

            verify(categoryRepository).findById(categoryId);
            verify(transactionRepository).save(any(Transaction.class));
        }
    }

    @Test
    void createTransaction_WithNonExistentCategory_ShouldThrowException() {
        Long userId = 1L;
        Long categoryId = 99L;

        Category categoryRef = new Category();
        categoryRef.setId(categoryId);

        Transaction inputTransaction = new Transaction();
        inputTransaction.setDescription("Weekly groceries");
        inputTransaction.setAmount(new BigDecimal("-50.00"));
        inputTransaction.setTransactionDate(LocalDate.now());
        inputTransaction.setCategory(categoryRef);

        try (MockedStatic<SecurityUtil> securityUtil = mockStatic(SecurityUtil.class)) {
            securityUtil.when(SecurityUtil::getCurrentUserId).thenReturn(userId);

            when(categoryRepository.findById(categoryId))
                    .thenReturn(Optional.empty());

            assertThrows(CategoryNotFoundException.class, () -> {
                transactionService.createTransaction(inputTransaction);
            });

            verify(transactionRepository, never()).save(any(Transaction.class));
        }
    }

    @Test
    void createTransaction_WithOtherUsersCategory_ShouldThrowException() {
        Long userId = 1L;
        Long otherUserId = 2L;
        Long categoryId = 1L;

        User otherUser = new User();
        otherUser.setId(otherUserId);

        Category othersCategory = new Category();
        othersCategory.setId(categoryId);
        othersCategory.setName("Other's Category");
        othersCategory.setUser(otherUser);

        Category categoryRef = new Category();
        categoryRef.setId(categoryId);

        Transaction inputTransaction = new Transaction();
        inputTransaction.setDescription("Test");
        inputTransaction.setAmount(new BigDecimal("-50.00"));
        inputTransaction.setTransactionDate(LocalDate.now());
        inputTransaction.setCategory(categoryRef);

        try (MockedStatic<SecurityUtil> securityUtil = mockStatic(SecurityUtil.class)) {
            securityUtil.when(SecurityUtil::getCurrentUserId).thenReturn(userId);

            when(categoryRepository.findById(categoryId))
                    .thenReturn(Optional.of(othersCategory));

            assertThrows(UnauthorizedCategoryAccessException.class, () -> {
                transactionService.createTransaction(inputTransaction);
            });

            verify(transactionRepository, never()).save(any(Transaction.class));
        }
    }

    @Test
    void deleteTransaction_WithValidData_ShouldSucceed() {
        Long userId = 1L;
        Long transactionId = 1L;

        User user = new User();
        user.setId(userId);

        Transaction existingTransaction = new Transaction();
        existingTransaction.setId(transactionId);
        existingTransaction.setDescription("Test");
        existingTransaction.setUser(user);

        when(transactionRepository.findById(transactionId))
                .thenReturn(Optional.of(existingTransaction));

        transactionService.deleteTransaction(transactionId, userId);

        verify(transactionRepository).findById(transactionId);
        verify(transactionRepository).delete(existingTransaction);
    }

    @Test
    void deleteTransaction_WithWrongUser_ShouldThrowException() {
        Long ownerUserId = 1L;
        Long wrongUserId = 2L;
        Long transactionId = 1L;

        User owner = new User();
        owner.setId(ownerUserId);

        Transaction existingTransaction = new Transaction();
        existingTransaction.setId(transactionId);
        existingTransaction.setUser(owner);

        when(transactionRepository.findById(transactionId))
                .thenReturn(Optional.of(existingTransaction));

        assertThrows(TransactionNotFoundException.class, () -> {
            transactionService.deleteTransaction(transactionId, wrongUserId);
        });

        verify(transactionRepository, never()).delete(any(Transaction.class));
    }

    @Test
    void deleteTransaction_NotFound_ShouldThrowException() {
        Long userId = 1L;
        Long transactionId = 99L;

        when(transactionRepository.findById(transactionId))
                .thenReturn(Optional.empty());

        assertThrows(TransactionNotFoundException.class, () -> {
            transactionService.deleteTransaction(transactionId, userId);
        });

        verify(transactionRepository, never()).delete(any(Transaction.class));
    }

    @Test
    void updateTransaction_WithValidData_ShouldReturnUpdatedTransaction() {
        Long userId = 1L;
        Long transactionId = 1L;
        Long categoryId = 1L;

        User user = new User();
        user.setId(userId);

        Category category = new Category();
        category.setId(categoryId);
        category.setName("Groceries");
        category.setUser(user);

        Transaction existingTransaction = new Transaction();
        existingTransaction.setId(transactionId);
        existingTransaction.setDescription("Old description");
        existingTransaction.setAmount(new BigDecimal("-30.00"));
        existingTransaction.setTransactionDate(LocalDate.now().minusDays(1));
        existingTransaction.setCategory(category);
        existingTransaction.setUser(user);

        Transaction updateDetails = new Transaction();
        updateDetails.setDescription("New description");
        updateDetails.setAmount(new BigDecimal("-75.00"));
        updateDetails.setTransactionDate(LocalDate.now());
        updateDetails.setCategory(category);

        Transaction updatedTransaction = new Transaction();
        updatedTransaction.setId(transactionId);
        updatedTransaction.setDescription("New description");
        updatedTransaction.setAmount(new BigDecimal("-75.00"));
        updatedTransaction.setTransactionDate(LocalDate.now());
        updatedTransaction.setCategory(category);
        updatedTransaction.setUser(user);

        when(transactionRepository.findById(transactionId))
                .thenReturn(Optional.of(existingTransaction));

        when(categoryRepository.findById(categoryId))
                .thenReturn(Optional.of(category));

        when(transactionRepository.save(any(Transaction.class)))
                .thenReturn(updatedTransaction);

        Transaction result = transactionService.updateTransaction(transactionId, updateDetails, userId);

        assertNotNull(result);
        assertEquals("New description", result.getDescription());
        assertEquals(new BigDecimal("-75.00"), result.getAmount());
        assertEquals(LocalDate.now(), result.getTransactionDate());

        verify(transactionRepository).findById(transactionId);
        verify(transactionRepository).save(any(Transaction.class));
    }

    @Test
    void updateTransaction_WithWrongUser_ShouldThrowException() {
        Long ownerUserId = 1L;
        Long wrongUserId = 2L;
        Long transactionId = 1L;

        User owner = new User();
        owner.setId(ownerUserId);

        Transaction existingTransaction = new Transaction();
        existingTransaction.setId(transactionId);
        existingTransaction.setUser(owner);

        Transaction updateDetails = new Transaction();
        updateDetails.setDescription("Hacked!");

        when(transactionRepository.findById(transactionId))
                .thenReturn(Optional.of(existingTransaction));

        assertThrows(TransactionNotFoundException.class, () -> {
            transactionService.updateTransaction(transactionId, updateDetails, wrongUserId);
        });

        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    void updateTransaction_NotFound_ShouldThrowException() {
        Long userId = 1L;
        Long transactionId = 99L;

        Transaction updateDetails = new Transaction();
        updateDetails.setDescription("Test");

        when(transactionRepository.findById(transactionId))
                .thenReturn(Optional.empty());

        assertThrows(TransactionNotFoundException.class, () -> {
            transactionService.updateTransaction(transactionId, updateDetails, userId);
        });

        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    void getMonthlySummary_WithValidData_ShouldReturnSummary() {
        Long userId = 1L;
        Integer year = 2025;
        Integer month = 6;

        MonthlySummaryDTO expectedSummary = new MonthlySummaryDTO(
                year,
                month,
                new BigDecimal("1000.00"),
                new BigDecimal("-500.00"),
                10
        );

        when(transactionRepository.getMonthlySummary(userId, year, month))
                .thenReturn(expectedSummary);

        MonthlySummaryDTO result = transactionService.getMonthlySummary(userId, year, month);

        assertNotNull(result);
        assertEquals(year, result.getYear());
        assertEquals(month, result.getMonth());
        assertEquals(new BigDecimal("1000.00"), result.getTotalIncome());
        assertEquals(new BigDecimal("-500.00"), result.getTotalExpenses());
        assertEquals(10, result.getTransactionCount());
    }

    @Test
    void getMonthlySummary_WithNoData_ShouldReturnEmptyDTO() {
        Long userId = 1L;
        Integer year = 2025;
        Integer month = 6;

        when(transactionRepository.getMonthlySummary(userId, year, month))
                .thenReturn(null);

        MonthlySummaryDTO result = transactionService.getMonthlySummary(userId, year, month);

        assertNotNull(result);
        assertEquals(year, result.getYear());
        assertEquals(month, result.getMonth());
        assertEquals(BigDecimal.ZERO, result.getTotalIncome());
        assertEquals(BigDecimal.ZERO, result.getTotalExpenses());
        assertEquals(0, result.getTransactionCount());
    }

    @Test
    void getMonthlySummary_WithInvalidMonth_ShouldThrowException() {
        Long userId = 1L;
        Integer year = 2025;
        Integer invalidMonth = 13;

        assertThrows(IllegalArgumentException.class, () -> {
            transactionService.getMonthlySummary(userId, year, invalidMonth);
        });

        verify(transactionRepository, never()).getMonthlySummary(any(), any(), any());
    }

    @Test
    void getMonthlySummary_WithNullUserId_ShouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> {
            transactionService.getMonthlySummary(null, 2025, 6);
        });

        verify(transactionRepository, never()).getMonthlySummary(any(), any(), any());
    }

    @Test
    void getCategorySummary_WithValidUserId_ShouldReturnList() {
        Long userId = 1L;

        List<CategorySummaryDTO> expectedSummaries = Arrays.asList(
                new CategorySummaryDTO(1L, "Groceries", new BigDecimal("-300.00"), 5L),
                new CategorySummaryDTO(2L, "Transport", new BigDecimal("-150.00"), 3L)
        );

        when(transactionRepository.getCategorySummary(userId))
                .thenReturn(expectedSummaries);

        List<CategorySummaryDTO> result = transactionService.getCategorySummary(userId);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Groceries", result.get(0).getCategoryName());
        assertEquals("Transport", result.get(1).getCategoryName());
    }

    @Test
    void getCategorySummary_WithNullUserId_ShouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> {
            transactionService.getCategorySummary(null);
        });

        verify(transactionRepository, never()).getCategorySummary(any());
    }

    @Test
    void getDateRangeSummary_WithValidData_ShouldReturnSummary() {
        Long userId = 1L;
        LocalDate startDate = LocalDate.now().minusDays(30);
        LocalDate endDate = LocalDate.now();

        DateRangeSummaryDTO expectedSummary = new DateRangeSummaryDTO(
                startDate,
                endDate,
                new BigDecimal("2000.00"),
                new BigDecimal("-1500.00"),
                25L
        );

        when(transactionRepository.getDateRangeSummary(userId, startDate, endDate))
                .thenReturn(expectedSummary);

        DateRangeSummaryDTO result = transactionService.getDateRangeSummary(userId, startDate, endDate);

        assertNotNull(result);
        assertEquals(startDate, result.getStartDate());
        assertEquals(endDate, result.getEndDate());
        assertEquals(new BigDecimal("2000.00"), result.getTotalIncome());
        assertEquals(new BigDecimal("-1500.00"), result.getTotalExpenses());
    }

    @Test
    void getDateRangeSummary_WithStartDateAfterEndDate_ShouldThrowException() {
        Long userId = 1L;
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = LocalDate.now().minusDays(10);

        assertThrows(IllegalArgumentException.class, () -> {
            transactionService.getDateRangeSummary(userId, startDate, endDate);
        });

        verify(transactionRepository, never()).getDateRangeSummary(any(), any(), any());
    }
}
