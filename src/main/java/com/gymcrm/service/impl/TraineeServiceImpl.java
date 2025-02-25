package com.gymcrm.service.impl;

import com.gymcrm.dao.TraineeRepository;
import com.gymcrm.model.Trainee;
import com.gymcrm.service.TraineeService;
import com.gymcrm.util.UserCredentialGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TraineeServiceImpl implements TraineeService {

    private static final Logger logger = LoggerFactory.getLogger(TraineeServiceImpl.class);

    private TraineeRepository traineeRepository;
    private long currentId = 1;

    @Autowired
    public void setTraineeDao(TraineeRepository traineeRepository) {
        this.traineeRepository = traineeRepository;
    }

    @Override
    public Trainee createTrainee(Trainee trainee) {
        int suffix = 1;
        trainee.setId(generateNewId());
        // generate username & password
        String username = UserCredentialGenerator.generateUsername(
                trainee.getFirstName(),
                trainee.getLastName()
        );
        String base = username;

        while (containsUsername(username)) {
            username = base + suffix;
            suffix++;
        }

        trainee.setUsername(username);
        trainee.setPassword(UserCredentialGenerator.generateRandomPassword());

        traineeRepository.create(trainee);
        logger.info("Created Trainee with ID={}, username={}", trainee.getId(), trainee.getUsername());

        return trainee;
    }

    private Boolean containsUsername(String userName) {
        return getAllTrainees().parallelStream().anyMatch(item -> item.getUsername().equals(userName));
    }

    @Override
    public Trainee updateTrainee(Trainee trainee) {
        traineeRepository.update(trainee);
        logger.info("Updated Trainee with ID={}", trainee.getId());
        return trainee;
    }

    @Override
    public void deleteTrainee(Long id) {
        traineeRepository.delete(id);
        logger.warn("Deleted Trainee with ID={}", id);
    }

    @Override
    public Trainee getTrainee(Long id) {
        return traineeRepository.read(id);
    }

    @Override
    public List<Trainee> getAllTrainees() {
        return traineeRepository.findAll();
    }

    private Long generateNewId() {
        return UserCredentialGenerator.generateTraineeUserId();
    }
}
