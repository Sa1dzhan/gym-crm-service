package com.gymcrm.trainerworkload;

import com.gymcrm.dto.workload.DurationRequestDto;
import com.gymcrm.dto.workload.DurationResponseDto;
import com.gymcrm.dto.workload.WorkloadRequestDto;
import org.springframework.http.ResponseEntity;

public interface TrainerWorkloadClient {
    ResponseEntity<?> updateTrainerSummary(WorkloadRequestDto request);

    ResponseEntity<DurationResponseDto> getTrainerWorkload(DurationRequestDto request);
}
