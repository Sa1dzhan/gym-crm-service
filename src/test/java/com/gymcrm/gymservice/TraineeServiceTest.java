package com.gymcrm.gymservice;

import com.gymcrm.dao.TraineeRepository;
import com.gymcrm.dao.TrainerRepository;
import com.gymcrm.model.Trainee;
import com.gymcrm.model.Trainer;
import com.gymcrm.service.impl.TraineeServiceImpl;
import com.gymcrm.util.Authentication;
import com.gymcrm.util.UserCredentialGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TraineeServiceTest {

    @Mock
    private TraineeRepository traineeRepository;

    @Mock
    private TrainerRepository trainerRepository;

    @InjectMocks
    private TraineeServiceImpl traineeService;

    private MockedStatic<Authentication> authenticationMock;
    private MockedStatic<UserCredentialGenerator> userCredentialGeneratorMock;

    @BeforeEach
    void setUp() {
        authenticationMock = mockStatic(Authentication.class);
        userCredentialGeneratorMock = mockStatic(UserCredentialGenerator.class);
    }

    @org.junit.jupiter.api.AfterEach
    void tearDown() {
        authenticationMock.close();
        userCredentialGeneratorMock.close();
    }

    @Test
    void testCreateTrainee_Success() {
        Trainee newTrainee = new Trainee();
        newTrainee.setFirstName("John");
        newTrainee.setLastName("Doe");

        userCredentialGeneratorMock.when(() ->
                UserCredentialGenerator.generateUserCredentials(eq(newTrainee), any())
        ).thenAnswer((Answer<Void>) invocation -> {
            newTrainee.setUsername("John.Doe");
            newTrainee.setPassword("randomPass123");
            return null;
        });

        when(traineeRepository.save(any(Trainee.class))).thenAnswer(inv -> {
            Trainee t = inv.getArgument(0);
            t.setId(100L);
            return t;
        });

        Trainee created = traineeService.createTrainee(newTrainee);

        assertNotNull(created.getId());
        assertEquals("John.Doe", created.getUsername());
        assertEquals("randomPass123", created.getPassword());

        verify(traineeRepository).save(newTrainee);
        authenticationMock.verifyNoInteractions();
    }

    @Test
    void testUpdateTrainee_Success() {
        Trainee existing = new Trainee();
        existing.setId(200L);
        existing.setUsername("Jane.Smith");
        existing.setPassword("oldPass");
        existing.setIsActive(true);

        authenticationMock.when(() ->
                Authentication.authenticateUser(eq("Jane.Smith"), eq("oldPass"), any())
        ).thenReturn(existing);

        when(traineeRepository.save(any(Trainee.class))).thenAnswer(inv -> inv.getArgument(0));

        existing.setAddress("New Address");

        Trainee updated = traineeService.updateTrainee(existing);

        assertEquals("New Address", updated.getAddress());
        verify(traineeRepository).save(existing);
        authenticationMock.verify(() ->
                Authentication.authenticateUser(eq("Jane.Smith"), eq("oldPass"), any()));
    }

    @Test
    void testUpdateTrainee_AuthFails() {
        Trainee existing = new Trainee();
        existing.setUsername("BadUser");
        existing.setPassword("wrongPass");

        authenticationMock.when(() ->
                Authentication.authenticateUser(eq("BadUser"), eq("wrongPass"), any())
        ).thenThrow(new RuntimeException("Authentication failed"));

        assertThrows(RuntimeException.class, () -> traineeService.updateTrainee(existing));
        verify(traineeRepository, never()).save(any());
    }

    @Test
    void testGetTrainee_Success() {
        Trainee t = new Trainee();
        t.setId(300L);
        t.setUsername("johnny");
        when(traineeRepository.findById(300L)).thenReturn(Optional.of(t));

        Trainee found = traineeService.getTrainee(300L);

        assertNotNull(found);
        assertEquals(300L, found.getId());
        verify(traineeRepository).findById(300L);
    }

    @Test
    void testGetTrainee_NotFound() {
        when(traineeRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> traineeService.getTrainee(999L));
    }

    @Test
    void testGetByUsername_Success() {
        Trainee t = new Trainee();
        t.setId(400L);
        t.setUsername("coolUser");
        when(traineeRepository.findByUsername("coolUser")).thenReturn(Optional.of(t));

        Trainee found = traineeService.getByUsername("coolUser");
        assertEquals("coolUser", found.getUsername());
        verify(traineeRepository).findByUsername("coolUser");
    }

    @Test
    void testGetByUsername_NotFound() {
        when(traineeRepository.findByUsername("unknown")).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> traineeService.getByUsername("unknown"));
    }

    @Test
    void testChangePassword_Success() {
        Trainee t = new Trainee();
        t.setUsername("John.Doe3");
        t.setPassword("zxcvbnmasd");

        authenticationMock.when(() ->
                Authentication.authenticateUser(eq("John.Doe3"), eq("zxcvbnmasd"), any())
        ).thenReturn(t);

        userCredentialGeneratorMock.when(() ->
                UserCredentialGenerator.checkNewPassword("newPass123")
        ).thenAnswer(invocation -> null); // no exception => pass

        when(traineeRepository.save(any(Trainee.class))).thenAnswer(inv -> inv.getArgument(0));

        traineeService.changePassword("John.Doe3", "zxcvbnmasd", "newPass123");

        // Then
        assertEquals("newPass123", t.getPassword());
        verify(traineeRepository).save(t);
    }

    @Test
    void testChangePassword_WeakPassword() {
        // Auth success
        Trainee t = new Trainee();
        t.setUsername("abc");
        t.setPassword("abc123");
        authenticationMock.when(() ->
                Authentication.authenticateUser("abc", "abc34", traineeRepository::findByUsername)
        ).thenReturn(t);

        userCredentialGeneratorMock.when(() ->
                UserCredentialGenerator.checkNewPassword("short")
        ).thenThrow(new IllegalArgumentException("Password must be 10+ chars"));

        assertThrows(IllegalArgumentException.class, () ->
                traineeService.changePassword("abc", "abc123", "short"));

        verify(traineeRepository, never()).save(any());
    }

    @Test
    void testToggleActive_Success() {
        Trainee t = new Trainee();
        t.setUsername("John.Doe3");
        t.setPassword("zxcvbnmasd");
        t.setIsActive(true);

        authenticationMock.when(() ->
                Authentication.authenticateUser(eq("John.Doe3"), eq("zxcvbnmasd"), any())
        ).thenReturn(t);

        when(traineeRepository.save(t)).thenReturn(t);

        traineeService.toggleActive("John.Doe3", "zxcvbnmasd");
        assertFalse(t.getIsActive(), "Should have toggled from true to false");
        verify(traineeRepository).save(t);
    }

    @Test
    void testDeleteTraineeById_Success() {
        Trainee t = new Trainee();
        t.setId(500L);
        t.setUsername("deleteMe");
        t.setPassword("delPass");

        authenticationMock.when(() ->
                Authentication.authenticateUser("deleteMe", "delPass", traineeRepository::findByUsername)
        ).thenReturn(t);

        doNothing().when(traineeRepository).delete(t);

        traineeService.deleteTraineeById(t);

        verify(traineeRepository).delete(t);
    }


    @Test
    void testDeleteTraineeByUsername_Success() {
        Trainee t = new Trainee();
        t.setId(501L);
        t.setUsername("delByUsername");
        t.setPassword("p123");

        authenticationMock.when(() ->
                Authentication.authenticateUser(eq("delByUsername"), eq("p123"), any())
        ).thenReturn(t);

        traineeService.deleteTraineeByUsername("delByUsername", "p123");

        verify(traineeRepository).delete(t);
    }

    @Test
    void testDeleteTraineeByUsername_NotFound() {
        authenticationMock.when(() ->
                Authentication.authenticateUser(eq("delByUsername"), eq("p123"), any())
        ).thenThrow(new RuntimeException("User not found"));

        assertThrows(RuntimeException.class, () ->
                traineeService.deleteTraineeByUsername("delByUsername", "p123"));
    }

    @Test
    void testGetTrainersNotAssigned_Success() {
        Trainee t = new Trainee();
        t.setUsername("traineeUser");
        t.setPassword("p");
        t.setIsActive(true);
        t.setTrainers(new HashSet<>());

        authenticationMock.when(() ->
                Authentication.authenticateUser("traineeUser", "p", traineeRepository::findByUsername)
        ).thenReturn(t);

        Trainer trainer1 = new Trainer();
        trainer1.setUsername("trainer1");
        Trainer trainer2 = new Trainer();
        trainer2.setUsername("trainer2");
        Trainer trainer3 = new Trainer();
        trainer3.setUsername("trainer3");

        when(trainerRepository.findAllTrainersNotAssigned(t.getUsername())).thenReturn(Arrays.asList(trainer1, trainer2, trainer3));

        List<Trainer> result = traineeService.getTrainersNotAssigned("traineeUser", "p");

        assertEquals(3, result.size());
    }

    @Test
    void testGetTrainersNotAssigned_AlreadyHasSomeAssigned() {
        Trainee t = new Trainee();
        t.setUsername("John.Doe3");
        t.setPassword("zxcvbnmasd");
        Trainer assigned = new Trainer();
        assigned.setUsername("trainer1");
        t.setTrainers(new HashSet<>(Collections.singletonList(assigned)));

        authenticationMock.when(() ->
                Authentication.authenticateUser(eq("John.Doe3"), eq("zxcvbnmasd"), any())
        ).thenReturn(t);

        Trainer trainer2 = new Trainer();
        trainer2.setUsername("trainer2");
        Trainer trainer3 = new Trainer();
        trainer3.setUsername("trainer3");
        when(trainerRepository.findAllTrainersNotAssigned(t.getUsername())).thenReturn(Arrays.asList(trainer2, trainer3));

        List<Trainer> result = traineeService.getTrainersNotAssigned("John.Doe3", "zxcvbnmasd");

        assertEquals(2, result.size());
    }


    @Test
    void testUpdateTrainersList_Success() {
        Trainee t = new Trainee();
        t.setUsername("John.Doe3");
        t.setPassword("zxcvbnmasd");
        t.setTrainers(new HashSet<>());

        authenticationMock.when(() ->
                Authentication.authenticateUser(eq("John.Doe3"), eq("zxcvbnmasd"), any())
        ).thenReturn(t);

        Trainer trainerA = new Trainer();
        trainerA.setId(10L);
        trainerA.setUsername("A");
        Trainer trainerB = new Trainer();
        trainerB.setId(20L);
        trainerB.setUsername("B");
        List<Trainer> foundTrainers = Arrays.asList(trainerA, trainerB);

        when(trainerRepository.findAllById(anyList())).thenReturn(foundTrainers);

        doAnswer(inv -> inv.getArgument(0)).when(traineeRepository).save(any(Trainee.class));

        traineeService.updateTrainersList("John.Doe3", "zxcvbnmasd", Arrays.asList(10L, 20L));

        assertEquals(2, t.getTrainers().size());
        verify(trainerRepository).findAllById(Arrays.asList(10L, 20L));
        verify(traineeRepository).save(t);
    }
}
