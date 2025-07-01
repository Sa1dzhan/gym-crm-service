package com.gymcrm.service.impl;

import com.gymcrm.client.WorkloadClient;
import com.gymcrm.dto.workload.DurationRequestDto;
import com.gymcrm.dto.workload.DurationResponseDto;
import com.gymcrm.dto.workload.WorkloadRequestDto;
import com.gymcrm.model.Trainer;
import com.gymcrm.model.Training;
import com.gymcrm.service.WorkloadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class WorkloadServiceImpl implements WorkloadService {
    private final WorkloadClient workloadClient;

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

    public ResponseEntity<DurationResponseDto> getTrainerWorkload(DurationRequestDto request) {
        try {
            log.info("Processing workload query for: {}", request.getUsername());
            return workloadClient.getTrainerWorkload(request);
        } catch (Exception e) {
            log.error("Error processing workload query: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
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
