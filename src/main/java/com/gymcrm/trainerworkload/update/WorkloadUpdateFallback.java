package com.gymcrm.trainerworkload.update;

import com.gymcrm.dto.workload.WorkloadRequestDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class WorkloadUpdateFallback implements WorkloadUpdateSummaryClient {
    @Override
    public ResponseEntity<?> updateTrainerSummary(WorkloadRequestDto request, String authorization, String transactionId) {
        log.error("Feign fallback: update workload for {}", request.getUsername());
        return ResponseEntity.internalServerError().build();
    }
}
