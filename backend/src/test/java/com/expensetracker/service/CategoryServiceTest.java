package com.expensetracker.service;

import com.expensetracker.exception.CategoryHasTransactionsException;
import com.expensetracker.exception.CategoryNotFoundException;
import com.expensetracker.exception.DuplicateCategoryException;
import com.expensetracker.model.Category;
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

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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
        // === ARRANGE ===
        //Set up test data
        Long userId = 1l;
        String categoryName = "Groceries";

        // The category being passed in (no ID yet, no user yet)
        Category inputCategory = new Category();
        inputCategory.setName(categoryName);

        // What the "DB" returns after saving
        Category savedCategory = new Category();
        savedCategory.setId(1L);
        savedCategory.setName(categoryName);
        User user = new User();
        user.setId(userId);
        savedCategory.setUser(user);

        // Mock the static SecurityUtil call
        try (MockedStatic<SecurityUtil> securityUtil = mockStatic(SecurityUtil.class)) {
            securityUtil.when(SecurityUtil::getCurrentUserId).thenReturn(userId);

            // No duplicate exists
            when(categoryRepository.findByNameAndUser_Id(categoryName, userId))
                    .thenReturn(Optional.empty());

            // Save returns the saved category
            when(categoryRepository.save(any(Category.class)))
                    .thenReturn(savedCategory);

            // ===== ACT =====
            Category result = categoryService.createCategory(inputCategory);

            // ===== ASSERT =====
            assertNotNull(result);
            assertEquals(1L, result.getId());
            assertEquals("Groceries", result.getName());
            assertEquals(userId, result.getUser().getId());

            // Verify interactions
            verify(categoryRepository).findByNameAndUser_Id(categoryName, userId);
            verify(categoryRepository).save(any(Category.class));
        }
    }

    @Test
    void createCategory_WithDuplicateName_ShouldThrowException() {
        // === ARRANGE ===
        Long userId = 1L;
        String categoryName = "Groceries";

        Category inputCategory = new Category();
        inputCategory.setName(categoryName);

        // The existing category already in "DB"
        User user = new User();
        user.setId(userId);

        Category existingCategory = new Category();
        existingCategory.setId(1L);
        existingCategory.setName(categoryName);
        existingCategory.setUser(user);

        try (MockedStatic<SecurityUtil> securityUtil = mockStatic(SecurityUtil.class)) {
            securityUtil.when(SecurityUtil::getCurrentUserId).thenReturn(userId);

            when(categoryRepository.findByNameAndUser_Id(categoryName, userId))
                    .thenReturn(Optional.of(existingCategory));

            // ===== ACT & ASSERT =====
            assertThrows(DuplicateCategoryException.class, () -> {
                categoryService.createCategory(inputCategory);
            });

            // Verify save was never called
            verify(categoryRepository, never()).save(any(Category.class));
        }
    }

    @Test
    void deleteCategory_WithExistingTransactions_ShouldThrowException() {
        // === ARRANGE ===
        Long userId = 1L;
        Long categoryId = 1L;
        String categoryName = "Groceries";

        // The existing category with transaction(s) in "DB"
        User user = new User();
        user.setId(userId);

        Category existingCategory = new Category();
        existingCategory.setId(categoryId);
        existingCategory.setName(categoryName);
        existingCategory.setUser(user);

        when(categoryRepository.findById(categoryId))
                .thenReturn(Optional.of(existingCategory));

        when(transactionRepository.countByCategory_Id(categoryId))
                .thenReturn(5L);

        // === ACT & ASSERT ===
        CategoryHasTransactionsException exception = assertThrows(
                CategoryHasTransactionsException.class,
                () -> categoryService.deleteCategory(categoryId, userId)
        );

        // Verify the message contains useful info
        assertTrue(exception.getMessage().contains("Groceries"));
        assertTrue(exception.getMessage().contains("5"));

        verify(categoryRepository, never()).delete(any(Category.class));
    }

    @Test
    void deleteCategory_WithNoTransactions_ShouldSucceed() {
        Long userId = 1L;
        Long categoryId = 1L;
        String categoryName = "Groceries";

        User user = new User();
        user.setId(userId);

        Category existingCategory = new Category();
        existingCategory.setId(categoryId);
        existingCategory.setName(categoryName);
        existingCategory.setUser(user);

        when(categoryRepository.findById(categoryId))
                .thenReturn(Optional.of(existingCategory));

        when(transactionRepository.countByCategory_Id(categoryId))
                .thenReturn(0L);

        // ===== ACT =====
        categoryService.deleteCategory(categoryId, userId);

        // === ASSERT ===
        verify(transactionRepository).countByCategory_Id(categoryId);
        verify(categoryRepository).delete(existingCategory);
    }

    @Test
    void deleteCategory_WithWrongUser_ShouldThrowException() {
        // === ARRANGE ===
        Long ownUserId = 1L;
        Long wrongUserId = 2L;
        Long categoryId = 1L;
        String categoryName = "Groceries";

        User user = new User();
        user.setId(ownUserId);

        Category existingCategory = new Category();
        existingCategory.setId(categoryId);
        existingCategory.setName(categoryName);
        existingCategory.setUser(user);

        when(categoryRepository.findById(categoryId))
                .thenReturn(Optional.of(existingCategory));

        // ===== ACT & ASSERT =====
        assertThrows(
            CategoryNotFoundException.class,
            () -> categoryService.deleteCategory(categoryId, wrongUserId)
        );

        verify(transactionRepository, never()).countByCategory_Id(any());
        verify(categoryRepository, never()).delete(any(Category.class));
    }
}
