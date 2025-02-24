package com.gymcrm.service.impl;

import com.gymcrm.dao.TrainingDao;
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

    private TrainingDao trainingDao;
    private long currentId = 1;

    @Autowired
    public void setTrainingDao(TrainingDao trainingDao) {
        this.trainingDao = trainingDao;
    }

    public Training createTraining(Training training) {
        training.setId(generateNewId());
        trainingDao.create(training);
        logger.info("Created Training with ID={}", training.getId());
        return training;
    }

    public Training getTraining(Long id) {
        return trainingDao.read(id);
    }

    public List<Training> getAllTrainings() {
        return trainingDao.findAll();
    }

    private Long generateNewId() {
        return currentId++;
    }
}
