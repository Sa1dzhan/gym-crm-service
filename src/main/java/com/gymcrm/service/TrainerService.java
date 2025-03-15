package com.gymcrm.service;

import com.gymcrm.model.Trainer;

import java.util.List;

public interface TrainerService {
    Trainer createTrainer(Trainer trainer);
    Trainer updateTrainer(Trainer trainer);
    Trainer getTrainer(Long id);

    Trainer getByUsername(String username);

    void changePassword(String username, String oldPassword, String newPassword);

    void toggleActive(String username, String password);
    List<Trainer> getAllTrainers();
}
