package com.gymcrm.integration.repository;

import com.gymcrm.dao.TrainerRepository;
import com.gymcrm.dao.impl.TraineeRepositoryImpl;
import com.gymcrm.dao.impl.TrainerRepositoryImpl;
import com.gymcrm.dao.impl.TrainingTypeRepositoryImpl;
import com.gymcrm.model.Trainee;
import com.gymcrm.model.Trainer;
import com.gymcrm.model.TrainingType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;


@DataJpaTest
@Import({TrainerRepositoryImpl.class, TraineeRepositoryImpl.class, TrainingTypeRepositoryImpl.class})
public class TrainerRepositoryIntegrTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private TrainerRepository trainerRepository;

    private TrainingType specialization1;
    private Trainer trainer1;
    private Trainer trainer2;
    private Trainer trainer3;

    @BeforeEach
    void setUp() {
        specialization1 = new TrainingType();
        specialization1.setTrainingTypeName("Cardio");
        entityManager.persist(specialization1);

        TrainingType specialization2 = new TrainingType();
        specialization2.setTrainingTypeName("Strength");
        entityManager.persist(specialization2);

        trainer1 = new Trainer();
        trainer1.setUsername("john.doe");
        trainer1.setFirstName("John");
        trainer1.setLastName("Doe");
        trainer1.setPassword("pass");
        trainer1.setIsActive(true);
        trainer1.setSpecialization(specialization1);

        trainer2 = new Trainer();
        trainer2.setUsername("jane.smith");
        trainer2.setFirstName("Jane");
        trainer2.setLastName("Smith");
        trainer2.setPassword("pass");
        trainer2.setIsActive(true);
        trainer2.setSpecialization(specialization2);

        trainer3 = new Trainer();
        trainer3.setUsername("peter.jones");
        trainer3.setFirstName("Peter");
        trainer3.setLastName("Jones");
        trainer3.setPassword("pass");
        trainer3.setIsActive(false);
        trainer3.setSpecialization(specialization1);


        Trainee sampleTrainee = new Trainee();
        sampleTrainee.setUsername("test.trainee");
        sampleTrainee.setFirstName("Test");
        sampleTrainee.setLastName("Trainee");
        sampleTrainee.setPassword("pass");
        sampleTrainee.setIsActive(true);

        sampleTrainee.getTrainers().add(trainer1);
        trainer1.getTrainees().add(sampleTrainee);

        entityManager.persist(trainer1);
        entityManager.persist(trainer2);
        entityManager.persist(trainer3);
        entityManager.persist(sampleTrainee);

        entityManager.flush();
    }

    @Test
    void testSaveAndFindById() {
        Trainer newTrainer = new Trainer();
        newTrainer.setUsername("new.trainer");
        newTrainer.setFirstName("New");
        newTrainer.setLastName("Trainer");
        newTrainer.setPassword("password");
        newTrainer.setIsActive(true);
        newTrainer.setSpecialization(specialization1);

        Trainer savedTrainer = trainerRepository.save(newTrainer);
        assertThat(savedTrainer.getId()).isNotNull();

        Optional<Trainer> foundTrainer = trainerRepository.findById(savedTrainer.getId());

        assertThat(foundTrainer).isPresent();
        assertThat(foundTrainer.get().getUsername()).isEqualTo("new.trainer");
    }

    @Test
    void testFindByUsername() {
        Optional<Trainer> found = trainerRepository.findByUsername("jane.smith");

        assertThat(found).isPresent();
        assertThat(found.get().getFirstName()).isEqualTo("Jane");
    }

    @Test
    void testFindAllTrainersNotAssigned() {
        List<Trainer> unassignedTrainers = trainerRepository.findAllTrainersNotAssigned("test.trainee");

        assertThat(unassignedTrainers).isNotNull();
        assertThat(unassignedTrainers).hasSize(2);
        assertThat(unassignedTrainers)
                .extracting(Trainer::getUsername)
                .containsExactlyInAnyOrder("jane.smith", "peter.jones");
    }

    @Test
    void testDelete() {
        Optional<Trainer> trainerOptional = trainerRepository.findByUsername("peter.jones");
        assertThat(trainerOptional).isPresent();
        trainerRepository.delete(trainerOptional.get());

        Optional<Trainer> foundAfterDelete = trainerRepository.findByUsername("peter.jones");
        assertThat(foundAfterDelete).isNotPresent();
    }

    @Test
    void testFindAll() {
        List<Trainer> allTrainers = trainerRepository.findAll();
        assertThat(allTrainers).hasSize(3);
        assertThat(allTrainers).extracting(Trainer::getUsername)
                .containsExactlyInAnyOrder("john.doe", "jane.smith", "peter.jones");
    }

    @Test
    void testExistsByUsername() {
        boolean exists = trainerRepository.existsByUsername("john.doe");
        assertThat(exists).isTrue();

        boolean notExists = trainerRepository.existsByUsername("non.existent.user");
        assertThat(notExists).isFalse();
    }

    @Test
    void testFindAllByUsername() {
        List<String> usernames = Arrays.asList("john.doe", "jane.smith");
        List<Trainer> foundTrainers = trainerRepository.findAllByUsername(usernames);
        assertThat(foundTrainers).hasSize(2);
        assertThat(foundTrainers).extracting(Trainer::getUsername)
                .containsExactlyInAnyOrderElementsOf(usernames);
    }
}
