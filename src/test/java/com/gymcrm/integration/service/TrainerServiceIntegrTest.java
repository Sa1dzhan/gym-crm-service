package com.gymcrm.integration.service;

import com.gymcrm.converter.TrainerMapper;
import com.gymcrm.dao.TrainerRepository;
import com.gymcrm.dto.UserCreatedResponseDto;
import com.gymcrm.dto.trainer.TrainerCreateRequestDto;
import com.gymcrm.dto.trainer.TrainerProfileResponseDto;
import com.gymcrm.dto.trainer.TrainerUpdateRequestDto;
import com.gymcrm.metrics.UserMetrics;
import com.gymcrm.model.Trainer;
import com.gymcrm.model.TrainingType;
import com.gymcrm.service.TrainerService;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@Transactional
public class TrainerServiceIntegrTest {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private TrainerService trainerService;

    @Autowired
    private TrainerRepository trainerRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @MockBean
    private TrainerMapper trainerMapper;

    @MockBean
    private UserMetrics userMetrics;

    private Trainer existingTrainer;
    private TrainingType existingSpecialization;
    private TrainingType newSpecialization;

    @BeforeEach
    void setUp() {
        entityManager.createQuery("DELETE FROM Training").executeUpdate();
        entityManager.createQuery("DELETE FROM Trainee").executeUpdate();
        entityManager.createQuery("DELETE FROM Trainer").executeUpdate();
        entityManager.createQuery("DELETE FROM TrainingType").executeUpdate();

        existingSpecialization = new TrainingType();
        existingSpecialization.setTrainingTypeName("Cardio");
        entityManager.persist(existingSpecialization);

        newSpecialization = new TrainingType();
        newSpecialization.setTrainingTypeName("Yoga");
        entityManager.persist(newSpecialization);

        existingTrainer = new Trainer();
        existingTrainer.setUsername("john.doe");
        existingTrainer.setFirstName("John");
        existingTrainer.setLastName("Doe");
        existingTrainer.setPassword(passwordEncoder.encode("password123"));
        existingTrainer.setIsActive(true);
        existingTrainer.setSpecialization(existingSpecialization);
        entityManager.persist(existingTrainer);

        entityManager.flush();
    }

    @Test
    void createTrainer() {
        TrainerCreateRequestDto createDto = new TrainerCreateRequestDto();
        createDto.setFirstName("Jane");
        createDto.setLastName("Doe");
        createDto.setSpecializationId(existingSpecialization.getId());

        when(trainerMapper.toEntity(any(TrainerCreateRequestDto.class))).then(invocation -> {
            Trainer t = new Trainer();
            t.setFirstName(createDto.getFirstName());
            t.setLastName(createDto.getLastName());
            return t;
        });

        UserCreatedResponseDto response = trainerService.createTrainer(createDto);

        assertThat(response).isNotNull();
        assertThat(response.getUsername()).isEqualTo("Jane.Doe");
        verify(userMetrics, times(1)).incrementUserRegistration();

        Trainer createdTrainer = trainerRepository.findByUsername("Jane.Doe").get();
        assertThat(createdTrainer).isNotNull();
        assertThat(createdTrainer.getFirstName()).isEqualTo("Jane");
    }

    @Test
    void login_withValidCredentials() {
        trainerService.login("john.doe", "password123");
        verify(userMetrics, times(1)).incrementUserLogin();
    }

    @Test
    void login_withInvalidCredentials() {
        assertThatThrownBy(() -> trainerService.login("john.doe", "wrongPassword"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Invalid credentials");
    }

    @Test
    void updateTrainer() {
        TrainerUpdateRequestDto updateDto = new TrainerUpdateRequestDto();
        updateDto.setUsername("john.doe");
        updateDto.setFirstName("Johnathan");
        updateDto.setLastName("Doeman");
        updateDto.setIsActive(false);
        updateDto.setSpecializationId(newSpecialization.getId());

        trainerService.updateTrainer(updateDto);

        entityManager.flush();
        entityManager.clear();

        Trainer savedTrainer = trainerRepository.findByUsername("john.doe").get();
        verify(userMetrics, times(1)).incrementUserProfileUpdate();
        assertThat(savedTrainer.getFirstName()).isEqualTo("Johnathan");
        assertThat(savedTrainer.getIsActive()).isFalse();
        assertThat(savedTrainer.getSpecialization().getTrainingTypeName()).isEqualTo("Yoga");
    }

    @Test
    void getByUsername() {
        TrainerProfileResponseDto mockDto = new TrainerProfileResponseDto();
        mockDto.setUsername(existingTrainer.getUsername());
        mockDto.setFirstName(existingTrainer.getFirstName());

        when(trainerMapper.toProfileDTO(any(Trainer.class))).thenReturn(mockDto);

        TrainerProfileResponseDto result = trainerService.getByUsername("john.doe");

        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo("john.doe");
        verify(trainerMapper, times(1)).toProfileDTO(any(Trainer.class));
    }

    @Test
    void changePassword() {
        trainerService.changePassword("john.doe", "password123", "newPassword456");

        entityManager.flush();
        entityManager.clear();

        Trainer updatedTrainer = trainerRepository.findByUsername("john.doe").get();
        assertThat(passwordEncoder.matches("newPassword456", updatedTrainer.getPassword())).isTrue();
    }

    @Test
    void toggleActive() {
        boolean initialStatus = existingTrainer.getIsActive();
        trainerService.toggleActive("john.doe");

        entityManager.flush();
        entityManager.clear();

        Trainer updatedTrainer = trainerRepository.findByUsername("john.doe").get();
        assertThat(updatedTrainer.getIsActive()).isEqualTo(!initialStatus);
    }

    @Test
    void getAllTrainers() {
        List<Trainer> trainers = trainerService.getAllTrainers();
        assertThat(trainers).hasSize(1);
        assertThat(trainers.get(0).getUsername()).isEqualTo("john.doe");
    }
}
