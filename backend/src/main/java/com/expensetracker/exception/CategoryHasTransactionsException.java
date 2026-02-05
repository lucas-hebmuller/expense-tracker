package com.expensetracker.exception;

public class CategoryHasTransactionsException extends RuntimeException{

    public CategoryHasTransactionsException(Long categoryId, long transactionCount) {
        super();
    }
}
