package com.gymcrm.service;

import com.gymcrm.dto.workload.WorkloadRequestDto;
import com.gymcrm.model.Training;

public interface WorkloadService {
    void updateTrainerWorkload(Training training, WorkloadRequestDto.ActionType action);
}
