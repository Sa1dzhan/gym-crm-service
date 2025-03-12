package com.gymcrm.service.impl;

import com.gymcrm.dao.*;
import com.gymcrm.model.Trainee;
import com.gymcrm.model.Trainer;
import com.gymcrm.model.Training;
import com.gymcrm.model.User;
import com.gymcrm.service.TrainingService;
import com.gymcrm.util.Authentication;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class TrainingServiceImpl implements TrainingService {
    private final TrainingRepository trainingRepository;
    private final TraineeRepository traineeRepository;
    private final TrainerRepository trainerRepository;
    private final UserRepository<User> userRepository;
    private final TrainingTypeRepository trainingTypeRepository;

    @Override
    public Training addTraining(String authUsername, String authPassword, Training training) {
        Authentication.authenticateUser(authUsername, authPassword, userRepository::findByUsername);

        Trainer trainer = trainerRepository.findByUsername(training.getTrainer().getUsername())
                .orElseThrow(() -> new RuntimeException("Trainer not found"));
        Trainee trainee = traineeRepository.findByUsername(training.getTrainee().getUsername())
                .orElseThrow(() -> new RuntimeException("Trainee not found"));

        if (!trainer.getIsActive() || !trainee.getIsActive()) {
            throw new RuntimeException("Cannot add training for inactive user");
        }

        trainingTypeRepository.findById(training.getTrainingType().getId())
                .orElseThrow(() -> new RuntimeException("TrainingType not found"));

        Training saved = trainingRepository.save(training);
        log.info("Added Training with ID={}, name={}", saved.getId(), saved.getTrainingName());
        return saved;
    }

    @Override
    public Training getTraining(Long id) {
        return trainingRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("No training found"));
    }

    @Override
    public List<Training> getAllTrainings() {
        return trainingRepository.findAll();
    }

    @Override
    public List<Training> getTraineeTrainings(String authUsername, String authPassword,
                                              @NonNull String traineeUsername,
                                              @NonNull Date fromDate, @NonNull Date toDate,
                                              @NonNull String trainerName, @NonNull String trainingType) {
        Authentication.authenticateUser(authUsername, authPassword, userRepository::findByUsername);

        return trainingRepository.findTrainingsForTrainee(
                traineeUsername, fromDate, toDate, trainerName, trainingType);
    }

    @Override
    public List<Training> getTrainerTrainings(String authUsername, String authPassword,
                                              @NonNull String trainerUsername,
                                              @NonNull Date fromDate, @NonNull Date toDate,
                                              @NonNull String traineeName) {
        Authentication.authenticateUser(authUsername, authPassword, userRepository::findByUsername);

        return trainingRepository.findTrainingsForTrainer(
                trainerUsername, fromDate, toDate, traineeName);
    }
}
