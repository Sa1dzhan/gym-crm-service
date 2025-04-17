package com.gymcrm.gymservice.service;

import com.gymcrm.converter.TraineeMapper;
import com.gymcrm.converter.TrainerMapper;
import com.gymcrm.dao.TrainerRepository;
import com.gymcrm.dao.TrainingTypeRepository;
import com.gymcrm.dto.UserCreatedResponseDto;
import com.gymcrm.dto.trainer.TrainerCreateRequestDto;
import com.gymcrm.dto.trainer.TrainerProfileResponseDto;
import com.gymcrm.dto.trainer.TrainerUpdateRequestDto;
import com.gymcrm.metrics.UserMetrics;
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
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainerServiceTest {

    @Mock
    private TrainerRepository trainerRepository;

    @Mock
    private TraineeMapper traineeMapper;

    @Mock
    private TrainerMapper trainerMapper;

    @Mock
    private TrainingTypeRepository trainingTypeRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserMetrics userMetrics;

    @InjectMocks
    private TrainerServiceImpl trainerService;

    private MockedStatic<Authentication> authenticationMock;
    private MockedStatic<UserCredentialGenerator> userCredentialGeneratorMock;

    @BeforeEach
    void setUp() {
        authenticationMock = mockStatic(Authentication.class);
        userCredentialGeneratorMock = mockStatic(UserCredentialGenerator.class);

        lenient().when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        lenient().when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);

        lenient().doNothing().when(userMetrics).incrementUserRegistration();
        lenient().doNothing().when(userMetrics).incrementUserLogin();
        lenient().doNothing().when(userMetrics).incrementUserProfileUpdate();
    }

    @AfterEach
    void tearDown() {
        if (authenticationMock != null) {
            authenticationMock.close();
        }
        if (userCredentialGeneratorMock != null) {
            userCredentialGeneratorMock.close();
        }
    }

    @Test
    void testCreateTrainer_Success() {
        TrainerCreateRequestDto createDto = new TrainerCreateRequestDto();
        createDto.setFirstName("Alice");
        createDto.setLastName("Smith");
        createDto.setSpecializationId(1L);

        Trainer trainerEntity = new Trainer();
        trainerEntity.setFirstName("Alice");
        trainerEntity.setLastName("Smith");

        TrainingType trainingType = new TrainingType();
        trainingType.setId(1L);
        when(trainingTypeRepository.findById(1L)).thenReturn(Optional.of(trainingType));

        when(trainerMapper.toEntity(any(TrainerCreateRequestDto.class))).thenReturn(trainerEntity);

        // Mock the static method properly
        userCredentialGeneratorMock.when(() -> UserCredentialGenerator.generateUserCredentials(eq(trainerEntity), any(Predicate.class)))
                .then(invocation -> {
                    Trainer t = invocation.getArgument(0);
                    t.setUsername("Alice.Smith");
                    t.setPassword("randomPass123");
                    return null;
                });

        userCredentialGeneratorMock.when(() -> UserCredentialGenerator.checkNewPassword(anyString()))
                .thenAnswer(invocation -> null);

        when(passwordEncoder.encode("randomPass123")).thenReturn("encodedRandomPass");

        when(trainerRepository.save(any(Trainer.class))).thenReturn(trainerEntity);

        UserCreatedResponseDto responseDto = new UserCreatedResponseDto();
        responseDto.setUsername("Alice.Smith");
        responseDto.setPassword("randomPass123");

        UserCreatedResponseDto created = trainerService.createTrainer(createDto);

        assertNotNull(created);
        assertEquals("Alice.Smith", created.getUsername());
        assertEquals("randomPass123", created.getPassword());
        verify(trainerRepository).save(any(Trainer.class));
    }

    @Test
    void testUpdateTrainer_Success() {
        TrainerUpdateRequestDto updateDto = new TrainerUpdateRequestDto();
        updateDto.setUsername("john.doe");
        updateDto.setFirstName("John");
        updateDto.setLastName("Doe");
        updateDto.setSpecializationId(1L);
        updateDto.setIsActive(true);

        Trainer existingTrainer = new Trainer();
        existingTrainer.setUsername("john.doe");
        existingTrainer.setFirstName("OldJohn");
        existingTrainer.setLastName("OldDoe");
        existingTrainer.setIsActive(true);

        TrainingType trainingType = new TrainingType();
        trainingType.setId(1L);
        when(trainingTypeRepository.findById(1L)).thenReturn(Optional.of(trainingType));
        when(trainerRepository.findByUsername("john.doe")).thenReturn(Optional.of(existingTrainer));
        when(trainerRepository.save(any(Trainer.class))).thenReturn(existingTrainer);

        TrainerProfileResponseDto profileDto = new TrainerProfileResponseDto();
        profileDto.setUsername("john.doe");
        profileDto.setFirstName("John");
        profileDto.setLastName("Doe");
        when(trainerMapper.toProfileDTO(any(Trainer.class))).thenReturn(profileDto);

        TrainerProfileResponseDto updated = trainerService.updateTrainer(updateDto);

        assertNotNull(updated);
        assertEquals("john.doe", updated.getUsername());
        assertEquals("John", updated.getFirstName());
        assertEquals("Doe", updated.getLastName());
        verify(trainerRepository).save(any(Trainer.class));
    }

    @Test
    void testGetByUsername_Success() {
        String username = "Alice.Smith";

        Trainer trainerEntity = new Trainer();
        trainerEntity.setUsername(username);
        trainerEntity.setFirstName("Alice");
        trainerEntity.setLastName("Smith");

        when(trainerRepository.findByUsername(username)).thenReturn(Optional.of(trainerEntity));

        TrainerProfileResponseDto profileDto = new TrainerProfileResponseDto();
        profileDto.setUsername(username);
        profileDto.setFirstName("Alice");
        profileDto.setLastName("Smith");

        when(trainerMapper.toProfileDTO(any(Trainer.class))).thenReturn(profileDto);

        TrainerProfileResponseDto found = trainerService.getByUsername(username);

        assertEquals(username, found.getUsername());
        assertEquals("Alice", found.getFirstName());
        assertEquals("Smith", found.getLastName());
    }

    @Test
    void testGetByUsername_NotFound() {
        String username = "NoUser";

        lenient().when(trainerRepository.findByUsername(username)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> trainerService.getByUsername(username));
    }

    @Test
    void testChangePassword_Success() {
        String username = "TrainerUser";
        String oldPassword = "oldPass";
        String newPassword = "newPass123";

        Trainer trainerEntity = new Trainer();
        trainerEntity.setUsername(username);
        trainerEntity.setPassword("encodedOldPass");

        when(trainerRepository.findByUsername(username)).thenReturn(Optional.of(trainerEntity));
        when(passwordEncoder.matches(eq(oldPassword), anyString())).thenReturn(true);
        when(passwordEncoder.encode(newPassword)).thenReturn("encodedNewPass");

        userCredentialGeneratorMock.when(() -> UserCredentialGenerator.checkNewPassword(anyString()))
                .thenAnswer(invocation -> null);

        when(trainerRepository.save(any(Trainer.class))).thenReturn(trainerEntity);

        trainerService.changePassword(username, oldPassword, newPassword);

        verify(trainerRepository).save(any(Trainer.class));
        assertEquals("encodedNewPass", trainerEntity.getPassword());
    }

    @Test
    void testChangePassword_WeakPassword() {
        String username = "abc";
        String oldPassword = "abc123";
        String weakPassword = "short";

        Trainer trainerEntity = new Trainer();
        trainerEntity.setUsername(username);
        trainerEntity.setPassword("encodedOldPass");

        lenient().when(trainerRepository.findByUsername(username)).thenReturn(Optional.of(trainerEntity));
        lenient().when(passwordEncoder.matches(eq(oldPassword), anyString())).thenReturn(true);

        userCredentialGeneratorMock.when(() -> UserCredentialGenerator.checkNewPassword(weakPassword))
                .thenThrow(new RuntimeException("Password must be 10+ chars"));

        assertThrows(RuntimeException.class, () ->
                trainerService.changePassword(username, oldPassword, weakPassword));

        verify(trainerRepository, never()).save(any());
    }

    @Test
    void testToggleActive_Success() {
        String username = "Alice.Smith";

        Trainer trainerEntity = new Trainer();
        trainerEntity.setUsername(username);
        trainerEntity.setIsActive(true);

        when(trainerRepository.findByUsername(username)).thenReturn(Optional.of(trainerEntity));
        when(trainerRepository.save(any(Trainer.class))).thenReturn(trainerEntity);

        trainerService.toggleActive(username);

        assertFalse(trainerEntity.getIsActive());
        verify(trainerRepository).save(any(Trainer.class));
    }

    @Test
    void testGetAllTrainers_Success() {
        Trainer t1 = new Trainer();
        t1.setUsername("trainer1");
        Trainer t2 = new Trainer();
        t2.setUsername("trainer2");
        List<Trainer> trainers = Arrays.asList(t1, t2);

        when(trainerRepository.findAll()).thenReturn(trainers);

        List<Trainer> result = trainerService.getAllTrainers();

        assertEquals(2, result.size());
        verify(trainerRepository).findAll();
    }
}
