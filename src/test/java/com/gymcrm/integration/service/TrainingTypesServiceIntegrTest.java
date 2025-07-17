package com.gymcrm.integration.service;

import com.gymcrm.converter.TrainingTypeMapper;
import com.gymcrm.dto.training_type.TrainingTypeDto;
import com.gymcrm.model.TrainingType;
import com.gymcrm.service.TrainingTypesService;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
@Transactional
public class TrainingTypesServiceIntegrTest {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private TrainingTypesService trainingTypesService;

    @MockBean
    private TrainingTypeMapper trainingTypeMapper;

    @BeforeEach
    void setUp() {
        entityManager.createQuery("DELETE FROM Training").executeUpdate();
        entityManager.createQuery("DELETE FROM Trainee").executeUpdate();
        entityManager.createQuery("DELETE FROM Trainer").executeUpdate();
        entityManager.createQuery("DELETE FROM TrainingType").executeUpdate();

        TrainingType type1 = new TrainingType();
        type1.setTrainingTypeName("Yoga");
        entityManager.persist(type1);

        TrainingType type2 = new TrainingType();
        type2.setTrainingTypeName("Pilates");
        entityManager.persist(type2);

        entityManager.flush();
    }

    @Test
    void getTrainingTypesList() {
        when(trainingTypeMapper.toResponseDTO(any(TrainingType.class))).then(invocation -> {
            TrainingType source = invocation.getArgument(0);
            TrainingTypeDto dto = new TrainingTypeDto();
            dto.setId(source.getId());
            dto.setTrainingTypeName(source.getTrainingTypeName());
            return dto;
        });

        List<TrainingTypeDto> result = trainingTypesService.getTrainingTypesList("anyUsername");

        assertThat(result).hasSize(2);
        assertThat(result).extracting(TrainingTypeDto::getTrainingTypeName).containsExactlyInAnyOrder("Yoga", "Pilates");
    }
}
