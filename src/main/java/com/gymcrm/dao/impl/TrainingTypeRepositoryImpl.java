package com.gymcrm.dao.impl;

import com.gymcrm.dao.TrainingTypeRepository;
import com.gymcrm.model.TrainingType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional(readOnly = true)
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
        String jpql = "SELECT t FROM Training t";
        return em.createQuery(jpql, TrainingType.class).getResultList();
    }
}
