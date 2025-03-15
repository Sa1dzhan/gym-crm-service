package com.gymcrm.gymservice;

import com.gymcrm.dao.TrainerRepository;
import com.gymcrm.model.Trainer;
import com.gymcrm.model.TrainingType;
import com.gymcrm.service.impl.TrainerServiceImpl;
import com.gymcrm.util.Authentication;
import com.gymcrm.util.UserCredentialGenerator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class TrainerServiceTest {

    @Mock
    private TrainerRepository trainerRepository;

    @InjectMocks
    private TrainerServiceImpl trainerService;

    private MockedStatic<Authentication> authenticationMock;
    private MockedStatic<UserCredentialGenerator> userCredentialGeneratorMock;

    @BeforeEach
    void setUp() {
        authenticationMock = mockStatic(Authentication.class);
        userCredentialGeneratorMock = mockStatic(UserCredentialGenerator.class);
    }

    @AfterEach
    void tearDown() {
        authenticationMock.close();
        userCredentialGeneratorMock.close();
    }

    @Test
    void testCreateTrainer_Success() {
        Trainer newTrainer = new Trainer();
        newTrainer.setFirstName("Alice");
        newTrainer.setLastName("Smith");


        userCredentialGeneratorMock.when(() ->
                UserCredentialGenerator.generateUserCredentials(eq(newTrainer), any())
        ).thenAnswer(invocation -> {

            newTrainer.setUsername("Alice.Smith");
            newTrainer.setPassword("randomPass123");
            return null;
        });

        when(trainerRepository.save(any(Trainer.class))).thenAnswer(inv -> {
            Trainer t = inv.getArgument(0);
            t.setId(10L);
            return t;
        });

        Trainer created = trainerService.createTrainer(newTrainer);

        assertNotNull(created.getId());
        assertEquals("Alice.Smith", created.getUsername());
        assertEquals("randomPass123", created.getPassword());
        verify(trainerRepository).save(newTrainer);

        authenticationMock.verifyNoInteractions();
    }

    @Test
    void testUpdateTrainer_Success() {
        Trainer existing = new Trainer();
        existing.setId(20L);
        existing.setUsername("Alice.Smith");
        existing.setPassword("oldPass");

        authenticationMock.when(() ->
                Authentication.authenticateUser(eq("Alice.Smith"), eq("oldPass"), any())
        ).thenReturn(existing);

        when(trainerRepository.save(any(Trainer.class))).thenAnswer(inv -> inv.getArgument(0));

        TrainingType tp = new TrainingType();
        tp.setId(1L);
        tp.setTrainingTypeName("Strength training");
        existing.setSpecialization(tp);
        Trainer updated = trainerService.updateTrainer(existing);

        assertEquals("Strength training", updated.getSpecialization().getTrainingTypeName());
        verify(trainerRepository).save(existing);
        authenticationMock.verify(() ->
                Authentication.authenticateUser(eq("Alice.Smith"), eq("oldPass"), any())
        );
    }

    @Test
    void testGetTrainer_Success() {
        Trainer t = new Trainer();
        t.setId(30L);
        t.setUsername("Alice.Smith");
        when(trainerRepository.findById(30L)).thenReturn(Optional.of(t));

        Trainer found = trainerService.getTrainer(30L);
        assertNotNull(found);
        assertEquals(30L, found.getId());
        verify(trainerRepository).findById(30L);
    }

    @Test
    void testGetTrainer_NotFound() {
        when(trainerRepository.findById(999L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> trainerService.getTrainer(999L));
    }

    @Test
    void testGetByUsername_Success() {
        Trainer t = new Trainer();
        t.setUsername("Alice.Smith");
        when(trainerRepository.findByUsername("Alice.Smith")).thenReturn(Optional.of(t));

        Trainer found = trainerService.getByUsername("Alice.Smith");
        assertEquals("Alice.Smith", found.getUsername());
        verify(trainerRepository).findByUsername("Alice.Smith");
    }

    @Test
    void testGetByUsername_NotFound() {
        when(trainerRepository.findByUsername("NoUser")).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> trainerService.getByUsername("NoUser"));
    }

    @Test
    void testChangePassword_Success() {
        Trainer t = new Trainer();
        t.setUsername("TrainerUser");
        t.setPassword("oldPass");

        authenticationMock.when(() ->
                Authentication.authenticateUser(eq("TrainerUser"), eq("oldPass"), any())
        ).thenReturn(t);

        userCredentialGeneratorMock.when(() ->
                UserCredentialGenerator.checkNewPassword("newPass123")
        ).thenAnswer(inv -> null);

        when(trainerRepository.save(any(Trainer.class))).thenAnswer(inv -> inv.getArgument(0));

        trainerService.changePassword("TrainerUser", "oldPass", "newPass123");
        assertEquals("newPass123", t.getPassword());
        verify(trainerRepository).save(t);
    }

    @Test
    void testChangePassword_Weak() {
        Trainer t = new Trainer();
        t.setUsername("Alice.Smith");
        t.setPassword("old123");

        authenticationMock.when(() ->
                Authentication.authenticateUser(eq("Alice.Smith"), eq("old123"), any())
        ).thenReturn(t);

        userCredentialGeneratorMock.when(() ->
                UserCredentialGenerator.checkNewPassword("new123")
        ).thenThrow(new IllegalArgumentException("Password must be 6+ chars"));

        assertThrows(IllegalArgumentException.class, () ->
                trainerService.changePassword("Alice.Smith", "old123", "new123")
        );
        verify(trainerRepository, never()).save(any());
    }

    @Test
    void testToggleActive_Success() {
        Trainer t = new Trainer();
        t.setUsername("Alice.Smith");
        t.setPassword("randomPass123");
        t.setIsActive(true);

        authenticationMock.when(() ->
                Authentication.authenticateUser(eq("Alice.Smith"), eq("randomPass123"), any())
        ).thenReturn(t);

        when(trainerRepository.save(t)).thenReturn(t);

        trainerService.toggleActive("Alice.Smith", "randomPass123");
        assertFalse(t.getIsActive());
        verify(trainerRepository).save(t);
    }

    @Test
    void testGetAllTrainers_Success() {
        Trainer t1 = new Trainer();
        t1.setUsername("trainer1");
        Trainer t2 = new Trainer();
        t2.setUsername("trainer2");
        when(trainerRepository.findAll()).thenReturn(Arrays.asList(t1, t2));

        List<Trainer> all = trainerService.getAllTrainers();
        assertEquals(2, all.size());
        verify(trainerRepository).findAll();
    }
}
