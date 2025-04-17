package com.gymcrm.service.impl;

import com.gymcrm.converter.TrainingMapper;
import com.gymcrm.dao.*;
import com.gymcrm.dto.trainee.AddTrainingRequestDto;
import com.gymcrm.dto.training.TraineeTrainingsListRequestDto;
import com.gymcrm.dto.training.TraineeTrainingsListResponseDto;
import com.gymcrm.dto.training.TrainerTrainingsListRequestDto;
import com.gymcrm.dto.training.TrainerTrainingsListResponseDto;
import com.gymcrm.metrics.TrainingMetrics;
import com.gymcrm.model.Trainee;
import com.gymcrm.model.Trainer;
import com.gymcrm.model.Training;
import com.gymcrm.service.TrainingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class TrainingServiceImpl implements TrainingService {
    private final TrainingMetrics trainingMetrics;

    private final TrainingMapper trainingMapper;
    private final TrainingRepository trainingRepository;
    private final TraineeRepository traineeRepository;
    private final TrainerRepository trainerRepository;
    private final GeneralUserRepository userRepository;
    private final TrainingTypeRepository trainingTypeRepository;

    @Override
    @Transactional
    public void addTraining(String username, AddTrainingRequestDto training) {
        // You may want to check if the user has rights to add training, but do not authenticate by password
        Trainer trainer = trainerRepository.findByUsername(training.getTrainerUsername())
                .orElseThrow(() -> new RuntimeException("Trainer not found"));
        Trainee trainee = traineeRepository.findByUsername(training.getTraineeUsername())
                .orElseThrow(() -> new RuntimeException("Trainee not found"));

        if (!trainer.getIsActive() || !trainee.getIsActive()) {
            throw new RuntimeException("Cannot add training for inactive user");
        }

        trainingTypeRepository.findById(training.getTrainingType().getId())
                .orElseThrow(() -> new RuntimeException("TrainingType not found"));

        Training entity = trainingMapper.toEntity(training);
        Training saved = trainingRepository.save(entity);
        log.info("Added training by user {}", username);
        trainingMetrics.incrementTrainingCreated();
    }

    @Override
    public List<TraineeTrainingsListResponseDto> getTraineeTrainings(String username, TraineeTrainingsListRequestDto dto) {
        // Only filter by username, do not authenticate by password
        return trainingRepository.findTrainingsForTrainee(
                username, dto.getPeriodFrom(), dto.getPeriodTo(), dto.getTrainerName(), dto.getTrainingTypeName()
        ).stream().map(trainingMapper::toTraineeTrainingsListDto).collect(Collectors.toList());
    }

    @Override
    public List<TrainerTrainingsListResponseDto> getTrainerTrainings(String username, TrainerTrainingsListRequestDto dto) {
        // Only filter by username, do not authenticate by password
        return trainingRepository.findTrainingsForTrainer(
                username, dto.getPeriodFrom(), dto.getPeriodTo(), dto.getTraineeName()
        ).stream().map(trainingMapper::toTrainerTrainingsListDto).collect(Collectors.toList());
    }
}
