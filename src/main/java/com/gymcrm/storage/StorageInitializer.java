package com.gymcrm.storage;

import com.gymcrm.facade.GymFacade;
import com.gymcrm.model.Trainee;
import com.gymcrm.model.Trainer;
import com.gymcrm.model.Training;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;

@Component
public class StorageInitializer implements BeanPostProcessor {

    private static final Logger logger = LoggerFactory.getLogger(StorageInitializer.class);

    @Value("${data.init.file}")
    private String dataFilePath;


    private static long traineeIdCounter = 1;
    private static long trainerIdCounter = 1;
    private static long trainingIdCounter = 1;

    private static synchronized long getNextTraineeId() {
        return traineeIdCounter++;
    }

    private static synchronized long getNextTrainerId() {
        return trainerIdCounter++;
    }

    private static synchronized long getNextTrainingId() {
        return trainingIdCounter++;
    }


    @Override
    @NonNull
    public Object postProcessAfterInitialization(@NonNull Object bean, @NonNull String beanName) throws BeansException {
        logger.info("Reading from file: {}", dataFilePath);

        if (bean instanceof GymFacade gymFacade) {
            try {
                List<String> lines = Files.readAllLines(Paths.get(dataFilePath));

                for (String line : lines) {
                    // Skip empty lines or commented lines
                    if (line.trim().isEmpty() || line.trim().startsWith("#")) {
                        continue;
                    }

                    String[] tokens = line.split(",");
                    if (tokens.length < 2) {
                        logger.warn("Skipping invalid line (not enough tokens): {}", line);
                        continue;
                    }

                    // First token is the record type: Trainee, Trainer, or Training
                    String recordType = tokens[0].trim().toLowerCase();
                    switch (recordType) {
                        case "trainee":
                            if (tokens.length < 5) {
                                logger.warn("Skipping incomplete trainee line: {}", line);
                                continue;
                            }
                            Trainee trainee = new Trainee();
                            trainee.setId(getNextTraineeId());
                            trainee.setFirstName(tokens[1].trim());
                            trainee.setLastName(tokens[2].trim());
                            trainee.setDateOfBirth(LocalDate.parse(tokens[3].trim()));
                            trainee.setAddress(tokens[4].trim());
                            trainee = gymFacade.createTrainee(trainee);
                            logger.debug("Loaded Trainee into storage: {}", trainee.getUsername());
                            break;

                        case "trainer":
                            if (tokens.length < 4) {
                                logger.warn("Skipping incomplete trainer line: {}", line);
                                continue;
                            }
                            Trainer trainer = new Trainer();
                            trainer.setId(getNextTrainerId());
                            trainer.setFirstName(tokens[1].trim());
                            trainer.setLastName(tokens[2].trim());
                            trainer.setSpecialization(tokens[3].trim());
                            trainer = gymFacade.createTrainer(trainer);
                            logger.debug("Loaded Trainer into storage: {}", trainer.getUsername());
                            break;

                        case "training":
                            if (tokens.length < 7) {
                                logger.warn("Skipping incomplete training line: {}", line);
                                continue;
                            }
                            Training training = new Training();
                            training.setId(getNextTrainingId());
                            training.setTrainerId(Long.parseLong(tokens[1].trim()));
                            training.setTraineeId(Long.parseLong(tokens[2].trim()));
                            training.setTrainingName(tokens[3].trim());
                            training.setTrainingType(tokens[4].trim());
                            training.setTrainingDate(LocalDate.parse(tokens[5].trim()));
                            training.setTrainingDuration(Integer.parseInt(tokens[6].trim()));
                            training = gymFacade.createTraining(training);
                            logger.debug("Loaded Training into storage: {}", training.getTrainingName());
                            break;

                        default:
                            logger.warn("Unknown record type: {}. Skipping line: {}", recordType, line);
                            break;
                    }
                }

            } catch (Exception e) {
                logger.error("Error reading data init file: {}", dataFilePath, e);
            }
        }
        return bean;
    }
}
