package com.gymcrm.dao;

import com.gymcrm.model.TrainingType;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TrainingTypeRepository {
    Optional<TrainingType> findById(Long id);

    List<TrainingType> findAll();
}
