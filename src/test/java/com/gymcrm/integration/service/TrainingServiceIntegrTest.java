package com.gymcrm.integration.service;

import com.gymcrm.converter.TrainingMapper;
import com.gymcrm.dao.TraineeRepository;
import com.gymcrm.dao.TrainerRepository;
import com.gymcrm.dao.TrainingRepository;
import com.gymcrm.dao.TrainingTypeRepository;
import com.gymcrm.dto.trainee.AddTrainingRequestDto;
import com.gymcrm.dto.training.TraineeTrainingsListRequestDto;
import com.gymcrm.dto.training.TraineeTrainingsListResponseDto;
import com.gymcrm.dto.training_type.TrainingTypeDto;
import com.gymcrm.dto.workload.WorkloadRequestDto;
import com.gymcrm.metrics.TrainingMetrics;
import com.gymcrm.model.Trainee;
import com.gymcrm.model.Trainer;
import com.gymcrm.model.Training;
import com.gymcrm.model.TrainingType;
import com.gymcrm.service.TrainingService;
import com.gymcrm.service.WorkloadService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@Transactional
public class TrainingServiceIntegrTest {

    @Autowired
    private TrainingService trainingService;

    @MockBean
    private TrainingRepository trainingRepository;
    @MockBean
    private TraineeRepository traineeRepository;
    @MockBean
    private TrainerRepository trainerRepository;
    @MockBean
    private TrainingTypeRepository trainingTypeRepository;
    @MockBean
    private WorkloadService workloadService;
    @MockBean
    private TrainingMetrics trainingMetrics;
    @MockBean
    private TrainingMapper trainingMapper;

    private Trainer trainer;
    private Trainee trainee;
    private TrainingType trainingType;
    private Training training;
    private AddTrainingRequestDto requestDto;

    @BeforeEach
    void setUp() {
        trainingType = new TrainingType();
        trainingType.setId(1L);
        trainingType.setTrainingTypeName("Crossfit");

        trainer = new Trainer();
        trainer.setUsername("test.trainer");
        trainer.setIsActive(true);
        trainer.setSpecialization(trainingType);

        trainee = new Trainee();
        trainee.setUsername("test.trainee");
        trainee.setIsActive(true);

        training = new Training();
        training.setTrainer(trainer);
        training.setTrainee(trainee);
        training.setTrainingType(trainingType);
        training.setTrainingName("Morning Workout");
        training.setTrainingDate(LocalDate.now());
        training.setTrainingDuration(60L);

        TrainingTypeDto trainingTypeDto = new TrainingTypeDto();
        trainingTypeDto.setId(trainingType.getId());
        trainingTypeDto.setTrainingTypeName(trainingType.getTrainingTypeName());

        requestDto = new AddTrainingRequestDto();
        requestDto.setTraineeUsername(trainee.getUsername());
        requestDto.setTrainerUsername(trainer.getUsername());
        requestDto.setTrainingName("Morning Workout");
        requestDto.setTrainingDate(LocalDate.now());
        requestDto.setTrainingDuration(60L);
        requestDto.setTrainingType(trainingTypeDto);
    }

    @Test
    void addTraining() {
        when(trainerRepository.findByUsername("test.trainer")).thenReturn(Optional.of(trainer));
        when(traineeRepository.findByUsername("test.trainee")).thenReturn(Optional.of(trainee));
        when(trainingTypeRepository.findById(1L)).thenReturn(Optional.of(trainingType));
        when(trainingMapper.toEntity(any(AddTrainingRequestDto.class))).thenReturn(training);
        when(trainingRepository.save(any(Training.class))).thenReturn(training);

        trainingService.addTraining("anyUser", requestDto);

        verify(trainingRepository, times(1)).save(any(Training.class));
        verify(trainingMetrics, times(1)).incrementTrainingCreated();

        ArgumentCaptor<WorkloadRequestDto.ActionType> actionTypeCaptor = ArgumentCaptor.forClass(WorkloadRequestDto.ActionType.class);
        verify(workloadService, times(1)).updateTrainerWorkload(any(Training.class), actionTypeCaptor.capture());

        assertThat(actionTypeCaptor.getValue()).isEqualTo(WorkloadRequestDto.ActionType.ADD);
    }

    @Test
    void addTraining_forInactiveUser() {
        trainer.setIsActive(false);
        when(trainerRepository.findByUsername("test.trainer")).thenReturn(Optional.of(trainer));
        when(traineeRepository.findByUsername("test.trainee")).thenReturn(Optional.of(trainee));

        assertThatThrownBy(() -> trainingService.addTraining("anyUser", requestDto))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Cannot add training for inactive user");
    }

    @Test
    void getTraineeTrainings() {
        TraineeTrainingsListRequestDto searchRequest = new TraineeTrainingsListRequestDto();

        when(trainingRepository.findTrainingsForTrainee(any(), any(), any(), any(), any()))
                .thenReturn(Collections.singletonList(training));
        when(trainingMapper.toTraineeTrainingsListDto(any(Training.class)))
                .thenReturn(new TraineeTrainingsListResponseDto());

        List<TraineeTrainingsListResponseDto> result = trainingService.getTraineeTrainings(trainee.getUsername(), searchRequest);

        assertThat(result).hasSize(1);
        verify(trainingRepository, times(1)).findTrainingsForTrainee(any(), any(), any(), any(), any());
        verify(trainingMapper, times(1)).toTraineeTrainingsListDto(any(Training.class));
    }
}
