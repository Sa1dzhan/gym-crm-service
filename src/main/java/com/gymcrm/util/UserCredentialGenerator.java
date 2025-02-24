package com.gymcrm.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

public class UserCredentialGenerator {

    private static final Logger logger = LoggerFactory.getLogger(UserCredentialGenerator.class);

    private static final String CHAR_POOL = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!$_#-*";
    private static final int DEFAULT_PASSWORD_LENGTH = 10;
    private static final Random RANDOM = new Random();

    private UserCredentialGenerator() {
    }

    public static String generateUniqueUsername(String firstName, String lastName, Set<String> existingUsernames) {
        String base = firstName + "." + lastName;
        String username = base;
        int suffix = 1;
        Set<String> lowerCaseUsernames = new HashSet<>();

        // to lower case
        if (!existingUsernames.isEmpty()) {
            lowerCaseUsernames = existingUsernames.stream()
                    .filter(Objects::nonNull)
                    .map(String::toLowerCase)
                    .collect(Collectors.toSet());
        }

        while (lowerCaseUsernames.contains(username.toLowerCase())) {
            username = base + suffix;
            suffix++;
        }
        logger.debug("Generated unique username: {}", username);
        return username;
    }

    public static String generateRandomPassword() {
        StringBuilder sb = new StringBuilder(DEFAULT_PASSWORD_LENGTH);
        for (int i = 0; i < DEFAULT_PASSWORD_LENGTH; i++) {
            sb.append(CHAR_POOL.charAt(RANDOM.nextInt(CHAR_POOL.length())));
        }
        return sb.toString();
    }
}
