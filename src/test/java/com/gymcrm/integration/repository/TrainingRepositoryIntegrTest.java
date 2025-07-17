package com.gymcrm.integration.repository;

import com.gymcrm.dao.TrainingRepository;
import com.gymcrm.dao.impl.TraineeRepositoryImpl;
import com.gymcrm.dao.impl.TrainerRepositoryImpl;
import com.gymcrm.dao.impl.TrainingRepositoryImpl;
import com.gymcrm.dao.impl.TrainingTypeRepositoryImpl;
import com.gymcrm.model.Trainee;
import com.gymcrm.model.Trainer;
import com.gymcrm.model.Training;
import com.gymcrm.model.TrainingType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import({TrainingRepositoryImpl.class, TrainerRepositoryImpl.class, TraineeRepositoryImpl.class, TrainingTypeRepositoryImpl.class})
public class TrainingRepositoryIntegrTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private TrainingRepository trainingRepository;

    private Trainee trainee;
    private Trainer trainer;
    private TrainingType trainingType;
    private Training training;

    @BeforeEach
    void setUp() {
        trainingType = new TrainingType();
        trainingType.setTrainingTypeName("Yoga");
        entityManager.persist(trainingType);

        trainer = new Trainer();
        trainer.setUsername("yogi.bear");
        trainer.setFirstName("Yogi");
        trainer.setLastName("Bear");
        trainer.setPassword("password");
        trainer.setSpecialization(trainingType);
        trainer.setIsActive(true);
        entityManager.persist(trainer);

        trainee = new Trainee();
        trainee.setUsername("cindy.jones");
        trainee.setFirstName("Cindy");
        trainee.setLastName("Jones");
        trainee.setPassword("password");
        trainee.setIsActive(true);
        entityManager.persist(trainee);

        training = new Training();
        training.setTrainee(trainee);
        training.setTrainer(trainer);
        training.setTrainingType(trainingType);
        training.setTrainingName("Morning Yoga");
        training.setTrainingDate(LocalDate.of(2025, 7, 17));
        training.setTrainingDuration(60L);
        entityManager.persist(training);

        entityManager.flush();
    }

    @Test
    void testSaveAndFindById() {
        Training newTraining = new Training();
        newTraining.setTrainee(trainee);
        newTraining.setTrainer(trainer);
        newTraining.setTrainingType(trainingType);
        newTraining.setTrainingName("Evening Meditation");
        newTraining.setTrainingDate(LocalDate.now());
        newTraining.setTrainingDuration(30L);

        Training savedTraining = trainingRepository.save(newTraining);

        assertThat(savedTraining.getId()).isNotNull();
        assertThat(trainingRepository.findById(savedTraining.getId())).isPresent();
    }

    @Test
    void testFindTrainingsForTrainee() {
        List<Training> trainings = trainingRepository.findTrainingsForTrainee(
                "cindy.jones",
                LocalDate.of(2025, 7, 1),
                LocalDate.of(2025, 7, 31),
                "Yogi",
                "Yoga"
        );

        assertThat(trainings).hasSize(1);
        assertThat(trainings.get(0).getTrainingName()).isEqualTo("Morning Yoga");
    }

    @Test
    void testFindTrainingsForTrainer() {
        List<Training> trainings = trainingRepository.findTrainingsForTrainer(
                "yogi.bear",
                LocalDate.of(2025, 7, 1),
                LocalDate.of(2025, 7, 31),
                "Cindy"
        );

        assertThat(trainings).hasSize(1);
        assertThat(trainings.get(0).getTrainee().getFirstName()).isEqualTo("Cindy");
    }

    @Test
    void testSave() {
        training.setTrainingName("Afternoon Yoga");
        trainingRepository.save(training);

        entityManager.flush();
        entityManager.clear();

        Training updatedTraining = trainingRepository.findById(training.getId()).get();
        assertThat(updatedTraining.getTrainingName()).isEqualTo("Afternoon Yoga");
    }

    @Test
    void testFindAll() {
        List<Training> trainings = trainingRepository.findAll();
        assertThat(trainings).hasSize(1);
        assertThat(trainings.get(0).getTrainingName()).isEqualTo("Morning Yoga");
    }
}
