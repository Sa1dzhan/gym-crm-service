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
import com.gymcrm.util.Authentication;
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
    public void addTraining(AddTrainingRequestDto training) {
        Authentication.authenticateUser(training.getUsername(), training.getPassword(), userRepository::findByUsername);

        Trainer trainer = trainerRepository.findByUsername(training.getTraineeUsername())
                .orElseThrow(() -> new RuntimeException("Trainer not found"));
        Trainee trainee = traineeRepository.findByUsername(training.getTrainerUsername())
                .orElseThrow(() -> new RuntimeException("Trainee not found"));

        if (!trainer.getIsActive() || !trainee.getIsActive()) {
            throw new RuntimeException("Cannot add training for inactive user");
        }

        trainingTypeRepository.findById(training.getTrainingType().getId())
                .orElseThrow(() -> new RuntimeException("TrainingType not found"));

        Training saved = trainingRepository.save(trainingMapper.toEntity(training));
        log.info("Added Training with ID={}, name={}", saved.getId(), saved.getTrainingName());
        trainingMetrics.incrementTrainingCreated();
    }

    @Override
    public List<TraineeTrainingsListResponseDto> getTraineeTrainings(TraineeTrainingsListRequestDto dto) {
        Authentication.authenticateUser(dto.getUsername(), dto.getPassword(), traineeRepository::findByUsername);
        return trainingRepository.findTrainingsForTrainee(
                dto.getUsername(), dto.getPeriodFrom(), dto.getPeriodTo(), dto.getTrainerName(), dto.getTrainingTypeName()
        ).stream().map(trainingMapper::toTraineeTrainingsListDto).collect(Collectors.toList());
    }

    @Override
    public List<TrainerTrainingsListResponseDto> getTrainerTrainings(TrainerTrainingsListRequestDto dto) {
        Authentication.authenticateUser(dto.getUsername(), dto.getPassword(), trainerRepository::findByUsername);
        return trainingRepository.findTrainingsForTrainer(
                dto.getUsername(), dto.getPeriodFrom(), dto.getPeriodTo(), dto.getTraineeName()
        ).stream().map(trainingMapper::toTrainerTrainingsListDto).collect(Collectors.toList());
    }
}
