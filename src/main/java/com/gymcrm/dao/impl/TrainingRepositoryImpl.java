package com.gymcrm.dao.impl;

import com.gymcrm.dao.TrainingRepository;
import com.gymcrm.model.Training;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Date;
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
        String jpql = "SELECT t FROM Training t";
        return em.createQuery(jpql, Training.class).getResultList();
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
    public List<Training> findTrainingsForTrainee(String traineeUsername, Date fromDate, Date toDate, String trainerName, String trainingType) {
        String jpql = "SELECT tr FROM Training tr "
                + "WHERE tr.trainee.username = :traineeUsername "
                + "AND (:fromDate IS NULL OR tr.trainingDate >= :fromDate) "
                + "AND (:toDate IS NULL OR tr.trainingDate <= :toDate) "
                + "AND (:trainerName IS NULL OR (LOWER(tr.trainer.lastName) LIKE LOWER(CONCAT('%', :trainerName, '%')) "
                + "     OR LOWER(tr.trainer.firstName) LIKE LOWER(CONCAT('%', :trainerName, '%')))) "
                + "AND (:trainingType IS NULL OR LOWER(tr.trainingType.trainingTypeName) = LOWER(:trainingType))";
        TypedQuery<Training> query = em.createQuery(jpql, Training.class);
        query.setParameter("traineeUsername", traineeUsername);
        query.setParameter("fromDate", fromDate);
        query.setParameter("toDate", toDate);
        query.setParameter("trainerName", trainerName);
        query.setParameter("trainingType", trainingType);
        return query.getResultList();
    }

    @Override
    public List<Training> findTrainingsForTrainer(String trainerUsername, Date fromDate, Date toDate, String traineeName) {
        String jpql = "SELECT tr FROM Training tr "
                + "WHERE tr.trainer.username = :trainerUsername "
                + "AND (:fromDate IS NULL OR tr.trainingDate >= :fromDate) "
                + "AND (:toDate IS NULL OR tr.trainingDate <= :toDate) "
                + "AND (:traineeName IS NULL OR (LOWER(tr.trainee.lastName) LIKE LOWER(CONCAT('%', :traineeName, '%')) "
                + "     OR LOWER(tr.trainee.firstName) LIKE LOWER(CONCAT('%', :traineeName, '%'))))";
        TypedQuery<Training> query = em.createQuery(jpql, Training.class);
        query.setParameter("trainerUsername", trainerUsername);
        query.setParameter("fromDate", fromDate);
        query.setParameter("toDate", toDate);
        query.setParameter("traineeName", traineeName);
        return query.getResultList();
    }
}
