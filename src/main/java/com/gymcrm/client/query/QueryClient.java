package com.gymcrm.client.query;

import com.gymcrm.dto.workload.DurationRequestDto;
import com.gymcrm.dto.workload.DurationResponseDto;
import org.springframework.http.ResponseEntity;

public interface QueryClient {
    ResponseEntity<DurationResponseDto> getTrainerWorkload(DurationRequestDto request);
}
