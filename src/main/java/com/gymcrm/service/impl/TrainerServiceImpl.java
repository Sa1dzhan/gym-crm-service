package com.gymcrm.service.impl;

import com.gymcrm.dao.TrainerRepository;
import com.gymcrm.model.Trainer;
import com.gymcrm.service.TrainerService;
import com.gymcrm.util.Authentication;
import com.gymcrm.util.UserCredentialGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class TrainerServiceImpl implements TrainerService {

    private final TrainerRepository trainerRepository;

    @Override
    public Trainer createTrainer(Trainer trainer) {
        UserCredentialGenerator.generateUserCredentials(trainer, trainerRepository::existsByUsername);

        Trainer savedTrainee = trainerRepository.save(trainer);
        log.info("Created Trainer with ID={}, username={}", savedTrainee.getId(), savedTrainee.getUsername());

        return savedTrainee;
    }

    @Override
    public Trainer updateTrainer(Trainer trainer) {
        Authentication.authenticateUser(trainer.getUsername(), trainer.getPassword(), trainerRepository::findByUsername);

        Trainer savedTrainer = trainerRepository.save(trainer);
        log.info("Updated {}", savedTrainer);
        return savedTrainer;
    }

    @Override
    public Trainer getTrainer(Long id) {
        return trainerRepository.findById(id).orElseThrow(() -> new RuntimeException("No trainer found"));
    }

    @Override
    public Trainer getByUsername(String username) {
        return trainerRepository.findByUsername(username).orElseThrow(() -> new IllegalArgumentException("No trainee with username " + username + " found"));
    }

    @Override
    public void changePassword(String username, String oldPassword, String newPassword) {
        Trainer trainer = Authentication.authenticateUser(username, oldPassword, trainerRepository::findByUsername);
        UserCredentialGenerator.checkNewPassword(newPassword);

        trainer.setPassword(newPassword);
        trainerRepository.save(trainer);

        log.info("Update password for {}", username);
    }

    @Override
    public void toggleActive(String username, String password) {
        Trainer trainer = Authentication.authenticateUser(username, password, trainerRepository::findByUsername);

        Boolean current = trainer.getIsActive();
        trainer.setIsActive(!current);
        trainerRepository.save(trainer);

        log.info("Username = {} toggled from {} to {}",
                username, current, !current);
    }


    @Override
    public List<Trainer> getAllTrainers() {
        return trainerRepository.findAll();
    }
}
