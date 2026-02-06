package com.expensetracker.exception;

public class CategoryHasTransactionsException extends RuntimeException{

    public CategoryHasTransactionsException(String categoryName, long transactionCount) {
        super("Cannot delete category '" + categoryName + "' because it has " +
                transactionCount + " transaction(s). Please delete or reassign the transaction(s) first. ");
    }
}
