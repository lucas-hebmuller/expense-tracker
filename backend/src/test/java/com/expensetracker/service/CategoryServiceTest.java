package com.expensetracker.service;

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

    }

    @Test
    void deleteCategory_WithExistingTransactions_ShouldThrowException() {

    }
}
