package com.gymcrm.dao.impl;

import com.gymcrm.dao.TrainerRepository;
import com.gymcrm.model.Trainer;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class TrainerRepositoryImpl extends UserRepositoryImpl<Trainer> implements TrainerRepository {
    public TrainerRepositoryImpl() {
        super(Trainer.class);
    }

    @Override
    public List<Trainer> findAllTrainersNotAssigned(String traineeUsername) {
        EntityManager em = getEm();
        Class<Trainer> entityClass = getEntityClass();

        String jpql = "SELECT t FROM Trainer t\n" +
                "        WHERE t NOT IN (\n" +
                "            SELECT tr FROM Trainer tr\n" +
                "            JOIN tr.trainees tt\n" +
                "            WHERE tt.username = :traineeUsername\n" +
                "        )";
        TypedQuery<Trainer> query = em.createQuery(jpql, entityClass);
        query.setParameter("traineeUsername", traineeUsername);

        return query.getResultList();
    }
}
