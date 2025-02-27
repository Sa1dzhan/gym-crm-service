package com.gymcrm;

import com.gymcrm.config.AppConfig;
import com.gymcrm.facade.GymFacade;
import com.gymcrm.model.Trainee;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;


public class GymCrmApplication {
    private static final Logger logger = LoggerFactory.getLogger(GymCrmApplication.class);

    public static void main(String[] args) {
        logger.info("Starting Gym CRM Application...");
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);

        GymFacade facade = context.getBean(GymFacade.class);

        // Example usage
        Trainee newTrainee = new Trainee();
        newTrainee.setFirstName("Jane");
        newTrainee.setLastName("Smith");
        newTrainee.setAddress("123 Maple St");
        facade.createTrainee(newTrainee);

        // List all
        facade.getAllTrainees().forEach(t ->
                logger.info("Trainee: id={}, username={}", t.getId(), t.getUsername())
        );

        facade.getAllTrainers().forEach(t ->
                logger.info("Trainer: id={}, username={}", t.getId(), t.getUsername())
        );

        facade.getAllTrainings().forEach(t ->
                logger.info("Training: id={}, username={}", t.getId(), t.getTrainingName())
        );

        context.close();
    }
}

