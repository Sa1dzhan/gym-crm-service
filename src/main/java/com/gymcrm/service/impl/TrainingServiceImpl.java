package com.gymcrm.service.impl;

import com.gymcrm.converter.TrainingMapper;
import com.gymcrm.dao.*;
import com.gymcrm.dto.trainee.AddTrainingRequestDto;
import com.gymcrm.dto.training.TraineeTrainingsListRequestDto;
import com.gymcrm.dto.training.TraineeTrainingsListResponseDto;
import com.gymcrm.dto.training.TrainerTrainingsListRequestDto;
import com.gymcrm.dto.training.TrainerTrainingsListResponseDto;
import com.gymcrm.dto.workload.WorkloadRequestDto;
import com.gymcrm.metrics.TrainingMetrics;
import com.gymcrm.model.Trainee;
import com.gymcrm.model.Trainer;
import com.gymcrm.model.Training;
import com.gymcrm.model.TrainingType;
import com.gymcrm.service.TrainingService;
import com.gymcrm.service.WorkloadService;
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
    private final WorkloadService workloadService;

    private final TrainingMapper trainingMapper;
    private final TrainingRepository trainingRepository;
    private final TraineeRepository traineeRepository;
    private final TrainerRepository trainerRepository;
    private final GeneralUserRepository userRepository;
    private final TrainingTypeRepository trainingTypeRepository;

    @Override
    @Transactional
    public void addTraining(String username, AddTrainingRequestDto training) {
        Trainer trainer = trainerRepository.findByUsername(training.getTrainerUsername())
                .orElseThrow(() -> new RuntimeException("Trainer not found"));
        Trainee trainee = traineeRepository.findByUsername(training.getTraineeUsername())
                .orElseThrow(() -> new RuntimeException("Trainee not found"));

        if (!trainer.getIsActive() || !trainee.getIsActive()) {
            throw new RuntimeException("Cannot add training for inactive user");
        }

        TrainingType type = trainingTypeRepository.findById(training.getTrainingType().getId())
                .orElseThrow(() -> new RuntimeException("TrainingType not found"));

        Training entity = trainingMapper.toEntity(training);
        entity.setTrainer(trainer);
        entity.setTrainee(trainee);
        entity.setTrainingType(type);
        Training saved = trainingRepository.save(entity);

        workloadService.updateTrainerWorkload(saved, WorkloadRequestDto.ActionType.ADD);
        log.info("Added training by user {}", username);
        trainingMetrics.incrementTrainingCreated();
    }

    @Override
    public List<TraineeTrainingsListResponseDto> getTraineeTrainings(String username, TraineeTrainingsListRequestDto dto) {
        return trainingRepository.findTrainingsForTrainee(
                username, dto.getPeriodFrom(), dto.getPeriodTo(), dto.getTrainerName(), dto.getTrainingTypeName()
        ).stream().map(trainingMapper::toTraineeTrainingsListDto).collect(Collectors.toList());
    }

    @Override
    public List<TrainerTrainingsListResponseDto> getTrainerTrainings(String username, TrainerTrainingsListRequestDto dto) {
        return trainingRepository.findTrainingsForTrainer(
                username, dto.getPeriodFrom(), dto.getPeriodTo(), dto.getTraineeName()
        ).stream().map(trainingMapper::toTrainerTrainingsListDto).collect(Collectors.toList());
    }
}
