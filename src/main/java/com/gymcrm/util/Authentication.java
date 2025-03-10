package com.gymcrm.util;

import com.gymcrm.model.User;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.function.Function;

@Transactional(readOnly = true)
public class Authentication {

    public static User authenticateUser(String username, String password, Function<String, Optional<User>> findByUsername) {
        return findByUsername.apply(username)
                .filter(u -> u.getPassword().equals(password))
                .orElseThrow(() -> new IllegalArgumentException("Invalid username or password"));
    }
}
