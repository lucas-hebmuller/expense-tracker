package com.expensetracker.service;

import com.expensetracker.model.User;
import com.expensetracker.repository.PasswordResetTokenRepository;
import com.expensetracker.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PasswordResetService {

    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    EmailService emailService;

    public void requestPasswordReset(String email) {

        Optional<User> user = userRepository.findByEmail(email);

        if() {

        }
    }
}
