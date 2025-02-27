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
        // generate username & password
        UserCredentialGenerator.generateUserCredentials(trainer, userName -> trainerRepository.existsByUsername(userName));

        trainerRepository.create(trainer);
        logger.info("Created Trainer with ID={}, username={}", trainer.getId(), trainer.getUsername());

        return trainer;
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
}
