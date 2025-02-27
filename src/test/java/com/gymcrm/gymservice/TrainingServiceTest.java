package com.gymcrm.gymservice;

import com.gymcrm.dao.TrainingRepository;
import com.gymcrm.model.Training;
import com.gymcrm.service.impl.TrainingServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainingServiceTest {

    @Mock
    private TrainingRepository trainingRepository;

    @InjectMocks
    private TrainingServiceImpl trainingService;

    @Test
    void testCreateTraining() {
        // Given
        Training training = new Training();
        training.setTrainingName("Morning Session");

        // When
        Training created = trainingService.createTraining(training);

        // Then
        assertNotNull(created.getId());
        verify(trainingRepository, times(1)).create(any(Training.class));
    }

    @Test
    void testGetTraining() {
        // Given
        Long trainingId = 10L;
        Training mockTraining = new Training();
        mockTraining.setId(trainingId);
        mockTraining.setTrainingName("Yoga Class");
        when(trainingRepository.read(trainingId)).thenReturn(mockTraining);

        // When
        Training found = trainingService.getTraining(trainingId);

        // Then
        assertEquals("Yoga Class", found.getTrainingName());
        verify(trainingRepository, times(1)).read(trainingId);
    }


    @Test
    void testGetAllTrainings() {
        Training t1 = new Training();
        Training t2 = new Training();
        when(trainingRepository.findAll()).thenReturn(Arrays.asList(t1, t2));

        List<Training> all = trainingService.getAllTrainings();

        assertEquals(2, all.size());
        verify(trainingRepository, times(1)).findAll();
    }
}

