package com.gymcrm.dao;

import com.gymcrm.model.Training;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface TrainingRepository {
    Optional<Training> findById(Long id);

    List<Training> findAll();

    Training save(Training entity);

    List<Training> findTrainingsForTrainee(String traineeUsername, Date fromDate, Date toDate, String trainerName, String trainingType);

    List<Training> findTrainingsForTrainer(String trainerUsername, Date fromDate, Date toDate, String traineeName);
}
