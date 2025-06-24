package com.gymcrm.service.impl;

import com.gymcrm.dto.workload.WorkloadRequestDto;
import com.gymcrm.model.Trainer;
import com.gymcrm.model.Training;
import com.gymcrm.service.WorkloadService;
import com.gymcrm.trainerworkload.TrainerWorkloadClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class WorkloadServiceImpl implements WorkloadService {
    private final TrainerWorkloadClient workloadClient;

    @Override
    public void updateTrainerWorkload(Training training, WorkloadRequestDto.ActionType action) {
        try {
            WorkloadRequestDto request = getWorkloadRequestDto(training, action);

            log.info("Sending workload update request for: {}", training.getTrainer().getUsername());
            workloadClient.updateTrainerSummary(request);

            log.info("Workload update request sent successfully.");
        } catch (Exception e) {
            log.error("Error updating workload: {}", e.getMessage(), e);
        }
    }

    private WorkloadRequestDto getWorkloadRequestDto(Training training, WorkloadRequestDto.ActionType action) {
        WorkloadRequestDto request = new WorkloadRequestDto();
        Trainer trainer = training.getTrainer();

        request.setUsername(trainer.getUsername());
        request.setFirstName(trainer.getFirstName());
        request.setLastName(trainer.getLastName());
        request.setIsActive(trainer.getIsActive());
        request.setTrainingDate(training.getTrainingDate());
        request.setTrainingDuration(training.getTrainingDuration());
        request.setActionType(action);
        return request;
    }
}
