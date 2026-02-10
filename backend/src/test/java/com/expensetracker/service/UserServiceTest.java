package com.expensetracker.service;

import com.expensetracker.exception.DuplicateEmailException;
import com.expensetracker.exception.UserNotFoundException;
import com.expensetracker.model.User;
import com.expensetracker.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    void createUser_WithValidData_ShouldReturnSavedUser() {
        String rawPassword = "password123";
        String encodedPassword = "encodedPassword123";

        User inputUser = new User();
        inputUser.setName("John Doe");
        inputUser.setEmail("john@example.com");
        inputUser.setPassword(rawPassword);

        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setName("John Doe");
        savedUser.setEmail("john@example.com");
        savedUser.setPassword(encodedPassword);

        when(userRepository.findByEmail("john@example.com"))
                .thenReturn(Optional.empty());

        when(passwordEncoder.encode(rawPassword))
                .thenReturn(encodedPassword);

        when(userRepository.save(any(User.class)))
                .thenReturn(savedUser);

        User result = userService.createUser(inputUser);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("John Doe", result.getName());
        assertEquals("john@example.com", result.getEmail());
        assertEquals(encodedPassword, result.getPassword());

        verify(userRepository).findByEmail("john@example.com");
        verify(passwordEncoder).encode(rawPassword);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void createUser_WithDuplicateEmail_ShouldThrowException() {
        User existingUser = new User();
        existingUser.setId(1L);
        existingUser.setEmail("john@example.com");

        User inputUser = new User();
        inputUser.setName("Jane Doe");
        inputUser.setEmail("john@example.com");
        inputUser.setPassword("password123");

        when(userRepository.findByEmail("john@example.com"))
                .thenReturn(Optional.of(existingUser));

        assertThrows(DuplicateEmailException.class, () -> {
            userService.createUser(inputUser);
        });

        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void updateUser_WithValidData_ShouldReturnUpdatedUser() {
        Long userId = 1L;
        String newEncodedPassword = "newEncodedPassword";

        User existingUser = new User();
        existingUser.setId(userId);
        existingUser.setName("John Doe");
        existingUser.setEmail("john@example.com");
        existingUser.setPassword("oldEncodedPassword");

        User updateDetails = new User();
        updateDetails.setName("John Updated");
        updateDetails.setEmail("john.updated@example.com");
        updateDetails.setPassword("newPassword123");

        User updatedUser = new User();
        updatedUser.setId(userId);
        updatedUser.setName("John Updated");
        updatedUser.setEmail("john.updated@example.com");
        updatedUser.setPassword(newEncodedPassword);

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(existingUser));

        when(passwordEncoder.encode("newPassword123"))
                .thenReturn(newEncodedPassword);

        when(userRepository.save(any(User.class)))
                .thenReturn(updatedUser);

        User result = userService.updateUser(userId, updateDetails);

        assertNotNull(result);
        assertEquals("John Updated", result.getName());
        assertEquals("john.updated@example.com", result.getEmail());
        assertEquals(newEncodedPassword, result.getPassword());

        verify(userRepository).findById(userId);
        verify(passwordEncoder).encode("newPassword123");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void updateUser_NotFound_ShouldThrowException() {
        Long userId = 99L;

        User updateDetails = new User();
        updateDetails.setName("Test");

        when(userRepository.findById(userId))
                .thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> {
            userService.updateUser(userId, updateDetails);
        });

        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void deleteUser_WithValidId_ShouldSucceed() {
        Long userId = 1L;

        User existingUser = new User();
        existingUser.setId(userId);
        existingUser.setName("John Doe");

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(existingUser));

        userService.deleteUser(userId);

        verify(userRepository).findById(userId);
        verify(userRepository).delete(existingUser);
    }

    @Test
    void deleteUser_NotFound_ShouldThrowException() {
        Long userId = 99L;

        when(userRepository.findById(userId))
                .thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> {
            userService.deleteUser(userId);
        });

        verify(userRepository, never()).delete(any(User.class));
    }

    @Test
    void verifyPassword_WithCorrectPassword_ShouldReturnTrue() {
        String rawPassword = "password123";
        String encodedPassword = "encodedPassword123";

        when(passwordEncoder.matches(rawPassword, encodedPassword))
                .thenReturn(true);

        boolean result = userService.verifyPassword(rawPassword, encodedPassword);

        assertTrue(result);
        verify(passwordEncoder).matches(rawPassword, encodedPassword);
    }

    @Test
    void verifyPassword_WithWrongPassword_ShouldReturnFalse() {
        String rawPassword = "wrongPassword";
        String encodedPassword = "encodedPassword123";

        when(passwordEncoder.matches(rawPassword, encodedPassword))
                .thenReturn(false);

        boolean result = userService.verifyPassword(rawPassword, encodedPassword);

        assertFalse(result);
        verify(passwordEncoder).matches(rawPassword, encodedPassword);
    }

    @Test
    void getUserById_WithValidId_ShouldReturnUser() {
        Long userId = 1L;

        User user = new User();
        user.setId(userId);
        user.setName("John Doe");
        user.setEmail("john@example.com");

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));

        Optional<User> result = userService.getUserById(userId);

        assertTrue(result.isPresent());
        assertEquals("John Doe", result.get().getName());
    }

    @Test
    void getUserById_NotFound_ShouldReturnEmpty() {
        Long userId = 99L;

        when(userRepository.findById(userId))
                .thenReturn(Optional.empty());

        Optional<User> result = userService.getUserById(userId);

        assertFalse(result.isPresent());
    }

    @Test
    void getUserByEmail_WithValidEmail_ShouldReturnUser() {
        String email = "john@example.com";

        User user = new User();
        user.setId(1L);
        user.setName("John Doe");
        user.setEmail(email);

        when(userRepository.findByEmail(email))
                .thenReturn(Optional.of(user));

        Optional<User> result = userService.getUserByEmail(email);

        assertTrue(result.isPresent());
        assertEquals("John Doe", result.get().getName());
    }
}
