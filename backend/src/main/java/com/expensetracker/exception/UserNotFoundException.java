package com.expensetracker.exception;

public class UserNotFoundException extends ResourceNotFoundException{

    public UserNotFoundException(Long id) {
        super("User", id);
    }

    public UserNotFoundException(String message) {
        super(message);
    }
}
