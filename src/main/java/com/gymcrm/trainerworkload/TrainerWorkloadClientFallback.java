package com.gymcrm.trainerworkload;

import com.gymcrm.dto.workload.WorkloadRequestDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class TrainerWorkloadClientFallback implements TrainerWorkloadClient {

    @Override
    public ResponseEntity<?> updateTrainerWorkload(WorkloadRequestDto request,
                                                   String token, String transactionId) {
        log.error("TransactionID: {} - Failed to update trainer workload for {}", transactionId, request.getUsername());

        return ResponseEntity.internalServerError().build();
    }
}
