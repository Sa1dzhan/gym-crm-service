package com.gymcrm.dao.impl;

import com.gymcrm.dao.TrainingTypeRepository;
import com.gymcrm.model.TrainingType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class TrainingTypeRepositoryImpl implements TrainingTypeRepository {
    @PersistenceContext
    private EntityManager em;

    @Override
    public Optional<TrainingType> findById(Long id) {
        TrainingType training = em.find(TrainingType.class, id);
        return Optional.ofNullable(training);
    }

    @Override
    public List<TrainingType> findAll() {
        return em.createNamedQuery("TrainingType.findAll", TrainingType.class).getResultList();
    }
}
