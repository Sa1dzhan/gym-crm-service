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

import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class GymFacade {

    private final TraineeService traineeService;
    private final TrainerService trainerService;
    private final TrainingService trainingService;

    // -- Trainee
    public Trainee createTrainee(Trainee trainee) {
        log.debug("Facade: createTrainee called for firstName={}, lastName={}",
                trainee.getUser().getFirstName(), trainee.getUser().getLastName());
        return traineeService.createTrainee(trainee);
    }

    public Trainee updateTrainee(Trainee trainee) {
        log.debug("Facade: updateTrainee called for ID={}", trainee.getId());
        return traineeService.updateTrainee(trainee);
    }

    public void deleteTrainee(Trainee trainee) {
        log.debug("Facade: deleteTrainee called for username={}", trainee.getUser().getUsername());
        traineeService.deleteTraineeById(trainee);
    }

    public Trainee getTrainee(Long id) {
        log.debug("Facade: getTrainee called for ID={}", id);
        return traineeService.getTrainee(id);
    }

    // -- Trainer
    public Trainer createTrainer(Trainer trainer) {
        log.debug("Facade: createTrainer called for firstName={}, lastName={}",
                trainer.getUser().getFirstName(), trainer.getUser().getLastName());
        return trainerService.createTrainer(trainer);
    }

    public Trainer updateTrainer(Trainer trainee) {
        log.debug("Facade: updateTrainer called for ID={}", trainee.getId());
        return trainerService.updateTrainer(trainee);
    }

    public Trainer getTrainer(Long id) {
        log.debug("Facade: getTrainer called for ID={}", id);
        return trainerService.getTrainer(id);
    }

    public List<Trainer> getAllTrainers() {
        log.debug("Facade: getAllTrainers called");
        return trainerService.getAllTrainers();
    }

    // -- Training
    public Training createTraining(String authUsername, String authPassword, Training training) {
        log.debug("Facade: createTraining called for name={}", training.getTrainingName());
        return trainingService.addTraining(authUsername, authPassword, training);
    }

    public Training getTraining(Long id) {
        log.debug("Facade: getTraining called for ID={}", id);
        return trainingService.getTraining(id);
    }

    public List<Training> getAllTrainings() {
        log.debug("Facade: getAllTrainers called");
        return trainingService.getAllTrainings();
    }
}
