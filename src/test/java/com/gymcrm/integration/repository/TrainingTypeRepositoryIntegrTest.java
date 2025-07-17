package com.gymcrm.integration.repository;

import com.gymcrm.dao.TrainingTypeRepository;
import com.gymcrm.dao.impl.TrainingTypeRepositoryImpl;
import com.gymcrm.model.TrainingType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;


@DataJpaTest
@Import(TrainingTypeRepositoryImpl.class)
public class TrainingTypeRepositoryIntegrTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private TrainingTypeRepository trainingTypeRepository;

    private TrainingType pilates;

    @BeforeEach
    void setUp() {
        pilates = new TrainingType();
        pilates.setTrainingTypeName("Pilates");

        TrainingType type2 = new TrainingType();
        type2.setTrainingTypeName("CrossFit");

        entityManager.persist(pilates);
        entityManager.persist(type2);
        entityManager.flush();
    }

    @Test
    void testFindAll() {
        List<TrainingType> allTypes = trainingTypeRepository.findAll();

        assertThat(allTypes).hasSize(2);
        assertThat(allTypes)
                .extracting(TrainingType::getTrainingTypeName)
                .containsExactlyInAnyOrder("Pilates", "CrossFit");
    }

    @Test
    void testFindById() {
        assertThat(pilates.getId()).isNotNull();

        Optional<TrainingType> foundType = trainingTypeRepository.findById(pilates.getId());

        assertThat(foundType).isPresent();
        assertThat(foundType.get().getTrainingTypeName()).isEqualTo("Pilates");
    }
}
