package com.gymcrm.dao.impl;

import com.gymcrm.dao.TraineeRepository;
import com.gymcrm.model.Trainee;
import org.springframework.stereotype.Repository;

@Repository
public class TraineeRepositoryImpl extends UserRepositoryImpl<Trainee> implements TraineeRepository {
    public TraineeRepositoryImpl() {
        super(Trainee.class);
    }
}
