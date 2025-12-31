package com.expensetracker.exception;

public class CategoryNotFoundException extends ResourceNotFoundException{

    public CategoryNotFoundException(Long id) {
        super("Category", id);
    }

    public CategoryNotFoundException(String message) {
        super(message);
    }
}
