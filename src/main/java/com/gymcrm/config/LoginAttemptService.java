package com.gymcrm.config;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class LoginAttemptService {
    private final int MAX_ATTEMPT = 3;
    private final long BLOCK_MINUTES = 5;
    private final Map<String, Integer> attempts = new ConcurrentHashMap<>();
    private final Map<String, LocalDateTime> blocked = new ConcurrentHashMap<>();

    public void loginSucceeded(String username) {
        attempts.remove(username);
        blocked.remove(username);
    }

    public void loginFailed(String username) {
        int attempt = attempts.getOrDefault(username, 0) + 1;
        attempts.put(username, attempt);
        if (attempt >= MAX_ATTEMPT) {
            blocked.put(username, LocalDateTime.now().plusMinutes(BLOCK_MINUTES));
        }
    }

    public boolean isBlocked(String username) {
        LocalDateTime blockedUntil = blocked.get(username);
        if (blockedUntil == null) return false;
        if (LocalDateTime.now().isAfter(blockedUntil)) {
            blocked.remove(username);
            attempts.remove(username);
            return false;
        }
        return true;
    }
}
