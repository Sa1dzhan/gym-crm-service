package com.gymcrm.gymservice.service;

import com.gymcrm.converter.TrainingMapper;
import com.gymcrm.dao.*;
import com.gymcrm.dto.trainee.AddTrainingRequestDto;
import com.gymcrm.dto.training.TraineeTrainingsListRequestDto;
import com.gymcrm.dto.training.TraineeTrainingsListResponseDto;
import com.gymcrm.dto.training.TrainerTrainingsListRequestDto;
import com.gymcrm.dto.training.TrainerTrainingsListResponseDto;
import com.gymcrm.dto.training_type.TrainingTypeDto;
import com.gymcrm.model.*;
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
        AddTrainingRequestDto dto = new AddTrainingRequestDto();
        dto.setUsername("authUser");
        dto.setPassword("authPass");
        dto.setTraineeUsername("activeTrainer");
        dto.setTrainerUsername("activeTrainee");
        dto.setTrainingType(trainingTypeDto);
        dto.setTrainingName("Morning Workout");
        Date trainingDate = new Date();
        dto.setTrainingDate(trainingDate);
        dto.setTrainingDuration(60L);

        User dummyUser = new User();
        authenticationMock.when(() ->
                Authentication.authenticateUser(eq("authUser"), eq("authPass"), any())
        ).thenReturn(dummyUser);

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

        when(trainingRepository.save(trainingEntity)).thenAnswer(inv -> {
            Training t = inv.getArgument(0);
            t.setId(999L);
            return t;
        });

        trainingService.addTraining(dto);
        verify(trainingRepository).save(trainingEntity);
    }

    @Test
    void testAddTraining_TrainerNotFound() {
        AddTrainingRequestDto dto = new AddTrainingRequestDto();
        dto.setUsername("authUser");
        dto.setPassword("authPass");
        dto.setTraineeUsername("activeTrainer");
        dto.setTrainerUsername("activeTrainee");
        dto.setTrainingType(trainingTypeDto);
        dto.setTrainingName("Workout");
        dto.setTrainingDate(new Date());
        dto.setTrainingDuration(60L);

        User dummyUser = new User();
        authenticationMock.when(() ->
                Authentication.authenticateUser(eq("authUser"), eq("authPass"), any())
        ).thenReturn(dummyUser);

        assertThrows(RuntimeException.class, () -> trainingService.addTraining(dto));
        verify(trainingRepository, never()).save(any());
    }

    @Test
    void testAddTraining_TraineeNotFound() {
        AddTrainingRequestDto dto = new AddTrainingRequestDto();
        dto.setUsername("authUser");
        dto.setPassword("authPass");
        dto.setTraineeUsername("activeTrainer");
        dto.setTrainerUsername("activeTrainee");
        dto.setTrainingType(trainingTypeDto);
        dto.setTrainingName("Workout");
        dto.setTrainingDate(new Date());
        dto.setTrainingDuration(60L);

        User dummyUser = new User();
        authenticationMock.when(() ->
                Authentication.authenticateUser(eq("authUser"), eq("authPass"), any())
        ).thenReturn(dummyUser);

        assertThrows(RuntimeException.class, () -> trainingService.addTraining(dto));
        verify(trainingRepository, never()).save(any());
    }

    @Test
    void testAddTraining_InactiveTrainer() {
        AddTrainingRequestDto dto = new AddTrainingRequestDto();
        dto.setUsername("authUser");
        dto.setPassword("authPass");
        dto.setTraineeUsername("inactiveTrainer");
        dto.setTrainerUsername("activeTrainee");
        dto.setTrainingType(trainingTypeDto);
        dto.setTrainingName("Workout");
        dto.setTrainingDate(new Date());
        dto.setTrainingDuration(60L);

        User dummyUser = new User();
        authenticationMock.when(() ->
                Authentication.authenticateUser(eq("authUser"), eq("authPass"), any())
        ).thenReturn(dummyUser);

        assertThrows(RuntimeException.class, () -> trainingService.addTraining(dto));
        verify(trainingRepository, never()).save(any());
    }

    @Test
    void testAddTraining_InactiveTrainee() {
        AddTrainingRequestDto dto = new AddTrainingRequestDto();
        dto.setUsername("authUser");
        dto.setPassword("authPass");
        dto.setTraineeUsername("activeTrainer");
        dto.setTrainerUsername("inactiveTrainee");
        dto.setTrainingType(trainingTypeDto);
        dto.setTrainingName("Workout");
        dto.setTrainingDate(new Date());
        dto.setTrainingDuration(60L);

        User dummyUser = new User();
        authenticationMock.when(() ->
                Authentication.authenticateUser(eq("authUser"), eq("authPass"), any())
        ).thenReturn(dummyUser);

        assertThrows(RuntimeException.class, () -> trainingService.addTraining(dto));
        verify(trainingRepository, never()).save(any());
    }

    @Test
    void testAddTraining_TrainingTypeNotFound() {
        AddTrainingRequestDto dto = new AddTrainingRequestDto();
        dto.setUsername("authUser");
        dto.setPassword("authPass");
        dto.setTraineeUsername("activeTrainer");
        dto.setTrainerUsername("activeTrainee");
        dto.setTrainingType(trainingTypeDto);
        dto.setTrainingName("Workout");
        dto.setTrainingDate(new Date());
        dto.setTrainingDuration(60L);

        User dummyUser = new User();
        authenticationMock.when(() ->
                Authentication.authenticateUser(eq("authUser"), eq("authPass"), any())
        ).thenReturn(dummyUser);

        assertThrows(RuntimeException.class, () -> trainingService.addTraining(dto));
        verify(trainingRepository, never()).save(any());
    }

    @Test
    void testGetTraineeTrainings() {
        TraineeTrainingsListRequestDto reqDto = new TraineeTrainingsListRequestDto();
        reqDto.setUsername("authUser");
        reqDto.setPassword("authPass");
        reqDto.setTrainerName("trainerName");
        reqDto.setTrainingTypeName("Strength");
        Date fromDate = new Date();
        Date toDate = new Date();
        reqDto.setPeriodFrom(fromDate);
        reqDto.setPeriodTo(toDate);


        authenticationMock.when(() ->
                Authentication.authenticateUser(eq("authUser"), eq("authPass"), any())
        ).thenReturn(activeTrainee);

        Training training = new Training();
        List<Training> mockList = Collections.singletonList(training);
        when(trainingRepository.findTrainingsForTrainee(
                eq("authUser"), eq(fromDate), eq(toDate), eq("trainerName"), eq("Strength")
        )).thenReturn(mockList);

        TraineeTrainingsListResponseDto respDto = new TraineeTrainingsListResponseDto();
        when(trainingMapper.toTraineeTrainingsListDto(training)).thenReturn(respDto);

        List<TraineeTrainingsListResponseDto> result = trainingService.getTraineeTrainings(reqDto);

        assertEquals(1, result.size());
        verify(trainingRepository).findTrainingsForTrainee(
                "authUser", fromDate, toDate, "trainerName", "Strength"
        );
    }

    @Test
    void testGetTrainerTrainings() {
        TrainerTrainingsListRequestDto reqDto = new TrainerTrainingsListRequestDto();
        reqDto.setUsername("authUser");
        reqDto.setPassword("authPass");
        reqDto.setTraineeName("traineeName");
        Date fromDate = new Date();
        Date toDate = new Date();
        reqDto.setPeriodFrom(fromDate);
        reqDto.setPeriodTo(toDate);

        authenticationMock.when(() ->
                Authentication.authenticateUser(eq("authUser"), eq("authPass"), any())
        ).thenReturn(activeTrainer);

        Training training1 = new Training();
        Training training2 = new Training();
        List<Training> mockList = Arrays.asList(training1, training2);
        when(trainingRepository.findTrainingsForTrainer(
                eq("authUser"), eq(fromDate), eq(toDate), eq("traineeName")
        )).thenReturn(mockList);

        TrainerTrainingsListResponseDto dto1 = new TrainerTrainingsListResponseDto();
        TrainerTrainingsListResponseDto dto2 = new TrainerTrainingsListResponseDto();
        when(trainingMapper.toTrainerTrainingsListDto(training1)).thenReturn(dto1);
        when(trainingMapper.toTrainerTrainingsListDto(training2)).thenReturn(dto2);

        List<TrainerTrainingsListResponseDto> result = trainingService.getTrainerTrainings(reqDto);

        assertEquals(2, result.size());
        verify(trainingRepository).findTrainingsForTrainer(
                "authUser", fromDate, toDate, "traineeName"
        );
    }
}
