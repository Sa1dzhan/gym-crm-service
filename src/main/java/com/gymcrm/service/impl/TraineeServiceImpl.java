package com.gymcrm.service.impl;

import com.gymcrm.dao.TraineeRepository;
import com.gymcrm.dao.TrainerRepository;
import com.gymcrm.model.Trainee;
import com.gymcrm.model.Trainer;
import com.gymcrm.service.TraineeService;
import com.gymcrm.util.Authentication;
import com.gymcrm.util.UserCredentialGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class TraineeServiceImpl implements TraineeService {
    private final TraineeRepository traineeRepository;
    private final TrainerRepository trainerRepository;

    @Override
    public Trainee createTrainee(Trainee trainee) {
        UserCredentialGenerator.generateUserCredentials(trainee, traineeRepository::existsByUsername);

        Trainee savedTrainee = traineeRepository.save(trainee);
        log.info("Created Trainee with ID={}, username={}", savedTrainee.getId(), savedTrainee.getUsername());

        return savedTrainee;
    }

    @Override
    public Trainee updateTrainee(Trainee trainee) {
        Authentication.authenticateUser(trainee.getUsername(), trainee.getPassword(), traineeRepository::findByUsername);

        Trainee savedTrainee = traineeRepository.save(trainee);
        log.info("Updated {}", savedTrainee);
        return trainee;
    }

    @Override
    public Trainee getTrainee(Long id) {
        return traineeRepository.findById(id).orElseThrow(() -> new RuntimeException("No such trainee found"));
    }

    @Override
    public Trainee getByUsername(String username) {
        return traineeRepository.findByUsername(username).orElseThrow(() -> new IllegalArgumentException("No trainee with username " + username + " found"));
    }

    @Override
    public void changePassword(String username, String oldPassword, String newPassword) {
        Trainee trainee = Authentication.authenticateUser(username, oldPassword, traineeRepository::findByUsername);
        UserCredentialGenerator.checkNewPassword(newPassword);

        trainee.setPassword(newPassword);
        traineeRepository.save(trainee);

        log.info("Update password for {}", username);
    }

    @Override
    public void toggleActive(String username, String password) {
        Trainee trainee = Authentication.authenticateUser(username, password, traineeRepository::findByUsername);

        Boolean current = trainee.getIsActive();
        trainee.setIsActive(!current);
        traineeRepository.save(trainee);

        log.info("Username = {} toggled from {} to {}",
                username, current, !current);
    }

    @Override
    public void deleteTraineeById(Trainee trainee) {
        Authentication.authenticateUser(trainee.getUsername(), trainee.getPassword(), traineeRepository::findByUsername);

        traineeRepository.delete(trainee);
        log.warn("Deleted Trainee with username = {}", trainee.getUsername());
    }

    @Override
    public void deleteTraineeByUsername(String username, String password) {
        Authentication.authenticateUser(username, password, traineeRepository::findByUsername);

        Trainee trainee = traineeRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Trainee not found"));
        traineeRepository.delete(trainee);
        log.warn("Deleted Trainee with username={}", username);
    }

    @Override
    public List<Trainer> getTrainersNotAssigned(String username, String password) {
        Authentication.authenticateUser(username, password, traineeRepository::findByUsername);

        Trainee trainee = traineeRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Trainee not found"));

        List<Trainer> allTrainers = trainerRepository.findAll();
        allTrainers.removeAll(trainee.getTrainers());

        return allTrainers;
    }

    @Override
    public void updateTrainersList(String username, String password, List<Long> trainerIds) {
        Trainee trainee = Authentication.authenticateUser(username, password, traineeRepository::findByUsername);

        Set<Trainer> newTrainers = new HashSet<>(trainerRepository.findAllById(trainerIds));
        trainee.setTrainers(newTrainers);
        traineeRepository.save(trainee);
        log.info("Updated trainers for Trainee username={}", username);
    }
}
