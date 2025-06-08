package com.gymcrm.dao.impl;

import com.gymcrm.dao.TrainingRepository;
import com.gymcrm.model.Training;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class TrainingRepositoryImpl implements TrainingRepository {
    @PersistenceContext
    private EntityManager em;

    @Override
    public Optional<Training> findById(Long id) {
        Training training = em.find(Training.class, id);
        return Optional.ofNullable(training);
    }

    @Override
    public List<Training> findAll() {
        return em.createNamedQuery("Training.findAll", Training.class).getResultList();
    }

    @Override
    public Training save(Training entity) {
        if (entity.getId() == null) {
            em.persist(entity);
            em.flush();
            return entity;
        } else {
            return em.merge(entity);
        }
    }

    @Override
    public List<Training> findTrainingsForTrainee(String traineeUsername, LocalDate fromDate, LocalDate toDate, String trainerName, String trainingType) {
        TypedQuery<Training> query = em.createNamedQuery("Training.findTrainingsForTrainee", Training.class);
        query.setParameter("traineeUsername", traineeUsername);
        query.setParameter("fromDate", fromDate);
        query.setParameter("toDate", toDate);
        query.setParameter("trainerName", trainerName);
        query.setParameter("trainingType", trainingType);
        return query.getResultList();
    }

    @Override
    public List<Training> findTrainingsForTrainer(String trainerUsername, LocalDate fromDate, LocalDate toDate, String traineeName) {
        TypedQuery<Training> query = em.createNamedQuery("Training.findTrainingsForTrainer", Training.class);
        query.setParameter("trainerUsername", trainerUsername);
        query.setParameter("fromDate", fromDate);
        query.setParameter("toDate", toDate);
        query.setParameter("traineeName", traineeName);
        return query.getResultList();
    }
}
