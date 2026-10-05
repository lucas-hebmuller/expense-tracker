package com.expensetracker.service;

import com.expensetracker.exception.InvalidVerificationTokenException;
import com.expensetracker.model.User;
import com.expensetracker.model.VerificationToken;
import com.expensetracker.repository.UserRepository;
import com.expensetracker.repository.VerificationTokenRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class EmailVerificationService {

    @Autowired
    private VerificationTokenRepository verificationTokenRepository;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private UserRepository userRepository;

    @Value("${app.frontend.base-url}")
    private String frontendBaseUrl;

    @Transactional
    public void sendVerificationEmail(User user) {

        String plainTextToken = tokenService.generateToken();
        String hashedToken = tokenService.hashToken(plainTextToken);

        VerificationToken verificationToken = new VerificationToken(
                hashedToken,
                user,
                LocalDateTime.now().plusHours(24)
        );
        verificationTokenRepository.save(verificationToken);

        String verificationLink = frontendBaseUrl + "/verify-email?token=" + plainTextToken;

        emailService.sendEmail(
                user.getEmail(),
                "Verify your email",
                "We received a request to verify your email.\n\n"
                        + "Click the link below to verify it:\n"
                        + verificationLink + "\n\n"
                        + "This link expires in 24 hours. "
                        + "If you didn't request this, you can safely ignore this email."
        );
    }

    @Transactional
    public void verifyEmail(String token) {

        String hashedToken = tokenService.hashToken(token);

        VerificationToken verificationToken = verificationTokenRepository.findByToken(hashedToken)
                .orElseThrow(() -> new InvalidVerificationTokenException());

        if (verificationToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new InvalidVerificationTokenException();
        }

        User user = verificationToken.getUser();
        user.setEmailVerified(true);
        userRepository.save(user);

        verificationTokenRepository.delete(verificationToken);
    }

    public void resendVerification(String email) {
        Optional<User> userOptional = userRepository.findByEmail(email);

        if (userOptional.isEmpty() || userOptional.get().isEmailVerified()) {
            return;
        }

        sendVerificationEmail(userOptional.get());
    }
}
