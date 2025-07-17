package com.gymcrm.integration.repository;

import com.gymcrm.dao.TraineeRepository;
import com.gymcrm.dao.impl.TraineeRepositoryImpl;
import com.gymcrm.model.Trainee;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;


@DataJpaTest
@Import(TraineeRepositoryImpl.class)
public class TraineeRepositoryIntegrTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private TraineeRepository traineeRepository;

    private Trainee trainee1;

    @BeforeEach
    void setUp() {
        trainee1 = new Trainee();
        trainee1.setUsername("test.trainee.one");
        trainee1.setFirstName("Test");
        trainee1.setLastName("One");
        trainee1.setPassword("password");
        trainee1.setIsActive(true);
        entityManager.persist(trainee1);

        Trainee trainee2 = new Trainee();
        trainee2.setUsername("test.trainee.two");
        trainee2.setFirstName("Test");
        trainee2.setLastName("Two");
        trainee2.setPassword("password");
        trainee2.setIsActive(true);
        entityManager.persist(trainee2);

        entityManager.flush();
    }

    @Test
    void testSaveAndFindByUsername() {
        Trainee newTrainee = new Trainee();
        newTrainee.setUsername("test.trainee");
        newTrainee.setFirstName("Test");
        newTrainee.setLastName("Trainee");
        newTrainee.setPassword("password");
        newTrainee.setDateOfBirth(LocalDate.of(1995, 5, 20));
        newTrainee.setAddress("123 Test Lane");
        newTrainee.setIsActive(true);

        traineeRepository.save(newTrainee);
        Optional<Trainee> foundTrainee = traineeRepository.findByUsername("test.trainee");

        assertThat(foundTrainee).isPresent();
        assertThat(foundTrainee.get().getFirstName()).isEqualTo("Test");
        assertThat(foundTrainee.get().getAddress()).isEqualTo("123 Test Lane");
    }

    @Test
    void testDelete() {
        Trainee traineeToDelete = new Trainee();
        traineeToDelete.setUsername("delete.me");
        traineeToDelete.setFirstName("Delete");
        traineeToDelete.setLastName("Me");
        traineeToDelete.setPassword("password");
        traineeToDelete.setIsActive(true);
        entityManager.persistAndFlush(traineeToDelete);

        assertThat(traineeRepository.findByUsername("delete.me")).isPresent();

        traineeRepository.delete(traineeToDelete);
        entityManager.flush();

        Optional<Trainee> foundTrainee = traineeRepository.findByUsername("delete.me");
        assertThat(foundTrainee).isNotPresent();
    }

    @Test
    void testFindById() {
        Optional<Trainee> foundTrainee = traineeRepository.findById(trainee1.getId());
        assertThat(foundTrainee).isPresent();
        assertThat(foundTrainee.get().getUsername()).isEqualTo("test.trainee.one");
    }

    @Test
    void testFindAll() {
        List<Trainee> allTrainees = traineeRepository.findAll();
        assertThat(allTrainees).hasSize(2);
        assertThat(allTrainees).extracting(Trainee::getUsername)
                .containsExactlyInAnyOrder("test.trainee.one", "test.trainee.two");
    }

    @Test
    void testExistsByUsername() {
        boolean exists = traineeRepository.existsByUsername("test.trainee.one");
        assertThat(exists).isTrue();

        boolean notExists = traineeRepository.existsByUsername("non.existent.user");
        assertThat(notExists).isFalse();
    }

    @Test
    void testFindAllByUsername() {
        List<String> usernames = Arrays.asList("test.trainee.one", "test.trainee.two");
        List<Trainee> foundTrainees = traineeRepository.findAllByUsername(usernames);
        assertThat(foundTrainees).hasSize(2);
        assertThat(foundTrainees).extracting(Trainee::getUsername)
                .containsExactlyInAnyOrderElementsOf(usernames);
    }
}
