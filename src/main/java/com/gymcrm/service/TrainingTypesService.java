package com.gymcrm.service;

import com.gymcrm.dto.training_type.TrainingTypeDto;

import java.util.List;

public interface TrainingTypesService {
    List<TrainingTypeDto> getTrainingTypesList(String username);
}
