package com.gymcrm.trainerworkload.rest;

import com.gymcrm.config.JwtUtil;
import com.gymcrm.dto.workload.DurationRequestDto;
import com.gymcrm.dto.workload.DurationResponseDto;
import com.gymcrm.dto.workload.WorkloadRequestDto;
import com.gymcrm.trainerworkload.rest.read.WorkloadReadSummaryClient;
import com.gymcrm.trainerworkload.rest.update.WorkloadUpdateSummaryClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class TrainerWorkloadREST {

    private final WorkloadUpdateSummaryClient updateClient;

    private final WorkloadReadSummaryClient readClient;

    private final JwtUtil jwtUtil;

    private final String service = "workload";

    public ResponseEntity<?> updateTrainerSummary(WorkloadRequestDto request) {
        String transactionId = MDC.get("transactionId");
        String authToken = jwtUtil.generateToken(service.concat(request.getUsername()));

        log.info("Sending workload update request for: {}", request.getUsername());
        return updateClient.updateTrainerSummary(request, authToken, transactionId);
    }

    public ResponseEntity<DurationResponseDto> getTrainerWorkload(DurationRequestDto request) {
        String transactionId = MDC.get("transactionId");
        String authToken = jwtUtil.generateToken(request.getUsername());

        log.info("Requesting workload summary for: {}", request.getUsername());
        return readClient.getTrainerWorkload(request, authToken, transactionId);
    }
}
