package com.gymcrm.util;

import com.gymcrm.model.User;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Optional;
import java.util.function.Function;

public class Authentication {

    public static <T extends User> T authenticateUser(String username, String password, Function<String, Optional<T>> findByUsername) {
        return findByUsername.apply(username)
                .filter(u -> u.getPassword().equals(password))
                .orElseThrow(() -> new IllegalArgumentException("Invalid username or password"));
    }

    public static String getCurrentAuthToken() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        String authHeader = attributes.getRequest().getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader;
        }

        return null;
    }
}
