package com.gymcrm.gymservice.service;

import com.gymcrm.converter.TraineeMapper;
import com.gymcrm.converter.TrainerMapper;
import com.gymcrm.dao.TraineeRepository;
import com.gymcrm.dao.TrainerRepository;
import com.gymcrm.dto.UserCreatedResponseDto;
import com.gymcrm.dto.trainee.TraineeCreateRequestDto;
import com.gymcrm.dto.trainee.TraineeNotAssignedTrainersDto;
import com.gymcrm.dto.trainee.TraineeProfileResponseDto;
import com.gymcrm.dto.trainee.TraineeUpdateRequestDto;
import com.gymcrm.dto.trainer.TrainerShortProfileDto;
import com.gymcrm.metrics.UserMetrics;
import com.gymcrm.model.Trainee;
import com.gymcrm.model.Trainer;
import com.gymcrm.service.impl.TraineeServiceImpl;
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
import org.mockito.stubbing.Answer;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TraineeServiceTest {

    @Mock
    private TraineeRepository traineeRepository;

    @Mock
    private TrainerRepository trainerRepository;

    @Mock
    private TraineeMapper traineeMapper;

    @Mock
    private TrainerMapper trainerMapper;

    @Mock
    private UserMetrics userMetrics;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private TraineeServiceImpl traineeService;

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
    void testCreateTrainee_Success() {
        TraineeCreateRequestDto createDto = new TraineeCreateRequestDto();
        createDto.setFirstName("John");
        createDto.setLastName("Doe");

        Trainee traineeEntity = new Trainee();
        traineeEntity.setFirstName("John");
        traineeEntity.setLastName("Doe");

        when(traineeMapper.toEntity(any(TraineeCreateRequestDto.class))).thenReturn(traineeEntity);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");

        userCredentialGeneratorMock.when(() ->
                UserCredentialGenerator.generateUserCredentials(eq(traineeEntity), any())
        ).thenAnswer((Answer<Void>) invocation -> {
            traineeEntity.setUsername("John.Doe");
            traineeEntity.setPassword("randomPass123");
            return null;
        });

        when(traineeRepository.save(any(Trainee.class))).thenAnswer(inv -> {
            Trainee t = inv.getArgument(0);
            t.setId(100L);
            return t;
        });

        UserCreatedResponseDto authDto = new UserCreatedResponseDto();
        authDto.setUsername("John.Doe");

        UserCreatedResponseDto result = traineeService.createTrainee(createDto);

        assertEquals("John.Doe", result.getUsername());
        verify(traineeRepository).save(traineeEntity);
        authenticationMock.verifyNoInteractions();
    }

    @Test
    void testUpdateTrainee_Success() {
        TraineeUpdateRequestDto updateDto = new TraineeUpdateRequestDto();
        updateDto.setUsername("Jane.Smith");
        updateDto.setPassword("oldPass");
        updateDto.setAddress("New Address");

        Trainee existingTrainee = new Trainee();
        existingTrainee.setUsername("Jane.Smith");
        existingTrainee.setPassword("oldPass");
        existingTrainee.setIsActive(true);

        when(traineeRepository.findByUsername("Jane.Smith")).thenReturn(Optional.of(existingTrainee));

        Trainee updatedTrainee = new Trainee();
        updatedTrainee.setUsername("Jane.Smith");
        updatedTrainee.setPassword("oldPass");
        updatedTrainee.setAddress("New Address");
        updatedTrainee.setIsActive(true);

        when(traineeRepository.save(any(Trainee.class))).thenReturn(updatedTrainee);

        TraineeProfileResponseDto profileDto = new TraineeProfileResponseDto();
        profileDto.setUsername("Jane.Smith");
        profileDto.setAddress("New Address");

        when(traineeMapper.toProfileDTO(any(Trainee.class))).thenReturn(profileDto);

        TraineeProfileResponseDto result = traineeService.updateTrainee(updateDto);

        assertEquals("New Address", result.getAddress());
        verify(traineeRepository).save(any(Trainee.class));
    }

    @Test
    void testUpdateTrainee_AuthFails() {
        TraineeUpdateRequestDto updateDto = new TraineeUpdateRequestDto();
        updateDto.setUsername("BadUser");
        updateDto.setPassword("wrongPass");

        when(traineeRepository.findByUsername("BadUser")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> traineeService.updateTrainee(updateDto));
        verify(traineeRepository, never()).save(any());
    }

    @Test
    void testGetByUsername_Success() {
        String username = "coolUser";

        Trainee traineeEntity = new Trainee();
        traineeEntity.setUsername(username);

        when(traineeRepository.findByUsername(username)).thenReturn(Optional.of(traineeEntity));

        TraineeProfileResponseDto profileDto = new TraineeProfileResponseDto();
        profileDto.setUsername(username);

        when(traineeMapper.toProfileDTO(traineeEntity)).thenReturn(profileDto);

        TraineeProfileResponseDto result = traineeService.getByUsername(username);
        assertEquals(username, result.getUsername());
    }

    @Test
    void testGetByUsername_AuthFails() {
        String username = "unknown";

        when(traineeRepository.findByUsername(username)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> traineeService.getByUsername(username));
    }

    @Test
    void testChangePassword_Success() {
        String username = "John.Doe3";
        String oldPassword = "zxcvbnmasd";
        String newPassword = "newPass123";

        Trainee traineeEntity = new Trainee();
        traineeEntity.setUsername(username);
        traineeEntity.setPassword("encodedOldPassword");

        when(traineeRepository.findByUsername(username)).thenReturn(Optional.of(traineeEntity));
        when(passwordEncoder.matches(oldPassword, traineeEntity.getPassword())).thenReturn(true);
        when(passwordEncoder.encode(newPassword)).thenReturn("encodedNewPassword");

        traineeService.changePassword(username, oldPassword, newPassword);

        verify(traineeRepository).save(traineeEntity);
    }

    @Test
    void testChangePassword_WeakPassword() {
        String username = "abc";
        String oldPassword = "abc123";
        String weakPassword = "short";

        Trainee traineeEntity = new Trainee();
        traineeEntity.setUsername(username);
        traineeEntity.setPassword("encodedPassword");

        when(traineeRepository.findByUsername(username)).thenReturn(Optional.of(traineeEntity));
        when(passwordEncoder.matches(oldPassword, traineeEntity.getPassword())).thenReturn(true);

        userCredentialGeneratorMock.when(() ->
                UserCredentialGenerator.checkNewPassword(weakPassword)
        ).thenThrow(new IllegalArgumentException("Password must be 10+ chars"));

        assertThrows(IllegalArgumentException.class, () ->
                traineeService.changePassword(username, oldPassword, weakPassword));

        verify(traineeRepository, never()).save(any());
    }

    @Test
    void testToggleActive_Success() {
        String username = "John.Doe3";

        Trainee traineeEntity = new Trainee();
        traineeEntity.setUsername(username);
        traineeEntity.setIsActive(true);

        when(traineeRepository.findByUsername(username)).thenReturn(Optional.of(traineeEntity));
        when(traineeRepository.save(traineeEntity)).thenReturn(traineeEntity);

        traineeService.toggleActive(username);
        assertFalse(traineeEntity.getIsActive(), "Should have toggled from true to false");
        verify(traineeRepository).save(traineeEntity);
    }

    @Test
    void testDeleteTraineeByUsername_Success() {
        String username = "delByUsername";
        String password = "p123";

        Trainee traineeEntity = new Trainee();
        traineeEntity.setId(501L);
        traineeEntity.setUsername(username);
        traineeEntity.setPassword("encodedPassword");

        when(traineeRepository.findByUsername(username)).thenReturn(Optional.of(traineeEntity));
        when(passwordEncoder.matches(password, traineeEntity.getPassword())).thenReturn(true);

        traineeService.deleteTraineeByUsername(username, password);

        verify(traineeRepository).delete(traineeEntity);
    }

    @Test
    void testDeleteTraineeByUsername_NotFound() {
        String username = "delByUsername";
        String password = "p123";

        when(traineeRepository.findByUsername(username)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () ->
                traineeService.deleteTraineeByUsername(username, password));
    }

    @Test
    void testGetTrainersNotAssigned_Success() {
        String username = "traineeUser";

        Trainee traineeEntity = new Trainee();
        traineeEntity.setUsername(username);
        traineeEntity.setIsActive(true);

        when(traineeRepository.findByUsername(username)).thenReturn(Optional.of(traineeEntity));

        Trainer trainer1 = new Trainer();
        trainer1.setUsername("trainer1");
        Trainer trainer2 = new Trainer();
        trainer2.setUsername("trainer2");
        Trainer trainer3 = new Trainer();
        trainer3.setUsername("trainer3");

        List<Trainer> trainersList = Arrays.asList(trainer1, trainer2, trainer3);
        when(trainerRepository.findAllTrainersNotAssigned(username)).thenReturn(trainersList);

        TrainerShortProfileDto dto1 = new TrainerShortProfileDto();
        dto1.setUsername("trainer1");
        TrainerShortProfileDto dto2 = new TrainerShortProfileDto();
        dto2.setUsername("trainer2");
        TrainerShortProfileDto dto3 = new TrainerShortProfileDto();
        dto3.setUsername("trainer3");

        when(trainerMapper.toShortProfileDto(trainer1)).thenReturn(dto1);
        when(trainerMapper.toShortProfileDto(trainer2)).thenReturn(dto2);
        when(trainerMapper.toShortProfileDto(trainer3)).thenReturn(dto3);

        TraineeNotAssignedTrainersDto result = traineeService.getTrainersNotAssigned(username);
        assertEquals(3, result.getTrainers().size());
    }

    @Test
    void testUpdateTrainersList_Success() {
        String username = "John.Doe3";

        Trainee traineeEntity = new Trainee();
        traineeEntity.setUsername(username);
        traineeEntity.setTrainers(new HashSet<>());

        when(traineeRepository.findByUsername(username)).thenReturn(Optional.of(traineeEntity));

        List<String> trainerUsernames = Arrays.asList("A", "B");

        Trainer trainerA = new Trainer();
        trainerA.setUsername("A");
        Trainer trainerB = new Trainer();
        trainerB.setUsername("B");

        List<Trainer> trainersList = Arrays.asList(trainerA, trainerB);
        when(trainerRepository.findAllByUsername(trainerUsernames)).thenReturn(trainersList);

        when(traineeRepository.save(any(Trainee.class))).thenAnswer(inv -> inv.getArgument(0));

        TrainerShortProfileDto dtoA = new TrainerShortProfileDto();
        dtoA.setUsername("A");
        TrainerShortProfileDto dtoB = new TrainerShortProfileDto();
        dtoB.setUsername("B");

        when(trainerMapper.toShortProfileDto(trainerA)).thenReturn(dtoA);
        when(trainerMapper.toShortProfileDto(trainerB)).thenReturn(dtoB);

        List<TrainerShortProfileDto> result = traineeService.updateTrainersList(username, trainerUsernames);

        assertEquals(2, result.size());
        verify(trainerRepository).findAllByUsername(trainerUsernames);
        verify(traineeRepository).save(traineeEntity);
    }
}
