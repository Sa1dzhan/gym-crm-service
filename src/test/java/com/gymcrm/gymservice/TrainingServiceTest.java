package com.gymcrm.gymservice;

import com.gymcrm.dao.TraineeRepository;
import com.gymcrm.dao.TrainerRepository;
import com.gymcrm.dao.TrainingRepository;
import com.gymcrm.dao.TrainingTypeRepository;
import com.gymcrm.model.Trainee;
import com.gymcrm.model.Trainer;
import com.gymcrm.model.Training;
import com.gymcrm.model.TrainingType;
import com.gymcrm.service.impl.TrainingServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
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

    @InjectMocks
    private TrainingServiceImpl trainingService;

    private Trainer activeTrainer;
    private Trainer inactiveTrainer;
    private Trainee activeTrainee;
    private Trainee inactiveTrainee;
    private TrainingType validType;

    @BeforeEach
    void setUp() {
        activeTrainer = new Trainer();
        activeTrainer.setId(100L);
        activeTrainer.setUsername("activeTrainer");
        activeTrainer.setIsActive(true);

        inactiveTrainer = new Trainer();
        inactiveTrainer.setId(101L);
        inactiveTrainer.setUsername("inactiveTrainer");
        inactiveTrainer.setIsActive(false);

        activeTrainee = new Trainee();
        activeTrainee.setId(200L);
        activeTrainee.setUsername("activeTrainee");
        activeTrainee.setIsActive(true);

        inactiveTrainee = new Trainee();
        inactiveTrainee.setId(201L);
        inactiveTrainee.setUsername("inactiveTrainee");
        inactiveTrainee.setIsActive(false);

        validType = new TrainingType();
        validType.setId(1L);
        validType.setTrainingTypeName("Strength training");
    }

    @Test
    void testAddTraining_Success() {
        Training training = new Training();
        training.setTrainer(activeTrainer);
        training.setTrainee(activeTrainee);
        training.setTrainingType(validType);
        training.setTrainingName("Morning Workout");
        training.setTrainingDate(new Date());
        training.setTrainingDuration(60L);

        when(trainerRepository.findByUsername("activeTrainer")).thenReturn(Optional.of(activeTrainer));
        when(traineeRepository.findByUsername("activeTrainee")).thenReturn(Optional.of(activeTrainee));
        when(trainingTypeRepository.findById(1L)).thenReturn(Optional.of(validType));
        when(trainingRepository.save(training)).thenAnswer(inv -> {
            Training t = inv.getArgument(0);
            t.setId(999L);
            return t;
        });

        Training saved = trainingService.addTraining("authUser", "authPass", training);

        assertNotNull(saved.getId());
        assertEquals("Morning Workout", saved.getTrainingName());
        verify(trainingRepository).save(training);
    }

    @Test
    void testAddTraining_TrainerNotFound() {
        Training training = new Training();
        training.setTrainer(activeTrainer);
        training.setTrainee(activeTrainee);
        training.setTrainingType(validType);

        when(trainerRepository.findByUsername("activeTrainer")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () ->
                trainingService.addTraining("authUser", "authPass", training)
        );
        verify(trainingRepository, never()).save(any());
    }

    @Test
    void testAddTraining_TraineeNotFound() {
        Training training = new Training();
        training.setTrainer(activeTrainer);
        training.setTrainee(activeTrainee);

        when(trainerRepository.findByUsername("activeTrainer")).thenReturn(Optional.of(activeTrainer));
        when(traineeRepository.findByUsername("activeTrainee")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () ->
                trainingService.addTraining("authUser", "authPass", training)
        );
        verify(trainingRepository, never()).save(any());
    }

    @Test
    void testAddTraining_InactiveTrainer() {
        Training training = new Training();
        training.setTrainer(inactiveTrainer);
        training.setTrainee(activeTrainee);
        training.setTrainingType(validType);

        when(trainerRepository.findByUsername("inactiveTrainer")).thenReturn(Optional.of(inactiveTrainer));
        when(traineeRepository.findByUsername("activeTrainee")).thenReturn(Optional.of(activeTrainee));

        assertThrows(RuntimeException.class, () ->
                trainingService.addTraining("authUser", "authPass", training)
        );
        verify(trainingRepository, never()).save(any());
    }

    @Test
    void testAddTraining_InactiveTrainee() {
        Training training = new Training();
        training.setTrainer(activeTrainer);
        training.setTrainee(inactiveTrainee);
        training.setTrainingType(validType);

        when(trainerRepository.findByUsername("activeTrainer")).thenReturn(Optional.of(activeTrainer));
        when(traineeRepository.findByUsername("inactiveTrainee")).thenReturn(Optional.of(inactiveTrainee));

        assertThrows(RuntimeException.class, () ->
                trainingService.addTraining("authUser", "authPass", training)
        );
        verify(trainingRepository, never()).save(any());
    }

    @Test
    void testAddTraining_TrainingTypeNotFound() {
        Training training = new Training();
        training.setTrainer(activeTrainer);
        training.setTrainee(activeTrainee);
        training.setTrainingType(validType);

        when(trainerRepository.findByUsername("activeTrainer")).thenReturn(Optional.of(activeTrainer));
        when(traineeRepository.findByUsername("activeTrainee")).thenReturn(Optional.of(activeTrainee));
        when(trainingTypeRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () ->
                trainingService.addTraining("authUser", "authPass", training)
        );
        verify(trainingRepository, never()).save(any());
    }

    @Test
    void testGetTraining_Success() {
        Training mockTraining = new Training();
        mockTraining.setId(123L);
        mockTraining.setTrainingName("Evening Session");

        when(trainingRepository.findById(123L)).thenReturn(Optional.of(mockTraining));

        Training found = trainingService.getTraining(123L);
        assertNotNull(found);
        assertEquals("Evening Session", found.getTrainingName());
    }

    @Test
    void testGetTraining_NotFound() {
        when(trainingRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> trainingService.getTraining(999L));
    }

    @Test
    void testGetAllTrainings() {
        Training t1 = new Training();
        t1.setId(10L);
        Training t2 = new Training();
        t2.setId(20L);

        when(trainingRepository.findAll()).thenReturn(Arrays.asList(t1, t2));

        List<Training> all = trainingService.getAllTrainings();
        assertEquals(2, all.size());
        verify(trainingRepository).findAll();
    }

    @Test
    void testGetTraineeTrainings() {
        Date fromDate = new Date();
        Date toDate = new Date();
        List<Training> mockList = Collections.singletonList(new Training());
        when(trainingRepository.findTrainingsForTrainee(
                eq("activeTrainee"), eq(fromDate), eq(toDate), eq("trainerName"), eq("Strength")))
                .thenReturn(mockList);

        List<Training> result = trainingService.getTraineeTrainings(
                "authUser", "authPass",
                "activeTrainee", fromDate, toDate,
                "trainerName", "Strength");

        assertEquals(1, result.size());
        verify(trainingRepository).findTrainingsForTrainee(
                "activeTrainee", fromDate, toDate, "trainerName", "Strength");
    }

    @Test
    void testGetTrainerTrainings() {
        Date fromDate = new Date();
        Date toDate = new Date();
        List<Training> mockList = Arrays.asList(new Training(), new Training());
        when(trainingRepository.findTrainingsForTrainer(
                eq("activeTrainer"), eq(fromDate), eq(toDate), eq("traineeName")))
                .thenReturn(mockList);

        List<Training> result = trainingService.getTrainerTrainings(
                "authUser", "authPass",
                "activeTrainer", fromDate, toDate, "traineeName");

        assertEquals(2, result.size());
        verify(trainingRepository).findTrainingsForTrainer(
                "activeTrainer", fromDate, toDate, "traineeName");
    }
}
