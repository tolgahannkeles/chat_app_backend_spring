package com.tolgahan.chat_app.validation;

import com.tolgahan.chat_app.request.RegisterRequest;

import java.util.regex.Pattern;

public class RegistrationValidator {
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$"
    );
    private static boolean isUsernameValid(String username) {
        return username != null && username.length() >= 3 && username.length() <= 20;
    }

    private static boolean isPasswordValid(String password) {
        return password != null && password.length() >= 5 && password.length() <= 20;
    }
    private static boolean isEmailValid(String email) {
        if (email == null || email.length() < 5 || email.length() > 50) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email).matches();
    }
    public static boolean isUserValid(RegisterRequest user) {
        return isUsernameValid(user.getUsername()) && isPasswordValid(user.getPassword()) && isEmailValid(user.getEmail());
    }
}
