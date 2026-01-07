package com.expensetracker.controller;

import com.expensetracker.exception.CategoryNotFoundException;
import com.expensetracker.model.Category;
import com.expensetracker.security.SecurityUtil;
import com.expensetracker.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping
    public ResponseEntity<List<Category>> getMyCategories() {
        Long userId = SecurityUtil.getCurrentUserId();
        List<Category> categories = categoryService.getCategoriesByUserId(userId);
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Category> getCategoryById(@PathVariable Long id) {
        Long userId = SecurityUtil.getCurrentUserId();
        Category category = categoryService.getCategoryById(id)
                .orElseThrow(() -> new CategoryNotFoundException(id));

        if (!category.getUser().getId().equals(userId)) {
            throw new CategoryNotFoundException(id);
        }

        return ResponseEntity.ok(category);
    }

    @PostMapping
    public ResponseEntity<Category> createCategory(@Valid @RequestBody Category category) {
        Category createdCategory = categoryService.createCategory(category);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdCategory);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Category> updateCategory(
            @PathVariable Long id,
            @Valid @RequestBody Category categoryDetails) {
        Long userId = SecurityUtil.getCurrentUserId();
        Category updatedCategory = categoryService.updateCategory(id, categoryDetails, userId);
        return ResponseEntity.ok(updatedCategory);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        Long userId = SecurityUtil.getCurrentUserId();
        categoryService.deleteCategory(id, userId);
        return ResponseEntity.noContent().build();
    }
}
