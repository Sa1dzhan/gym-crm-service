package com.gymcrm.service.impl;

import com.gymcrm.dao.TrainerRepository;
import com.gymcrm.model.Trainer;
import com.gymcrm.service.TrainerService;
import com.gymcrm.util.UserCredentialGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TrainerServiceImpl implements TrainerService {

    private static final Logger logger = LoggerFactory.getLogger(TrainerServiceImpl.class);

    private TrainerRepository trainerRepository;

    @Autowired
    public void setTrainerDao(TrainerRepository trainerRepository) {
        this.trainerRepository = trainerRepository;
    }

    @Override
    public Trainer createTrainer(Trainer trainer) {
        int suffix = 1;
        trainer.setId(generateNewId());
        // generate username & password
        String username = UserCredentialGenerator.generateUsername(
                trainer.getFirstName(),
                trainer.getLastName()
        );
        String base = username;

        while (containsUsername(username)) {
            username = base + suffix;
            suffix++;
        }

        trainer.setUsername(username);
        trainer.setPassword(UserCredentialGenerator.generateRandomPassword());
        trainerRepository.create(trainer);
        logger.info("Created Trainer with ID={}, username={}", trainer.getId(), trainer.getUsername());
        return trainer;
    }

    private Boolean containsUsername(String userName) {
        return getAllTrainers().parallelStream().anyMatch(item -> item.getUsername().equals(userName));
    }

    @Override
    public Trainer updateTrainer(Trainer trainer) {
        trainerRepository.update(trainer);
        logger.info("Updated Trainer with ID={}", trainer.getId());
        return trainer;
    }

    @Override
    public Trainer getTrainer(Long id) {
        return trainerRepository.read(id);
    }

    @Override
    public List<Trainer> getAllTrainers() {
        return trainerRepository.findAll();
    }

    private Long generateNewId() {
        return UserCredentialGenerator.generateTrainerUserId();
    }
}
