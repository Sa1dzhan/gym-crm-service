package com.gymcrm;


import com.gymcrm.config.AppConfig;
import com.gymcrm.model.Trainee;
import com.gymcrm.service.TraineeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

@Slf4j
public class GymCrmApplication {

    public static void main(String[] args) {
        log.info("Starting Gym CRM Application...");
        AnnotationConfigApplicationContext context =
                new AnnotationConfigApplicationContext(AppConfig.class);

        TraineeService traineeService = context.getBean(TraineeService.class);

        Trainee t = new Trainee();
        t.setUsername("john.doe");
        t.setPassword("secret");
        t.setFirstName("John");
        t.setLastName("Doe");
        t.setIsActive(true);
        Trainee saved = traineeService.createTrainee(t);
        System.out.println("Created Trainee with ID=" + saved.getId());

        Trainee found = traineeService.getTrainee(saved.getId());
        System.out.println("Found Trainee username=" + found.getUsername());

        context.close();
    }
}

