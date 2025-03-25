package com.gymcrm.dao;

import com.gymcrm.model.TrainingType;

import java.util.List;
import java.util.Optional;

public interface TrainingTypeRepository {
    Optional<TrainingType> findById(Long id);

    List<TrainingType> findAll();
}
