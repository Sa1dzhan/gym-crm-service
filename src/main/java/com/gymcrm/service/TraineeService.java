package com.gymcrm.service;

import com.gymcrm.model.Trainee;
import com.gymcrm.model.Trainer;

import java.util.List;
import java.util.Set;

public interface TraineeService {
    Trainee createTrainee(Trainee trainee);
    Trainee updateTrainee(Trainee trainee);

    void deleteTraineeById(Trainee trainee);
    Trainee getTrainee(Long id);

    Trainee getByUsername(String username);

    void changePassword(String username, String oldPassword, String newPassword);

    void toggleActive(String username, String password);

    void deleteTraineeByUsername(String username, String password);

    List<Trainer> getTrainersNotAssigned(String username, String password);

    void updateTrainersList(String username, String password, Set<Long> trainerIds);
}
