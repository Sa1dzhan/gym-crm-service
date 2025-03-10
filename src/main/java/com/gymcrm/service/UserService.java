package com.gymcrm.service;

public interface UserService {
    void changePassword(String username, String oldPassword, String newPassword);

    void toggleActive(String username, String password);
}
