package com.gymcrm.service.impl;

import com.gymcrm.dao.TraineeDao;
import com.gymcrm.model.Trainee;
import com.gymcrm.service.TraineeService;
import com.gymcrm.util.UserCredentialGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TraineeServiceImpl implements TraineeService {

    private static final Logger logger = LoggerFactory.getLogger(TraineeServiceImpl.class);

    private TraineeDao traineeDao;
    private long currentId = 1;

    @Autowired
    public void setTraineeDao(TraineeDao traineeDao) {
        this.traineeDao = traineeDao;
    }

    @Override
    public Trainee createTrainee(Trainee trainee) {
        trainee.setId(generateNewId());
        // generate username & password
        String uniqueUsername = UserCredentialGenerator.generateUniqueUsername(
                trainee.getFirstName(),
                trainee.getLastName(),
                traineeDao.findAll().stream()
                        .map(Trainee::getUsername)
                        .collect(Collectors.toSet())
        );
        trainee.setUsername(uniqueUsername);
        trainee.setPassword(UserCredentialGenerator.generateRandomPassword());

        traineeDao.create(trainee);
        logger.info("Created Trainee with ID={}, username={}", trainee.getId(), trainee.getUsername());

        return trainee;
    }

    @Override
    public Trainee updateTrainee(Trainee trainee) {
        traineeDao.update(trainee);
        logger.info("Updated Trainee with ID={}", trainee.getId());
        return trainee;
    }

    @Override
    public void deleteTrainee(Long id) {
        traineeDao.delete(id);
        logger.warn("Deleted Trainee with ID={}", id);
    }

    @Override
    public Trainee getTrainee(Long id) {
        return traineeDao.read(id);
    }

    @Override
    public List<Trainee> getAllTrainees() {
        return traineeDao.findAll();
    }

    private Long generateNewId() {
        return currentId++;
    }
}
