package com.gymcrm.facade;

import com.gymcrm.model.Trainee;
import com.gymcrm.model.Trainer;
import com.gymcrm.model.Training;
import com.gymcrm.service.TraineeService;
import com.gymcrm.service.TrainerService;
import com.gymcrm.service.TrainingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GymFacade {
    private static final Logger logger = LoggerFactory.getLogger(GymFacade.class);

    private final TraineeService traineeService;
    private final TrainerService trainerService;
    private final TrainingService trainingService;

    @Autowired
    public GymFacade(TraineeService traineeService,
                     TrainerService trainerService,
                     TrainingService trainingService) {
        this.traineeService = traineeService;
        this.trainerService = trainerService;
        this.trainingService = trainingService;
    }

    // -- Trainee
    public Trainee createTrainee(Trainee trainee) {
        logger.debug("Facade: createTrainee called for firstName={}, lastName={}",
                trainee.getFirstName(), trainee.getLastName());
        return traineeService.createTrainee(trainee);
    }

    public Trainee updateTrainee(Trainee trainee) {
        logger.debug("Facade: updateTrainee called for ID={}", trainee.getId());
        return traineeService.updateTrainee(trainee);
    }

    public void deleteTrainee(Long id) {
        logger.debug("Facade: deleteTrainee called for ID={}", id);
        traineeService.deleteTrainee(id);
    }

    public Trainee getTrainee(Long id) {
        logger.debug("Facade: getTrainee called for ID={}", id);
        return traineeService.getTrainee(id);
    }

    public List<Trainee> getAllTrainees() {
        logger.debug("Facade: getAllTrainees called");
        return traineeService.getAllTrainees();
    }

    // -- Trainer
    public Trainer createTrainer(Trainer trainer) {
        logger.debug("Facade: createTrainer called for firstName={}, lastName={}",
                trainer.getFirstName(), trainer.getLastName());
        return trainerService.createTrainer(trainer);
    }

    public Trainer updateTrainer(Trainer trainee) {
        logger.debug("Facade: updateTrainer called for ID={}", trainee.getId());
        return trainerService.updateTrainer(trainee);
    }

    public Trainer getTrainer(Long id) {
        logger.debug("Facade: getTrainer called for ID={}", id);
        return trainerService.getTrainer(id);
    }

    public List<Trainer> getAllTrainers() {
        logger.debug("Facade: getAllTrainers called");
        return trainerService.getAllTrainers();
    }

    // -- Training
    public Training createTraining(Training training) {
        logger.debug("Facade: createTraining called for name={}", training.getTrainingName());
        return trainingService.createTraining(training);
    }

    public Training getTraining(Long id) {
        logger.debug("Facade: getTraining called for ID={}", id);
        return trainingService.getTraining(id);
    }

    public List<Training> getAllTrainings() {
        logger.debug("Facade: getAllTrainers called");
        return trainingService.getAllTrainings();
    }
}
