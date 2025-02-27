package com.gymcrm.storage;

import com.gymcrm.model.Trainee;
import com.gymcrm.model.Trainer;
import com.gymcrm.model.Training;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class InMemoryStorage {
    private static final Logger logger = LoggerFactory.getLogger(InMemoryStorage.class);
    private final Map<Long, Trainer> trainerStorage = new HashMap<>();
    private final Map<Long, Trainee> traineeStorage = new HashMap<>();
    private final Map<Long, Training> trainingStorage = new HashMap<>();
    private final Map<String, Long> trainerUsernames = new HashMap<>();
    private final Map<String, Long> traineeUsernames = new HashMap<>();

    public InMemoryStorage() {
        logger.info("InMemoryStorage created.");
    }

    public Map<Long, Trainer> getTrainerStorage() {
        return trainerStorage;
    }

    public Map<Long, Trainee> getTraineeStorage() {
        return traineeStorage;
    }

    public Map<Long, Training> getTrainingStorage() {
        return trainingStorage;
    }

    public Map<String, Long> getTrainerUsernames() {
        return trainerUsernames;
    }

    public Map<String, Long> getTraineeUsernames() {
        return traineeUsernames;
    }
}
