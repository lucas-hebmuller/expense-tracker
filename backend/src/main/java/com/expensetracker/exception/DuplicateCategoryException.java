package com.expensetracker.exception;

public class DuplicateCategoryException extends RuntimeException {

    public DuplicateCategoryException(String categoryName, Long userId) {
        super("Category '" + categoryName + "' already exists for user with id: " + userId);
    }
}
