package com.gymcrm.util;

import com.gymcrm.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;
import java.util.function.Predicate;

public class UserCredentialGenerator {

    private static final Logger logger = LoggerFactory.getLogger(UserCredentialGenerator.class);

    private static final String CHAR_POOL = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!$_#-*";
    private static final int DEFAULT_PASSWORD_LENGTH = 10;
    private static final Random RANDOM = new Random();

    private UserCredentialGenerator() {
    }

    public static <T extends User> void generateUserCredentials(T user, Predicate<String> existsByUsername) {
        int suffix = 1;
        String candidate = generateUsername(user.getFirstName(), user.getLastName());
        String base = candidate;

        while (existsByUsername.test(candidate)) {
            candidate = base + suffix;
            suffix++;
        }

        user.setUsername(candidate);
        user.setPassword(generateRandomPassword());
    }

    public static String generateUsername(String firstName, String lastName) {
        return firstName + "." + lastName;
    }

    public static String generateRandomPassword() {
        StringBuilder sb = new StringBuilder(DEFAULT_PASSWORD_LENGTH);
        for (int i = 0; i < DEFAULT_PASSWORD_LENGTH; i++) {
            sb.append(CHAR_POOL.charAt(RANDOM.nextInt(CHAR_POOL.length())));
        }
        return sb.toString();
    }
}
