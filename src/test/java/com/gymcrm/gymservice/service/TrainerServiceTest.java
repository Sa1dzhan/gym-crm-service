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

    @Mock
    private TraineeMapper traineeMapper;

    @Mock
    private TrainerMapper trainerMapper;

    @Mock
    private TrainingTypeRepository trainingTypeRepository;

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
    }

    @AfterEach
    void tearDown() {
        authenticationMock.close();
        userCredentialGeneratorMock.close();
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
        when(trainingTypeRepository.findById(1L)).thenReturn(java.util.Optional.of(trainingType));

        when(trainerMapper.toEntity(any(TrainerCreateRequestDto.class))).thenReturn(trainerEntity);

        userCredentialGeneratorMock.when(() ->
                UserCredentialGenerator.generateUserCredentials(eq(trainerEntity), any())
        ).thenAnswer(invocation -> {
            trainerEntity.setUsername("Alice.Smith");
            trainerEntity.setPassword("randomPass123");
            return null;
        });

        when(trainerRepository.save(any(Trainer.class))).thenAnswer(inv -> {
            Trainer t = inv.getArgument(0);
            t.setId(10L);
            return t;
        });

        UserCreatedResponseDto authDto = new UserCreatedResponseDto();
        authDto.setUsername("Alice.Smith");

        when(trainerMapper.toRegisteredDto(any(Trainer.class))).thenReturn(authDto);

        UserCreatedResponseDto created = trainerService.createTrainer(createDto);

        assertEquals("Alice.Smith", created.getUsername());
        verify(trainerRepository).save(trainerEntity);
        authenticationMock.verifyNoInteractions();
    }

    @Test
    void testUpdateTrainer_Success() {
        TrainerUpdateRequestDto updateDto = new TrainerUpdateRequestDto();
        updateDto.setUsername("john.doe");
        updateDto.setPassword("secret");
        updateDto.setFirstName("John");
        updateDto.setLastName("Doe");
        updateDto.setSpecializationId(1L);
        updateDto.setIsActive(true);

        Trainer trainerEntity = new Trainer();
        trainerEntity.setUsername("john.doe");
        trainerEntity.setPassword("secret");
        trainerEntity.setFirstName("John");
        trainerEntity.setLastName("Doe");
        trainerEntity.setIsActive(true);

        TrainingType trainingType = new TrainingType();
        trainingType.setId(1L);
        when(trainingTypeRepository.findById(1L)).thenReturn(java.util.Optional.of(trainingType));

        when(Authentication.authenticateUser(eq("john.doe"), eq("secret"), any())).thenReturn(trainerEntity);
        when(trainerRepository.save(any(Trainer.class))).thenReturn(trainerEntity);

        TrainerProfileResponseDto profile = new TrainerProfileResponseDto();
        profile.setUsername("john.doe");
        profile.setFirstName("John");
        profile.setLastName("Doe");
        when(trainerMapper.toProfileDTO(any(Trainer.class))).thenReturn(profile);

        TrainerProfileResponseDto updated = trainerService.updateTrainer(updateDto);

        assertEquals("john.doe", updated.getUsername());
        assertEquals("John", updated.getFirstName());
        assertEquals("Doe", updated.getLastName());
        verify(trainerRepository).save(trainerEntity);
    }

    @Test
    void testGetTrainer_Success() {
        Trainer trainerEntity = new Trainer();
        trainerEntity.setId(30L);
        trainerEntity.setUsername("Alice.Smith");

        when(trainerRepository.findById(30L)).thenReturn(Optional.of(trainerEntity));

        TrainerProfileResponseDto profileDto = new TrainerProfileResponseDto();
        profileDto.setId(30L);
        profileDto.setUsername("Alice.Smith");

        when(trainerMapper.toProfileDTO(any(Trainer.class))).thenReturn(profileDto);

        TrainerProfileResponseDto found = trainerService.getTrainer(30L);

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
        String username = "Alice.Smith";
        String password = "secretPass";
        Trainer trainerEntity = new Trainer();
        trainerEntity.setUsername(username);
        trainerEntity.setPassword(password);

        authenticationMock.when(() ->
                Authentication.authenticateUser(eq(username), eq(password), any())
        ).thenReturn(trainerEntity);

        TrainerProfileResponseDto profileDto = new TrainerProfileResponseDto();
        profileDto.setUsername(username);

        when(trainerMapper.toProfileDTO(trainerEntity)).thenReturn(profileDto);

        TrainerProfileResponseDto found = trainerService.getByUsername(username, password);
        assertEquals(username, found.getUsername());
        // Repository interactions occur inside the static method, so no direct verification here.
    }

    @Test
    void testGetByUsername_NotFound() {
        String username = "NoUser";
        String password = "anyPass";

        authenticationMock.when(() ->
                Authentication.authenticateUser(eq(username), eq(password), any())
        ).thenThrow(new RuntimeException("Trainer not found"));

        assertThrows(RuntimeException.class, () -> trainerService.getByUsername(username, password));
    }

    @Test
    void testChangePassword_Success() {
        Trainer trainerEntity = new Trainer();
        trainerEntity.setUsername("TrainerUser");
        trainerEntity.setPassword("oldPass");

        authenticationMock.when(() ->
                Authentication.authenticateUser(eq("TrainerUser"), eq("oldPass"), any())
        ).thenReturn(trainerEntity);

        // For a successful password change, let checkNewPassword execute normally (void method does nothing)
        when(trainerRepository.save(any(Trainer.class))).thenAnswer(inv -> inv.getArgument(0));

        trainerService.changePassword("TrainerUser", "oldPass", "newPass123");
        assertEquals("newPass123", trainerEntity.getPassword());
        verify(trainerRepository).save(trainerEntity);
    }

    @Test
    void testChangePassword_WeakPassword() {
        Trainer trainerEntity = new Trainer();
        trainerEntity.setUsername("abc");
        trainerEntity.setPassword("abc123");

        authenticationMock.when(() ->
                Authentication.authenticateUser(eq("abc"), eq("abc123"), any())
        ).thenReturn(trainerEntity);

        userCredentialGeneratorMock.when(() ->
                UserCredentialGenerator.checkNewPassword("short")
        ).thenThrow(new IllegalArgumentException("Password must be 10+ chars"));

        assertThrows(IllegalArgumentException.class, () ->
                trainerService.changePassword("abc", "abc123", "short"));

        verify(trainerRepository, never()).save(any());
    }

    @Test
    void testToggleActive_Success() {
        Trainer trainerEntity = new Trainer();
        trainerEntity.setUsername("Alice.Smith");
        trainerEntity.setPassword("randomPass123");
        trainerEntity.setIsActive(true);

        authenticationMock.when(() ->
                Authentication.authenticateUser(eq("Alice.Smith"), eq("randomPass123"), any())
        ).thenReturn(trainerEntity);

        when(trainerRepository.save(trainerEntity)).thenReturn(trainerEntity);

        trainerService.toggleActive("Alice.Smith", "randomPass123");
        assertFalse(trainerEntity.getIsActive());
        verify(trainerRepository).save(trainerEntity);
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
