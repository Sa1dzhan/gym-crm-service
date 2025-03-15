package com.gymcrm.dao;

import com.gymcrm.model.Trainer;

import java.util.List;

public interface TrainerRepository extends UserRepository<Trainer> {
    List<Trainer> findAllTrainersNotAssigned(String traineeUsername);
}
