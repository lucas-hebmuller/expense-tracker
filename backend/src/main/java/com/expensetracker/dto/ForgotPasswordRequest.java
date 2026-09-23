package com.expensetracker.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class ForgotPasswordRequest {

    @NotBlank(message = "Email cannot be blank!")
    @Email(message = "Email must be valid.")
    private String email;

    public ForgotPasswordRequest() {}

    public ForgotPasswordRequest(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
