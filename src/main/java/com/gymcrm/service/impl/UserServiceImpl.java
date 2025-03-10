package com.gymcrm.service.impl;

import com.gymcrm.dao.UserRepository;
import com.gymcrm.model.User;
import com.gymcrm.service.UserService;
import com.gymcrm.util.Authentication;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public void changePassword(String username, String oldPassword, String newPassword) {
        User user = Authentication.authenticateUser(username, oldPassword, userRepository::findByUsername);
        if (newPassword.length() < 10) {
            throw new IllegalArgumentException("Password cannot be shorter than 10 characters");
        }

        user.setPassword(newPassword);
        userRepository.save(user);
        log.info("Update password for {}", username);
    }

    @Override
    public void toggleActive(String username, String password) {
        User user = Authentication.authenticateUser(username, password, userRepository::findByUsername);

        Boolean current = user.getIsActive();
        user.setIsActive(!current);
        userRepository.save(user);

        log.warn("Username = {} toggled from {} to {}",
                username, current, !current);
    }
}
