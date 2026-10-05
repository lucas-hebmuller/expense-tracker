package com.expensetracker.controller;

import com.expensetracker.dto.*;
import com.expensetracker.exception.EmailNotVerifiedException;
import com.expensetracker.exception.InvalidCredentialsException;
import com.expensetracker.model.User;
import com.expensetracker.security.JwtUtil;
import com.expensetracker.service.EmailVerificationService;
import com.expensetracker.service.PasswordResetService;
import com.expensetracker.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordResetService passwordResetService;

    @Autowired
    private EmailVerificationService emailVerificationService;

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(@Valid @RequestBody RegisterRequest request) {
        User user = new User(request.getName(), request.getEmail(), request.getPassword());
        userService.createUser(user);

        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message",
                "Registration successful. Please check your email to verify your account."));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {

        User user = userService.getUserByEmail(request.getEmail())
                .orElseThrow(() -> new InvalidCredentialsException());

        if (!userService.verifyPassword(request.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException();
        }

        if(!user.isEmailVerified()) {
            throw new EmailNotVerifiedException();
        }

        String token = jwtUtil.generateToken(user.getId(), user.getEmail());

        AuthResponse response = new AuthResponse(
                token,
                user.getId(),
                user.getEmail(),
                user.getName()
        );

        return ResponseEntity.ok(response);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<Map<String, String>> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequest request) {

        passwordResetService.requestPasswordReset(request.getEmail());

        return ResponseEntity.ok(Map.of("message",
                "If an account exists for that email, a reset link has been sent."));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Map<String, String>> resetPassword(
            @Valid @RequestBody ResetPasswordRequest request) {

        passwordResetService.resetPassword(request.getToken(), request.getNewPassword());

        return ResponseEntity.ok(Map.of("message",
                "Your password has been reset successfully."));
    }

    @PostMapping("/verify-email")
    public ResponseEntity<Map<String, String>> verifyEmail(
            @Valid @RequestBody VerifyEmailRequest request) {

        emailVerificationService.verifyEmail(request.getToken());

        return ResponseEntity.ok(Map.of("message",
                "Your email has been verified. You can now log in."));
    }

    @PostMapping("/resend-verification")
    public ResponseEntity<Map<String, String>> resendVerification(
            @Valid @RequestBody ForgotPasswordRequest request) {

        emailVerificationService.resendVerification(request.getEmail());

        return ResponseEntity.ok(Map.of("message",
                "If an unverified account exists for that email, a verification link has been sent."));
    }
}
