package com.gymcrm.dao.impl;

import com.gymcrm.dao.TrainerRepository;
import com.gymcrm.model.Trainer;
import org.springframework.stereotype.Repository;

@Repository
public class TrainerRepositoryImpl extends UserRepositoryImpl<Trainer> implements TrainerRepository {
    public TrainerRepositoryImpl() {
        super(Trainer.class);
    }
}
