package com.gymcrm.facade;

import com.gymcrm.model.Trainee;
import com.gymcrm.model.Trainer;
import com.gymcrm.model.Training;
import com.gymcrm.service.TraineeService;
import com.gymcrm.service.TrainerService;
import com.gymcrm.service.TrainingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class GymFacade {

    private final TraineeService traineeService;
    private final TrainerService trainerService;
    private final TrainingService trainingService;

    // -- Trainee operations
    public Trainee registerTrainee(Trainee trainee) {
        return traineeService.createTrainee(trainee);
    }


    public Trainee updateTrainee(Trainee trainee) {
        return traineeService.updateTrainee(trainee);
    }


    public Trainee getTraineeById(Long id) {
        return traineeService.getTrainee(id);
    }


    public Trainee getTraineeByUsername(String username) {
        return traineeService.getByUsername(username);
    }


    public void changeTraineePassword(String username, String oldPassword, String newPassword) {
        traineeService.changePassword(username, oldPassword, newPassword);
    }


    public void toggleTraineeActiveStatus(String username, String password) {
        traineeService.toggleActive(username, password);
    }

    
    public void deleteTrainee(Trainee trainee) {
        traineeService.deleteTraineeById(trainee);
    }


    public void deleteTraineeByUsername(String username, String password) {
        traineeService.deleteTraineeByUsername(username, password);
    }


    public List<Trainer> getAvailableTrainersForTrainee(String username, String password) {
        return traineeService.getTrainersNotAssigned(username, password);
    }


    public void updateTraineeTrainers(String username, String password, List<Long> trainerIds) {
        traineeService.updateTrainersList(username, password, trainerIds);
    }


    // -- Trainer operations
    public Trainer registerTrainer(Trainer trainer) {
        return trainerService.createTrainer(trainer);
    }


    public Trainer updateTrainer(Trainer trainer) {
        return trainerService.updateTrainer(trainer);
    }


    public Trainer getTrainerById(Long id) {
        return trainerService.getTrainer(id);
    }


    public Trainer getTrainerByUsername(String username) {
        return trainerService.getByUsername(username);
    }


    public void changeTrainerPassword(String username, String oldPassword, String newPassword) {
        trainerService.changePassword(username, oldPassword, newPassword);
    }


    public void toggleTrainerActiveStatus(String username, String password) {
        trainerService.toggleActive(username, password);
    }

    
    public List<Trainer> getAllTrainers() {
        return trainerService.getAllTrainers();
    }


    // -- Training operations
    public Training addTraining(String authUsername, String authPassword, Training training) {
        return trainingService.addTraining(authUsername, authPassword, training);
    }


    public Training getTrainingById(Long id) {
        return trainingService.getTraining(id);
    }


    public List<Training> getAllTrainings() {
        return trainingService.getAllTrainings();
    }


    public List<Training> getTraineeTrainings(String authUsername,
                                              String authPassword,
                                              String traineeUsername,
                                              Date fromDate,
                                              Date toDate,
                                              String trainerName,
                                              String trainingType) {
        return trainingService.getTraineeTrainings(
                authUsername, authPassword, traineeUsername, fromDate, toDate, trainerName, trainingType);
    }


    public List<Training> getTrainerTrainings(String authUsername,
                                              String authPassword,
                                              String trainerUsername,
                                              Date fromDate,
                                              Date toDate,
                                              String traineeName) {
        return trainingService.getTrainerTrainings(
                authUsername, authPassword, trainerUsername, fromDate, toDate, traineeName);
    }
}
