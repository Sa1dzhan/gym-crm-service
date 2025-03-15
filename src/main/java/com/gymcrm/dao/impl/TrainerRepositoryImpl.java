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

        TypedQuery<Trainer> query = em.createNamedQuery("Trainer.findAllTrainersNotAssigned", Trainer.class);
        query.setParameter("traineeUsername", traineeUsername);

        return query.getResultList();
    }
}
