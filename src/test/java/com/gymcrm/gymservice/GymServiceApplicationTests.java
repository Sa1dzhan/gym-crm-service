package com.gymcrm.gymservice;

import com.gymcrm.config.AppConfig;
import com.gymcrm.facade.GymFacade;
import com.gymcrm.model.Trainee;
import com.gymcrm.model.Trainer;
import com.gymcrm.model.Training;
import com.gymcrm.service.TraineeService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = AppConfig.class)
public class GymServiceApplicationTests {

    @Autowired
    private GymFacade gymFacade;

    @Test
    void testTraineeCRUD() {
        // 1. Create
        Trainee trainee = new Trainee();
        trainee.setFirstName("John");
        trainee.setLastName("Doe");
        trainee.setDateOfBirth(LocalDate.of(1990, 1, 1));
        trainee.setAddress("123 Maple St");

        Trainee created = gymFacade.createTrainee(trainee);
        assertNotNull(created.getId(), "Trainee ID should be assigned");
        assertNotNull(created.getUsername(), "Username should be generated");
        assertNotNull(created.getPassword(), "Password should be generated");

        // 2. Read (get by ID)
        Trainee found = gymFacade.getTrainee(created.getId());
        assertEquals("John", found.getFirstName(), "First name should match");

        // 3. Update
        found.setAddress("456 Oak Ave");
        Trainee updated = gymFacade.updateTrainee(found);
        assertEquals("456 Oak Ave", updated.getAddress(), "Address should be updated");

        // 4. List all
        List<Trainee> allTrainees = gymFacade.getAllTrainees();
        assertFalse(allTrainees.isEmpty(), "Should have at least one Trainee");

        // 5. Delete
        gymFacade.deleteTrainee(created.getId());
        Trainee afterDelete = gymFacade.getTrainee(created.getId());
        assertNull(afterDelete, "Trainee should be deleted");
    }

    @Test
    void testTrainerCRUD() {
        // 1. Create
        Trainer trainer = new Trainer();
        trainer.setFirstName("Alice");
        trainer.setLastName("Smith");
        trainer.setSpecialization("Yoga");

        Trainer created = gymFacade.createTrainer(trainer);
        assertNotNull(created.getId(), "Trainer ID should be assigned");
        assertNotNull(created.getUsername(), "Username should be generated");
        assertNotNull(created.getPassword(), "Password should be generated");

        // 2. Read
        Trainer found = gymFacade.getTrainer(created.getId());
        assertEquals("Alice", found.getFirstName(), "First name should match");

        // 3. Update
        found.setSpecialization("Pilates");
        Trainer updated = gymFacade.updateTrainer(found);
        assertEquals("Pilates", updated.getSpecialization(), "Specialization should be updated");

        // 4. List all
        List<Trainer> allTrainers = gymFacade.getAllTrainers();
        assertFalse(allTrainers.isEmpty(), "Should have at least one Trainer");
    }

    @Test
    void testTrainingCreateAndSelect() {
        // Create required trainee and trainer
        Trainee trainee = new Trainee();
        trainee.setFirstName("Bob");
        trainee.setLastName("Marley");
        Trainee createdTrainee = gymFacade.createTrainee(trainee);

        Trainer trainer = new Trainer();
        trainer.setFirstName("Carol");
        trainer.setLastName("Danvers");
        trainer.setSpecialization("Cardio");
        Trainer createdTrainer = gymFacade.createTrainer(trainer);

        // 1. Create Training
        Training training = new Training();
        training.setTrainerId(createdTrainer.getId());
        training.setTraineeId(createdTrainee.getId());
        training.setTrainingName("Morning Session");
        training.setTrainingType("Strength");
        training.setTrainingDate(LocalDate.of(2025, 1, 15));
        training.setTrainingDuration(60); // 60 minutes

        Training createdTraining = gymFacade.createTraining(training);
        assertNotNull(createdTraining.getId(), "Training ID should be assigned");

        // 2. Read
        Training foundTraining = gymFacade.getTraining(createdTraining.getId());
        assertEquals("Morning Session", foundTraining.getTrainingName(), "Training name should match");

        // 3. List all
        List<Training> allTrainings = gymFacade.getAllTrainings();
        assertFalse(allTrainings.isEmpty(), "Should have at least one Training");
    }
}
