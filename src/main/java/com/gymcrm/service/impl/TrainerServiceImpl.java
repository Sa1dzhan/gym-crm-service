package com.gymcrm.service.impl;

import com.gymcrm.dao.TrainerRepository;
import com.gymcrm.dao.UserRepository;
import com.gymcrm.model.Trainer;
import com.gymcrm.model.User;
import com.gymcrm.service.TrainerService;
import com.gymcrm.service.UserService;
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
    private final UserRepository userRepository;

    private final UserService userService;

    @Override
    public Trainer createTrainer(Trainer trainer) {
        User user = trainer.getUser();

        UserCredentialGenerator.generateUserCredentials(user, userRepository::existsByUsername);
        trainer.setUser(user);

        Trainer savedTrainee = trainerRepository.save(trainer);
        log.info("Created Trainer with ID={}, username={}", savedTrainee.getId(), savedTrainee.getUser().getUsername());

        return savedTrainee;
    }

    @Override
    public Trainer updateTrainer(Trainer trainer) {
        Authentication.authenticateUser(trainer.getUser().getUsername(), trainer.getUser().getPassword(), userRepository::findByUsername);

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
        return trainerRepository.findByUserUsername(username).orElseThrow(() -> new IllegalArgumentException("No trainee with username " + username + " found"));
    }

    @Override
    public void changePassword(String username, String oldPassword, String newPassword) {
        userService.changePassword(username, oldPassword, newPassword);
    }

    @Override
    public void toggleActive(String username, String password) {
        userService.toggleActive(username, password);
    }


    @Override
    public List<Trainer> getAllTrainers() {
        return trainerRepository.findAll();
    }
}
