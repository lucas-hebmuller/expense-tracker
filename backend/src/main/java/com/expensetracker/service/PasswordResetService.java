package com.expensetracker.service;

import com.expensetracker.exception.InvalidResetTokenException;
import com.expensetracker.model.PasswordResetToken;
import com.expensetracker.model.User;
import com.expensetracker.repository.PasswordResetTokenRepository;
import com.expensetracker.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${app.frontend.base-url}")
    private String frontendBaseUrl;

    public void requestPasswordReset(String email) {

        Optional<User> userOptional = userRepository.findByEmail(email);

        if(userOptional.isEmpty()) {
            return;
        }

        User user = userOptional.get();

        String plainTextToken = tokenService.generateToken();
        String hashedToken = tokenService.hashToken(plainTextToken);

        PasswordResetToken resetToken = new PasswordResetToken(
                hashedToken,
                user,
                LocalDateTime.now().plusHours(1)
        );
        passwordResetTokenRepository.save(resetToken);

        String resetLink = frontendBaseUrl + "/reset-password?token=" + plainTextToken;

        emailService.sendEmail(
                user.getEmail(),
                "Reset your password",
                "We received a request to reset your password.\n\n"
                        + "Click the link below to choose a new one:\n"
                        + resetLink + "\n\n"
                        + "This link expires in 1 hour. "
                        + "If you didn't request this, you can safely ignore this email."
        );
    }

    @Transactional
    public void resetPassword(String token, String newPassword) {
        String hashedToken = tokenService.hashToken(token);

        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(hashedToken)
                .orElseThrow(() -> new InvalidResetTokenException());

        if (resetToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new InvalidResetTokenException();
        }

        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        passwordResetTokenRepository.delete(resetToken);
    }
}
