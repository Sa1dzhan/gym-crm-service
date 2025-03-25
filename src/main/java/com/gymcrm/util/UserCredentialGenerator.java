package com.gymcrm.util;

import com.gymcrm.model.User;

import java.util.Random;
import java.util.function.Predicate;

public class UserCredentialGenerator {
    private static final String CHAR_POOL = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!$_#-*";
    private static final int DEFAULT_PASSWORD_LENGTH = 10;
    private static final Random RANDOM = new Random();

    public static <T extends User> void generateUserCredentials(T user, Predicate<String> existsByUsername) {
        if (user.getFirstName() == null || user.getLastName() == null) {
            throw new IllegalArgumentException("Required fields missing");
        }

        // generate username & password
        String username = generateUsername(
                user.getFirstName(),
                user.getLastName(),
                existsByUsername
        );
        String password = generateRandomPassword();

        user.setUsername(username);
        user.setPassword(password);
    }

    private static String generateUsername(String firstName, String lastName, Predicate<String> existsByUsername) {
        int suffix = 1;
        String candidate = firstName + "." + lastName;
        String base = candidate;

        while (existsByUsername.test(candidate)) {
            candidate = base + suffix;
            suffix++;
        }

        return candidate;
    }

    private static String generateRandomPassword() {
        StringBuilder sb = new StringBuilder(DEFAULT_PASSWORD_LENGTH);
        for (int i = 0; i < DEFAULT_PASSWORD_LENGTH; i++) {
            sb.append(CHAR_POOL.charAt(RANDOM.nextInt(CHAR_POOL.length())));
        }
        return sb.toString();
    }

    public static void checkNewPassword(String newPassword) {
        if (newPassword.length() < 10) {
            throw new RuntimeException("Password cannot be shorter than 10 characters");
        }
    }
}
