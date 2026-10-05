package com.expensetracker.exception;

public class InvalidVerificationTokenException extends RuntimeException {

    public InvalidVerificationTokenException() {
        super("Invalid or expired verification token.");
    }

    public InvalidVerificationTokenException(String message) {
        super(message);
    }
}
