package com.gymcrm.service;

import com.gymcrm.model.Training;

import java.util.List;

public interface TrainingService {
    Training createTraining(Training training);
    Training getTraining(Long id);
    List<Training> getAllTrainings();
}
