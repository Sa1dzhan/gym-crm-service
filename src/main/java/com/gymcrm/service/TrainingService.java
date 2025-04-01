package com.gymcrm.service;

import com.gymcrm.dto.trainee.AddTrainingRequestDto;
import com.gymcrm.dto.training.TraineeTrainingsListRequestDto;
import com.gymcrm.dto.training.TraineeTrainingsListResponseDto;
import com.gymcrm.dto.training.TrainerTrainingsListRequestDto;
import com.gymcrm.dto.training.TrainerTrainingsListResponseDto;

import java.util.List;

public interface TrainingService {
    void addTraining(AddTrainingRequestDto request);

    List<TraineeTrainingsListResponseDto> getTraineeTrainings(TraineeTrainingsListRequestDto dto);

    List<TrainerTrainingsListResponseDto> getTrainerTrainings(TrainerTrainingsListRequestDto dto);
}
