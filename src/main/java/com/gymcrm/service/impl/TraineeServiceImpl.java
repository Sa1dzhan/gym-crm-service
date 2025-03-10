package com.gymcrm.service.impl;

import com.gymcrm.dao.TraineeRepository;
import com.gymcrm.dao.TrainerRepository;
import com.gymcrm.dao.UserRepository;
import com.gymcrm.model.Trainee;
import com.gymcrm.model.Trainer;
import com.gymcrm.model.User;
import com.gymcrm.service.TraineeService;
import com.gymcrm.service.UserService;
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
    private final UserRepository userRepository;

    private final UserService userService;

    @Override
    public Trainee createTrainee(Trainee trainee) {
        User user = trainee.getUser();

        UserCredentialGenerator.generateUserCredentials(user, userRepository::existsByUsername);
        trainee.setUser(user);

        Trainee savedTrainee = traineeRepository.save(trainee);
        log.info("Created Trainee with ID={}, username={}", savedTrainee.getId(), savedTrainee.getUser().getUsername());

        return savedTrainee;
    }

    @Override
    public Trainee updateTrainee(Trainee trainee) {
        Authentication.authenticateUser(trainee.getUser().getUsername(), trainee.getUser().getPassword(), userRepository::findByUsername);

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
        return traineeRepository.findByUserUsername(username).orElseThrow(() -> new IllegalArgumentException("No trainee with username " + username + " found"));
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
    public void deleteTraineeById(Trainee trainee) {
        Authentication.authenticateUser(trainee.getUser().getUsername(), trainee.getUser().getPassword(), userRepository::findByUsername);

        traineeRepository.deleteById(trainee.getId());
        log.warn("Deleted Trainee with username = {}", trainee.getUser().getUsername());
    }

    @Override
    public void deleteTraineeByUsername(String username, String password) {
        Authentication.authenticateUser(username, password, userRepository::findByUsername);

        Trainee trainee = traineeRepository.findByUserUsername(username)
                .orElseThrow(() -> new RuntimeException("Trainee not found"));
        traineeRepository.delete(trainee);
        log.warn("Deleted Trainee with username={}", username);
    }

    @Override
    public List<Trainer> getTrainersNotAssigned(String username, String password) {
        Authentication.authenticateUser(username, password, userRepository::findByUsername);

        Trainee trainee = traineeRepository.findByUserUsername(username)
                .orElseThrow(() -> new RuntimeException("Trainee not found"));

        List<Trainer> allTrainers = trainerRepository.findAll();
        allTrainers.removeAll(trainee.getTrainers());

        return allTrainers;
    }

    @Override
    public void updateTrainersList(String username, String password, Set<Long> trainerIds) {
        Authentication.authenticateUser(username, password, userRepository::findByUsername);

        Trainee trainee = traineeRepository.findByUserUsername(username)
                .orElseThrow(() -> new RuntimeException("Trainee not found"));

        Set<Trainer> newTrainers = new HashSet<>(trainerRepository.findAllById(trainerIds));
        trainee.setTrainers(newTrainers);
        traineeRepository.save(trainee);
        log.info("Updated trainers for Trainee username={}", username);
    }
}
