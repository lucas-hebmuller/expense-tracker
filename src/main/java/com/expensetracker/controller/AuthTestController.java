package com.expensetracker.controller;

import com.expensetracker.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth/test")
public class AuthTestController {

    @Autowired
    private UserService userService;

    @PostMapping("/verify-password")
    public ResponseEntity<?> verifyPassword(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String password = request.get("password");

        return userService.getUserByEmail(email)
                .map(user -> {
                    boolean matches = userService.verifyPassword(password, user.getPassword());
                    return ResponseEntity.ok(Map.of(
                            "email", email,
                            "passwordMatches", matches
                    ));
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}