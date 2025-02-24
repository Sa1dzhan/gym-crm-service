package com.gymcrm.service.impl;

import com.gymcrm.dao.TrainerDao;
import com.gymcrm.model.Trainee;
import com.gymcrm.model.Trainer;
import com.gymcrm.service.TrainerService;
import com.gymcrm.util.UserCredentialGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
public class TrainerServiceImpl implements TrainerService {

    private static final Logger logger = LoggerFactory.getLogger(TrainerServiceImpl.class);

    private TrainerDao trainerDao;
    private long currentId = 1;

    @Autowired
    public void setTrainerDao(TrainerDao trainerDao) {
        this.trainerDao = trainerDao;
    }

    public Trainer createTrainer(Trainer trainer) {
        trainer.setId(generateNewId());
        // generate username & password
        String uniqueUsername = UserCredentialGenerator.generateUniqueUsername(
                trainer.getFirstName(),
                trainer.getLastName(),
                trainerDao.findAll().stream()
                        .map(Trainer::getUsername)
                        .collect(Collectors.toSet())
        );
        trainer.setUsername(uniqueUsername);
        trainer.setPassword(UserCredentialGenerator.generateRandomPassword());
        trainerDao.create(trainer);
        logger.info("Created Trainer with ID={}, username={}", trainer.getId(), trainer.getUsername());
        return trainer;
    }

    public Trainer updateTrainer(Trainer trainer) {
        trainerDao.update(trainer);
        logger.info("Updated Trainer with ID={}", trainer.getId());
        return trainer;
    }

    public Trainer getTrainer(Long id) {
        return trainerDao.read(id);
    }

    public List<Trainer> getAllTrainers() {
        return trainerDao.findAll();
    }

    private Long generateNewId() {
        return currentId++;
    }
}
