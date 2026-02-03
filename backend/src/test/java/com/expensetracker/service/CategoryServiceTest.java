package com.expensetracker.service;

import com.expensetracker.repository.CategoryRepository;
import com.expensetracker.repository.TransactionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private CategoryService categoryService;

    @Test
    void createCategory_WithValidData_ShouldReturnSavedCategory() {

    }

    @Test
    void createCategory_WithDuplicateName_ShouldThrowException() {

    }

    @Test
    void deleteCategory_WithExistingTransactions_ShouldThrowException() {

    }
}
