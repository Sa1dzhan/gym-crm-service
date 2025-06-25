package com.gymcrm.client;

import com.gymcrm.dto.workload.DurationRequestDto;
import com.gymcrm.dto.workload.DurationResponseDto;
import com.gymcrm.dto.workload.WorkloadRequestDto;
import org.springframework.http.ResponseEntity;

public interface WorkloadClient {
    void updateTrainerSummary(WorkloadRequestDto request);

    ResponseEntity<DurationResponseDto> getTrainerWorkload(DurationRequestDto request);
}
