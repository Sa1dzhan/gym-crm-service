package com.gymcrm.service.impl;

import com.gymcrm.dto.workload.WorkloadRequestDto;
import com.gymcrm.model.Training;
import com.gymcrm.service.WorkloadService;
import com.gymcrm.trainerworkload.TrainerWorkloadClient;
import com.gymcrm.util.ActionType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class WorkloadServiceImpl implements WorkloadService {
    private final TrainerWorkloadClient workloadClient;

    @Override
    public void updateTrainerWorkload(Training training, ActionType action) {
        try {
            WorkloadRequestDto request = new WorkloadRequestDto(training, action);

            log.info("Sending workload update request for: {}", training.getTrainer().getUsername());
            ResponseEntity<?> response = workloadClient.updateTrainerSummary(request);

            if (HttpStatus.OK.equals(response.getStatusCode())) {
                log.info("Successfully updated workload");
            } else {
                log.warn("Failed to update workload: {}", response.getBody());
            }
        } catch (Exception e) {
            log.error("Error updating workload: {}", e.getMessage(), e);
        }
    }
}
