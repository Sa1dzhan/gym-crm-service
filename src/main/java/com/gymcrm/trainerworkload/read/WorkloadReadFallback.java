package com.gymcrm.trainerworkload.read;

import com.gymcrm.dto.workload.DurationRequestDto;
import com.gymcrm.dto.workload.DurationResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class WorkloadReadFallback implements WorkloadReadSummaryClient {

    @Override
    public ResponseEntity<DurationResponseDto> getTrainerWorkload(DurationRequestDto request, String authorization, String transactionId) {
        log.error("Feign fallback: get workload for {}", request.getUsername());
        DurationResponseDto fallbackResponse = new DurationResponseDto();
        return ResponseEntity.internalServerError().body(fallbackResponse);
    }
}
