package com.gymcrm.gymservice;

import com.gymcrm.dao.TrainerRepository;
import com.gymcrm.model.Trainer;
import com.gymcrm.service.impl.TrainerServiceImpl;
import org.junit.jupiter.api.BeforeEach;
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
class TrainerServiceTest {

    @Mock
    private TrainerRepository trainerRepository;

    @InjectMocks
    private TrainerServiceImpl trainerService;

    @BeforeEach
    void setup() {
    }

    @Test
    void testCreateTrainer_UserNameDoesNotExist() {
        Trainer trainer = new Trainer();
        trainer.setId(1L);
        trainer.setFirstName("John");
        trainer.setLastName("Doe");

        when(trainerRepository.existsByUsername("John.Doe")).thenReturn(false);
        
        Trainer created = trainerService.createTrainer(trainer);

        assertNotNull(created.getId(), "ID should be generated by the service");
        assertNotNull(created.getPassword(), "Password should be generated by the service");
        assertEquals(10, created.getPassword().length(), "Password should be of length 10");
        assertEquals("John.Doe", created.getUsername(), "Username should be firstName.lastName");

        verify(trainerRepository, times(1)).create(any(Trainer.class));
    }

    @Test
    void testCreateTrainer_UserNameExistsOnce() {
        Trainer trainer = new Trainer();
        trainer.setId(1L);
        trainer.setFirstName("John");
        trainer.setLastName("Doe");

        when(trainerRepository.existsByUsername("John.Doe")).thenReturn(true);
        when(trainerRepository.existsByUsername("John.Doe1")).thenReturn(false);

        Trainer created = trainerService.createTrainer(trainer);

        assertEquals("John.Doe1", created.getUsername(), "Username should be firstName.lastName1");

        verify(trainerRepository, times(1)).create(any(Trainer.class));
    }

    @Test
    void testCreateTrainer_UserNameExistsTwice() {
        Trainer trainer = new Trainer();
        trainer.setId(3L);
        trainer.setFirstName("John");
        trainer.setLastName("Doe");

        when(trainerRepository.existsByUsername("John.Doe")).thenReturn(true);
        when(trainerRepository.existsByUsername("John.Doe1")).thenReturn(true);
        when(trainerRepository.existsByUsername("John.Doe2")).thenReturn(false);

        Trainer created = trainerService.createTrainer(trainer);

        assertEquals("John.Doe2", created.getUsername(), "Username should be firstName.lastName2");

        verify(trainerRepository, times(1)).create(any(Trainer.class));
    }

    @Test
    void testUpdateTrainer() {
        Trainer existing = new Trainer();
        existing.setId(10L);
        existing.setFirstName("Jane");

        trainerService.updateTrainer(existing);

        verify(trainerRepository, times(1)).update(existing);
    }

    @Test
    void testGetTrainer() {
        Long trainerId = 2L;
        Trainer mockTrainer = new Trainer();
        mockTrainer.setId(trainerId);
        mockTrainer.setFirstName("Mark");

        when(trainerRepository.read(trainerId)).thenReturn(mockTrainer);

        Trainer result = trainerService.getTrainer(trainerId);

        assertNotNull(result, "Should return the trainer from DAO");
        assertEquals("Mark", result.getFirstName());
        verify(trainerRepository, times(1)).read(trainerId);
    }

    @Test
    void testGetAllTrainers() {
        Trainer t1 = new Trainer();
        Trainer t2 = new Trainer();
        when(trainerRepository.findAll()).thenReturn(Arrays.asList(t1, t2));

        List<Trainer> all = trainerService.getAllTrainers();

        assertEquals(2, all.size());
        verify(trainerRepository, times(1)).findAll();
    }
}
