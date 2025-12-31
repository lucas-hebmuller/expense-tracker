package com.expensetracker.exception;

public class TransactionNotFoundException extends ResourceNotFoundException {

    public TransactionNotFoundException(Long id) {
        super("Transaction", id);
    }

    public TransactionNotFoundException(String message) {
        super(message);
    }
}
