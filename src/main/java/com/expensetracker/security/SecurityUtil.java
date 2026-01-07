package com.expensetracker.security;

import com.expensetracker.model.User;
import com.expensetracker.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SecurityUtil {

    private static UserService userServiceStatic;

    @Autowired
    private UserService userService;

    @Autowired
    public void setUserServiceStatic(UserService userService) {
        SecurityUtil.userServiceStatic = userService;
    }

    public static String getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }

        return authentication.getName();
    }

    public static boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        return authentication != null && authentication.isAuthenticated();
    }

    public static User getCurrentUser() {
        String email = getCurrentUserEmail();

        if (email == null) {
            return null;
        }

        return userServiceStatic.getUserByEmail(email)
                .orElse(null);
    }

    public static Long getCurrentUserId() {
        User user = getCurrentUser();

        return user != null ? user.getId() : null;
    }
}
