package com.gymcrm.dao;

import com.gymcrm.model.Training;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TrainingRepository {
    Optional<Training> findById(Long id);

    List<Training> findAll();

    Training save(Training entity);

    List<Training> findTrainingsForTrainee(String traineeUsername, LocalDate fromDate, LocalDate toDate, String trainerName, String trainingType);

    List<Training> findTrainingsForTrainer(String trainerUsername, LocalDate fromDate, LocalDate toDate, String traineeName);
}
