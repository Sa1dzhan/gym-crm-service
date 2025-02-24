package com.gymcrm.service;

import com.gymcrm.model.Trainee;
import org.springframework.stereotype.Service;

import java.util.List;

public interface TraineeService {
    Trainee createTrainee(Trainee trainee);
    Trainee updateTrainee(Trainee trainee);
    void deleteTrainee(Long id);
    Trainee getTrainee(Long id);
    List<Trainee> getAllTrainees();
}
