package com.gymcrm.service.impl;

import com.gymcrm.dao.TrainingRepository;
import com.gymcrm.model.Training;
import com.gymcrm.service.TrainingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TrainingServiceImpl implements TrainingService {

    private static final Logger logger = LoggerFactory.getLogger(TrainingServiceImpl.class);

    private TrainingRepository trainingRepository;
    private long currentId = 1;

    @Autowired
    public void setTrainingDao(TrainingRepository trainingRepository) {
        this.trainingRepository = trainingRepository;
    }

    public Training createTraining(Training training) {
        training.setId(generateNewId());
        trainingRepository.create(training);
        logger.info("Created Training with ID={}", training.getId());
        return training;
    }

    public Training getTraining(Long id) {
        return trainingRepository.read(id);
    }

    public List<Training> getAllTrainings() {
        return trainingRepository.findAll();
    }

    private Long generateNewId() {
        return currentId++;
    }
}
