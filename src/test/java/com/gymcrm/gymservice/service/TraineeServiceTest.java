package com.gymcrm.gymservice.service;

import com.gymcrm.converter.Converter;
import com.gymcrm.dao.TraineeRepository;
import com.gymcrm.dao.TrainerRepository;
import com.gymcrm.dto.AuthenticatedRequestDto;
import com.gymcrm.dto.trainee.TraineeCreateRequestDto;
import com.gymcrm.dto.trainee.TraineeNotAssignedTrainersDto;
import com.gymcrm.dto.trainee.TraineeProfileResponseDto;
import com.gymcrm.dto.trainee.TraineeUpdateRequestDto;
import com.gymcrm.dto.trainer.TrainerShortProfileDto;
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
    private Converter converter;

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

        when(converter.toEntity(any(TraineeCreateRequestDto.class))).thenReturn(traineeEntity);

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

        AuthenticatedRequestDto authDto = new AuthenticatedRequestDto();
        authDto.setUsername("John.Doe");

        when(converter.toRegisteredDto(any(Trainee.class))).thenReturn(authDto);

        AuthenticatedRequestDto result = traineeService.createTrainee(createDto);

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

        authenticationMock.when(() ->
                Authentication.authenticateUser(eq("Jane.Smith"), eq("oldPass"), any())
        ).thenReturn(existingTrainee);

        Trainee updatedTrainee = new Trainee();
        updatedTrainee.setUsername("Jane.Smith");
        updatedTrainee.setPassword("oldPass");
        updatedTrainee.setAddress("New Address");
        updatedTrainee.setIsActive(true);

        when(converter.toEntity(any(TraineeUpdateRequestDto.class))).thenReturn(updatedTrainee);

        when(traineeRepository.save(any(Trainee.class))).thenReturn(updatedTrainee);

        TraineeProfileResponseDto profileDto = new TraineeProfileResponseDto();
        profileDto.setUsername("Jane.Smith");
        profileDto.setAddress("New Address");

        when(converter.toProfileDTO(any(Trainee.class))).thenReturn(profileDto);

        TraineeProfileResponseDto result = traineeService.updateTrainee(updateDto);

        assertEquals("New Address", result.getAddress());
        verify(traineeRepository).save(updatedTrainee);
        authenticationMock.verify(() ->
                Authentication.authenticateUser(eq("Jane.Smith"), eq("oldPass"), any()));
    }

    @Test
    void testUpdateTrainee_AuthFails() {
        TraineeUpdateRequestDto updateDto = new TraineeUpdateRequestDto();
        updateDto.setUsername("BadUser");
        updateDto.setPassword("wrongPass");

        authenticationMock.when(() ->
                Authentication.authenticateUser(eq("BadUser"), eq("wrongPass"), any())
        ).thenThrow(new RuntimeException("Authentication failed"));

        assertThrows(RuntimeException.class, () -> traineeService.updateTrainee(updateDto));
        verify(traineeRepository, never()).save(any());
    }

    @Test
    void testGetTrainee_Success() {
        Trainee traineeEntity = new Trainee();
        traineeEntity.setId(300L);
        traineeEntity.setUsername("johnny");

        when(traineeRepository.findById(300L)).thenReturn(Optional.of(traineeEntity));

        TraineeProfileResponseDto profileDto = new TraineeProfileResponseDto();
        profileDto.setId(300L);
        profileDto.setUsername("johnny");

        when(converter.toProfileDTO(any(Trainee.class))).thenReturn(profileDto);

        TraineeProfileResponseDto result = traineeService.getTrainee(300L);

        assertNotNull(result);
        assertEquals(300L, result.getId());
        verify(traineeRepository).findById(300L);
    }

    @Test
    void testGetTrainee_NotFound() {
        when(traineeRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> traineeService.getTrainee(999L));
    }

    @Test
    void testGetByUsername_Success() {
        String username = "coolUser";
        String password = "secret";
        Trainee traineeEntity = new Trainee();
        traineeEntity.setUsername(username);
        traineeEntity.setPassword(password);

        authenticationMock.when(() ->
                Authentication.authenticateUser(eq(username), eq(password), any())
        ).thenReturn(traineeEntity);

        TraineeProfileResponseDto profileDto = new TraineeProfileResponseDto();
        profileDto.setUsername(username);

        when(converter.toProfileDTO(traineeEntity)).thenReturn(profileDto);

        TraineeProfileResponseDto result = traineeService.getByUsername(username, password);
        assertEquals(username, result.getUsername());
    }

    @Test
    void testGetByUsername_AuthFails() {
        String username = "unknown";
        String password = "pass";
        authenticationMock.when(() ->
                Authentication.authenticateUser(eq(username), eq(password), any())
        ).thenThrow(new RuntimeException("Authentication failed"));

        assertThrows(RuntimeException.class, () -> traineeService.getByUsername(username, password));
    }

    @Test
    void testChangePassword_Success() {
        Trainee traineeEntity = new Trainee();
        traineeEntity.setUsername("John.Doe3");
        traineeEntity.setPassword("zxcvbnmasd");

        authenticationMock.when(() ->
                Authentication.authenticateUser(eq("John.Doe3"), eq("zxcvbnmasd"), any())
        ).thenReturn(traineeEntity);

        when(traineeRepository.save(any(Trainee.class))).thenAnswer(inv -> inv.getArgument(0));

        traineeService.changePassword("John.Doe3", "zxcvbnmasd", "newPass123");

        assertEquals("newPass123", traineeEntity.getPassword());
        verify(traineeRepository).save(traineeEntity);
    }

    @Test
    void testChangePassword_WeakPassword() {
        Trainee traineeEntity = new Trainee();
        traineeEntity.setUsername("abc");
        traineeEntity.setPassword("abc123");

        authenticationMock.when(() ->
                Authentication.authenticateUser(eq("abc"), eq("abc123"), any())
        ).thenReturn(traineeEntity);

        userCredentialGeneratorMock.when(() ->
                UserCredentialGenerator.checkNewPassword("short")
        ).thenThrow(new IllegalArgumentException("Password must be 10+ chars"));

        assertThrows(IllegalArgumentException.class, () ->
                traineeService.changePassword("abc", "abc123", "short"));

        verify(traineeRepository, never()).save(any());
    }

    @Test
    void testToggleActive_Success() {
        Trainee traineeEntity = new Trainee();
        traineeEntity.setUsername("John.Doe3");
        traineeEntity.setPassword("zxcvbnmasd");
        traineeEntity.setIsActive(true);

        authenticationMock.when(() ->
                Authentication.authenticateUser(eq("John.Doe3"), eq("zxcvbnmasd"), any())
        ).thenReturn(traineeEntity);

        when(traineeRepository.save(traineeEntity)).thenReturn(traineeEntity);

        traineeService.toggleActive("John.Doe3", "zxcvbnmasd");
        assertFalse(traineeEntity.getIsActive(), "Should have toggled from true to false");
        verify(traineeRepository).save(traineeEntity);
    }

    @Test
    void testDeleteTraineeByUsername_Success() {
        Trainee traineeEntity = new Trainee();
        traineeEntity.setId(501L);
        traineeEntity.setUsername("delByUsername");
        traineeEntity.setPassword("p123");

        authenticationMock.when(() ->
                Authentication.authenticateUser(eq("delByUsername"), eq("p123"), any())
        ).thenReturn(traineeEntity);

        traineeService.deleteTraineeByUsername("delByUsername", "p123");

        verify(traineeRepository).delete(traineeEntity);
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
        String username = "traineeUser";
        String password = "p";

        Trainee traineeEntity = new Trainee();
        traineeEntity.setUsername(username);
        traineeEntity.setPassword(password);
        traineeEntity.setIsActive(true);

        authenticationMock.when(() ->
                Authentication.authenticateUser(eq(username), eq(password), any())
        ).thenReturn(traineeEntity);

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

        when(converter.toShortProfileDto(trainer1)).thenReturn(dto1);
        when(converter.toShortProfileDto(trainer2)).thenReturn(dto2);
        when(converter.toShortProfileDto(trainer3)).thenReturn(dto3);

        TraineeNotAssignedTrainersDto result = traineeService.getTrainersNotAssigned(username, password);
        assertEquals(3, result.getTrainers().size());
    }

    @Test
    void testUpdateTrainersList_Success() {
        String username = "John.Doe3";
        String password = "zxcvbnmasd";
        Trainee traineeEntity = new Trainee();
        traineeEntity.setUsername(username);
        traineeEntity.setPassword(password);
        traineeEntity.setTrainers(new HashSet<>());

        authenticationMock.when(() ->
                Authentication.authenticateUser(eq(username), eq(password), any())
        ).thenReturn(traineeEntity);

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

        when(converter.toShortProfileDto(trainerA)).thenReturn(dtoA);
        when(converter.toShortProfileDto(trainerB)).thenReturn(dtoB);

        List<TrainerShortProfileDto> result = traineeService.updateTrainersList(username, password, trainerUsernames);

        assertEquals(2, result.size());
        verify(trainerRepository).findAllByUsername(trainerUsernames);
        verify(traineeRepository).save(traineeEntity);
    }
}
