package com.expensetracker.exception;

public class UnauthorizedCategoryAccessException extends RuntimeException {

    public UnauthorizedCategoryAccessException(Long categoryId, Long userId) {
        super("Category with id " + categoryId + " does not belong to user with id " + userId);
    }

    public UnauthorizedCategoryAccessException(String message) {
        super(message);
    }
}
