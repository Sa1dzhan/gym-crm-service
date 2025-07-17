package com.gymcrm.integration.service;

import com.gymcrm.converter.TraineeMapper;
import com.gymcrm.converter.TrainerMapper;
import com.gymcrm.dao.TraineeRepository;
import com.gymcrm.dto.trainee.TraineeNotAssignedTrainersDto;
import com.gymcrm.dto.trainee.TraineeUpdateRequestDto;
import com.gymcrm.metrics.UserMetrics;
import com.gymcrm.model.Trainee;
import com.gymcrm.model.Trainer;
import com.gymcrm.model.TrainingType;
import com.gymcrm.service.TraineeService;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest
@Transactional
public class TraineeServiceIntegrTest {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private TraineeService traineeService;

    @Autowired
    private TraineeRepository traineeRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @MockBean
    private TraineeMapper traineeMapper;
    @MockBean
    private TrainerMapper trainerMapper;
    @MockBean
    private UserMetrics userMetrics;

    private Trainee existingTrainee;
    private Trainer trainer1;
    private Trainer trainer2;

    @BeforeEach
    void setUp() {
        entityManager.createQuery("DELETE FROM Training").executeUpdate();
        entityManager.createQuery("DELETE FROM Trainee").executeUpdate();
        entityManager.createQuery("DELETE FROM Trainer").executeUpdate();
        entityManager.createQuery("DELETE FROM TrainingType").executeUpdate();

        TrainingType specialization = new TrainingType();
        specialization.setTrainingTypeName("General");
        entityManager.persist(specialization);

        existingTrainee = new Trainee();
        existingTrainee.setUsername("test.trainee");
        existingTrainee.setFirstName("Test");
        existingTrainee.setLastName("Trainee");
        existingTrainee.setDateOfBirth(LocalDate.of(2000, 1, 1));
        existingTrainee.setPassword(passwordEncoder.encode("password123"));
        existingTrainee.setIsActive(true);
        entityManager.persist(existingTrainee);

        trainer1 = new Trainer();
        trainer1.setUsername("trainer.one");
        trainer1.setFirstName("Trainer");
        trainer1.setLastName("One");
        trainer1.setIsActive(true);
        trainer1.setPassword("password");
        trainer1.setSpecialization(specialization);
        entityManager.persist(trainer1);

        trainer2 = new Trainer();
        trainer2.setUsername("trainer.two");
        trainer2.setFirstName("Trainer");
        trainer2.setLastName("Two");
        trainer2.setIsActive(true);
        trainer2.setPassword("password");
        trainer2.setSpecialization(specialization);
        entityManager.persist(trainer2);

        entityManager.flush();
    }

    @Test
    void updateTrainee() {
        TraineeUpdateRequestDto updateDto = new TraineeUpdateRequestDto();
        updateDto.setUsername("test.trainee");
        updateDto.setFirstName("Updated");
        updateDto.setLastName("User");

        traineeService.updateTrainee(updateDto);

        entityManager.flush();
        entityManager.clear();

        Trainee updated = traineeRepository.findByUsername("test.trainee").get();
        assertThat(updated.getFirstName()).isEqualTo("Updated");
        assertThat(updated.getLastName()).isEqualTo("User");
        verify(userMetrics, times(1)).incrementUserProfileUpdate();
    }

    @Test
    void deleteTraineeByUsername_withValidPassword() {
        traineeService.deleteTraineeByUsername("test.trainee", "password123");

        entityManager.flush();
        entityManager.clear();

        assertThat(traineeRepository.findByUsername("test.trainee")).isEmpty();
    }

    @Test
    void deleteTraineeByUsername_withInvalidPassword() {
        assertThatThrownBy(() -> traineeService.deleteTraineeByUsername("test.trainee", "wrongPassword"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Invalid credentials");
    }

    @Test
    void getTrainersNotAssigned() {
        existingTrainee.getTrainers().add(trainer1);
        entityManager.persist(existingTrainee);
        entityManager.flush();

        TraineeNotAssignedTrainersDto result = traineeService.getTrainersNotAssigned("test.trainee");

        assertThat(result.getTrainers()).hasSize(1);
    }

    @Test
    void updateTrainersList() {
        List<String> trainerUsernames = Arrays.asList("trainer.one", "trainer.two");
        traineeService.updateTrainersList("test.trainee", trainerUsernames);

        entityManager.flush();
        entityManager.clear();

        Trainee updated = traineeRepository.findByUsername("test.trainee").get();
        assertThat(updated.getTrainers()).hasSize(2);
        assertThat(updated.getTrainers()).extracting(Trainer::getUsername).containsExactlyInAnyOrder("trainer.one", "trainer.two");
    }
}
