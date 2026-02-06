package com.expensetracker.service;

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

        }
    }
}
