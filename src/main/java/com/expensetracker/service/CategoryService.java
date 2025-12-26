package com.expensetracker.service;

import com.expensetracker.model.Category;
import com.expensetracker.repository.CategoryRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public List<Category> getCategoriesByUserId(Long userId) {
        return categoryRepository.findByUser_Id(userId);
    }

    public Optional<Category> getCategoryById(Long id) {
        return categoryRepository.findById(id);
    }

    @Transactional
    public Category createCategory(Category category) {
        if (category.getUser() == null || category.getUser().getId() == null) {
            throw new RuntimeException("Category must belong to a user!");
        }

        if (categoryRepository.findByNameAndUser_Id(
                category.getName(),
                category.getUser().getId()).isPresent()) {
            throw new RuntimeException("Category already exists for this user!");
        }

        return categoryRepository.save(category);
    }

    @Transactional
    public Category updateCategory(Long id, Category categoryDetails) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("Category not found with id: " + id));

        category.setName(categoryDetails.getName());
        category.setUser(categoryDetails.getUser());

        return categoryRepository.save(category);
    }

    @Transactional
    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("Category not found with id: " + id));
        categoryRepository.delete(category);
    }
}
