package com.gymcrm.gymservice.service;

import com.gymcrm.converter.TrainingMapper;
import com.gymcrm.dao.*;
import com.gymcrm.dto.trainee.AddTrainingRequestDto;
import com.gymcrm.dto.training.TraineeTrainingsListRequestDto;
import com.gymcrm.dto.training.TraineeTrainingsListResponseDto;
import com.gymcrm.dto.training.TrainerTrainingsListRequestDto;
import com.gymcrm.dto.training.TrainerTrainingsListResponseDto;
import com.gymcrm.dto.training_type.TrainingTypeDto;
import com.gymcrm.metrics.TrainingMetrics;
import com.gymcrm.model.Trainee;
import com.gymcrm.model.Trainer;
import com.gymcrm.model.Training;
import com.gymcrm.model.TrainingType;
import com.gymcrm.service.impl.TrainingServiceImpl;
import com.gymcrm.util.Authentication;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainingServiceTest {

    @Mock
    private TrainingRepository trainingRepository;
    @Mock
    private TraineeRepository traineeRepository;
    @Mock
    private TrainerRepository trainerRepository;
    @Mock
    private TrainingTypeRepository trainingTypeRepository;
    @Mock
    private GeneralUserRepository userRepository;
    @Mock
    private TrainingMapper trainingMapper;
    @Mock
    private TrainingMetrics trainingMetrics;

    @InjectMocks
    private TrainingServiceImpl trainingService;

    private Trainer activeTrainer;
    private Trainee activeTrainee;
    private TrainingType trainingType;
    private TrainingTypeDto trainingTypeDto;

    private MockedStatic<Authentication> authenticationMock;

    @BeforeEach
    void setUp() {
        activeTrainer = new Trainer();
        activeTrainer.setId(100L);
        activeTrainer.setUsername("activeTrainer");
        activeTrainer.setIsActive(true);

        activeTrainee = new Trainee();
        activeTrainee.setId(200L);
        activeTrainee.setUsername("activeTrainee");
        activeTrainee.setIsActive(true);

        trainingType = new TrainingType();
        trainingType.setId(1L);
        trainingType.setTrainingTypeName("Strength training");

        trainingTypeDto = new TrainingTypeDto();
        trainingTypeDto.setId(1L);
        trainingTypeDto.setTrainingTypeName("Strength training");

        authenticationMock = mockStatic(Authentication.class);
    }

    @AfterEach
    void tearDown() {
        authenticationMock.close();
    }

    @Test
    void testAddTraining_Success() {
        String username = "authUser";
        AddTrainingRequestDto dto = new AddTrainingRequestDto();
        dto.setTraineeUsername("activeTrainee");
        dto.setTrainerUsername("activeTrainer");
        dto.setTrainingType(trainingTypeDto);
        dto.setTrainingName("Morning Workout");
        Date trainingDate = new Date();
        dto.setTrainingDate(trainingDate);
        dto.setTrainingDuration(60L);

        when(trainerRepository.findByUsername("activeTrainer")).thenReturn(Optional.of(activeTrainer));
        when(traineeRepository.findByUsername("activeTrainee")).thenReturn(Optional.of(activeTrainee));

        when(trainingTypeRepository.findById(trainingTypeDto.getId())).thenReturn(Optional.of(trainingType));

        Training trainingEntity = new Training();
        trainingEntity.setTrainer(activeTrainer);
        trainingEntity.setTrainee(activeTrainee);
        trainingEntity.setTrainingType(trainingType);
        trainingEntity.setTrainingName("Morning Workout");
        trainingEntity.setTrainingDate(trainingDate);
        trainingEntity.setTrainingDuration(60L);

        when(trainingMapper.toEntity(dto)).thenReturn(trainingEntity);
        when(trainingRepository.save(any(Training.class))).thenReturn(trainingEntity);

        trainingService.addTraining(username, dto);

        verify(trainingRepository).save(any(Training.class));
    }

    @Test
    void testAddTraining_TrainerNotFound() {
        String username = "authUser";
        AddTrainingRequestDto dto = new AddTrainingRequestDto();
        dto.setTraineeUsername("activeTrainer");
        dto.setTrainerUsername("activeTrainee");
        dto.setTrainingType(trainingTypeDto);
        dto.setTrainingName("Workout");
        dto.setTrainingDate(new Date());
        dto.setTrainingDuration(60L);

        when(trainerRepository.findByUsername("activeTrainer")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> trainingService.addTraining(username, dto));
        verify(trainingRepository, never()).save(any());
    }

    @Test
    void testAddTraining_TraineeNotFound() {
        String username = "authUser";
        AddTrainingRequestDto dto = new AddTrainingRequestDto();
        dto.setTraineeUsername("activeTrainer");
        dto.setTrainerUsername("activeTrainee");
        dto.setTrainingType(trainingTypeDto);
        dto.setTrainingName("Workout");
        dto.setTrainingDate(new Date());
        dto.setTrainingDuration(60L);

        when(trainerRepository.findByUsername("activeTrainer")).thenReturn(Optional.of(activeTrainer));
        when(traineeRepository.findByUsername("activeTrainee")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> trainingService.addTraining(username, dto));
        verify(trainingRepository, never()).save(any());
    }

    @Test
    void testAddTraining_InactiveTrainer() {
        String username = "authUser";
        AddTrainingRequestDto dto = new AddTrainingRequestDto();
        Trainer inactiveTrainer = new Trainer();
        inactiveTrainer.setId(101L);
        inactiveTrainer.setUsername("inactiveTrainer");
        inactiveTrainer.setIsActive(false);
        dto.setTraineeUsername("inactiveTrainer");
        dto.setTrainerUsername("activeTrainee");
        dto.setTrainingType(trainingTypeDto);
        dto.setTrainingName("Workout");
        dto.setTrainingDate(new Date());
        dto.setTrainingDuration(60L);

        when(trainerRepository.findByUsername("inactiveTrainer")).thenReturn(Optional.of(inactiveTrainer));

        assertThrows(RuntimeException.class, () -> trainingService.addTraining(username, dto));
        verify(trainingRepository, never()).save(any());
    }

    @Test
    void testAddTraining_InactiveTrainee() {
        String username = "authUser";
        AddTrainingRequestDto dto = new AddTrainingRequestDto();
        Trainee inactiveTrainee = new Trainee();
        inactiveTrainee.setId(201L);
        inactiveTrainee.setUsername("inactiveTrainee");
        inactiveTrainee.setIsActive(false);
        dto.setTraineeUsername("activeTrainer");
        dto.setTrainerUsername("inactiveTrainee");
        dto.setTrainingType(trainingTypeDto);
        dto.setTrainingName("Workout");
        dto.setTrainingDate(new Date());
        dto.setTrainingDuration(60L);

        when(trainerRepository.findByUsername("activeTrainer")).thenReturn(Optional.of(activeTrainer));
        when(traineeRepository.findByUsername("inactiveTrainee")).thenReturn(Optional.of(inactiveTrainee));

        assertThrows(RuntimeException.class, () -> trainingService.addTraining(username, dto));
        verify(trainingRepository, never()).save(any());
    }

    @Test
    void testAddTraining_TrainingTypeNotFound() {
        String username = "authUser";
        AddTrainingRequestDto dto = new AddTrainingRequestDto();
        dto.setTraineeUsername("activeTrainer");
        dto.setTrainerUsername("activeTrainee");
        dto.setTrainingType(trainingTypeDto);
        dto.setTrainingName("Workout");
        dto.setTrainingDate(new Date());
        dto.setTrainingDuration(60L);

        when(trainerRepository.findByUsername("activeTrainer")).thenReturn(Optional.of(activeTrainer));
        when(traineeRepository.findByUsername("activeTrainee")).thenReturn(Optional.of(activeTrainee));
        when(trainingTypeRepository.findById(trainingTypeDto.getId())).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> trainingService.addTraining(username, dto));
        verify(trainingRepository, never()).save(any());
    }

    @Test
    void testGetTraineeTrainings() {
        String username = "authUser";
        TraineeTrainingsListRequestDto reqDto = new TraineeTrainingsListRequestDto();
        reqDto.setTrainerName("trainerName");
        reqDto.setTrainingTypeName("Strength");
        Date fromDate = new Date();
        Date toDate = new Date();
        reqDto.setPeriodFrom(fromDate);
        reqDto.setPeriodTo(toDate);

        Training training = new Training();
        List<Training> mockList = Collections.singletonList(training);
        when(trainingRepository.findTrainingsForTrainee(
                eq(username), eq(fromDate), eq(toDate), eq("trainerName"), eq("Strength")
        )).thenReturn(mockList);

        TraineeTrainingsListResponseDto respDto = new TraineeTrainingsListResponseDto();
        when(trainingMapper.toTraineeTrainingsListDto(training)).thenReturn(respDto);

        List<TraineeTrainingsListResponseDto> result = trainingService.getTraineeTrainings(username, reqDto);

        assertEquals(1, result.size());
        verify(trainingRepository).findTrainingsForTrainee(
                username, fromDate, toDate, "trainerName", "Strength"
        );
    }

    @Test
    void testGetTrainerTrainings() {
        String username = "authUser";
        TrainerTrainingsListRequestDto reqDto = new TrainerTrainingsListRequestDto();
        reqDto.setTraineeName("traineeName");
        Date fromDate = new Date();
        Date toDate = new Date();
        reqDto.setPeriodFrom(fromDate);
        reqDto.setPeriodTo(toDate);

        Training training1 = new Training();
        Training training2 = new Training();
        List<Training> mockList = Arrays.asList(training1, training2);
        when(trainingRepository.findTrainingsForTrainer(
                eq(username), eq(fromDate), eq(toDate), eq("traineeName")
        )).thenReturn(mockList);

        TrainerTrainingsListResponseDto dto1 = new TrainerTrainingsListResponseDto();
        TrainerTrainingsListResponseDto dto2 = new TrainerTrainingsListResponseDto();
        when(trainingMapper.toTrainerTrainingsListDto(training1)).thenReturn(dto1);
        when(trainingMapper.toTrainerTrainingsListDto(training2)).thenReturn(dto2);

        List<TrainerTrainingsListResponseDto> result = trainingService.getTrainerTrainings(username, reqDto);

        assertEquals(2, result.size());
        verify(trainingRepository).findTrainingsForTrainer(
                username, fromDate, toDate, "traineeName"
        );
    }
}
