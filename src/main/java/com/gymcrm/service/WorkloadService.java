package com.gymcrm.service;

import com.gymcrm.model.Training;
import com.gymcrm.util.ActionType;

public interface WorkloadService {
    void updateTrainerWorkload(Training training, ActionType action);
}
