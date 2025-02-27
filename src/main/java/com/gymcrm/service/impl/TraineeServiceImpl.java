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

    @Autowired
    public void setTraineeDao(TraineeRepository traineeRepository) {
        this.traineeRepository = traineeRepository;
    }

    @Override
    public Trainee createTrainee(Trainee trainee) {
        // generate username & password
        UserCredentialGenerator.generateUserCredentials(trainee, userName -> traineeRepository.existsByUsername(userName));

        traineeRepository.create(trainee);
        logger.info("Created Trainee with ID={}, username={}", trainee.getId(), trainee.getUsername());

        return trainee;
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
}
