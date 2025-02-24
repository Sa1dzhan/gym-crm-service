package com.gymcrm.service;

import com.gymcrm.model.Trainer;

import java.util.List;

public interface TrainerService {
    Trainer createTrainer(Trainer trainer);
    Trainer updateTrainer(Trainer trainer);
    Trainer getTrainer(Long id);
    List<Trainer> getAllTrainers();
}
