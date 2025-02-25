package com.gymcrm.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;

public class UserCredentialGenerator {

    private static final Logger logger = LoggerFactory.getLogger(UserCredentialGenerator.class);

    private static final String CHAR_POOL = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!$_#-*";
    private static final int DEFAULT_PASSWORD_LENGTH = 10;
    private static final Random RANDOM = new Random();
    private static long traineeUserId = 1;
    private static long trainerUserId = 1;

    private UserCredentialGenerator() {
    }

    public static Long generateTraineeUserId() {
        return traineeUserId++;
    }

    public static Long generateTrainerUserId() {
        return trainerUserId++;
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
