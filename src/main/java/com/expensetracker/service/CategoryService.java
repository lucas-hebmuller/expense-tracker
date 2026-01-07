package com.expensetracker.service;

import com.expensetracker.exception.CategoryNotFoundException;
import com.expensetracker.exception.DuplicateCategoryException;
import com.expensetracker.model.Category;
import com.expensetracker.model.User;
import com.expensetracker.repository.CategoryRepository;
import com.expensetracker.security.SecurityUtil;
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
        Long userId = SecurityUtil.getCurrentUserId();
        User user = new User();
        user.setId(userId);
        category.setUser(user);

        if (categoryRepository.findByNameAndUser_Id(
                category.getName(),
                userId).isPresent()) {
            throw new DuplicateCategoryException(category.getName(), userId);
        }

        return categoryRepository.save(category);
    }

    @Transactional
    public Category updateCategory(Long id, Category categoryDetails, Long userId) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(()-> new CategoryNotFoundException(id));

        if (!category.getUser().getId().equals(userId)) {
            throw new CategoryNotFoundException(id);
        }

        category.setName(categoryDetails.getName());

        return categoryRepository.save(category);
    }

    @Transactional
    public void deleteCategory(Long id, Long userId) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(()-> new CategoryNotFoundException(id));

        if (!category.getUser().getId().equals(userId)) {
            throw new CategoryNotFoundException(id);
        }

        categoryRepository.delete(category);
    }
}
