package com.gymcrm.service;

import com.gymcrm.model.Training;

import java.util.Date;
import java.util.List;

public interface TrainingService {
    Training addTraining(String authUsername, String authPassword, Training training);
    Training getTraining(Long id);
    List<Training> getAllTrainings();

    List<Training> getTraineeTrainings(String authUsername, String authPassword,
                                       String traineeUsername,
                                       Date fromDate, Date toDate,
                                       String trainerName, String trainingType);

    List<Training> getTrainerTrainings(String authUsername, String authPassword,
                                       String trainerUsername,
                                       Date fromDate, Date toDate,
                                       String traineeName);
}
