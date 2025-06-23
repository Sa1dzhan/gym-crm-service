package com.gymcrm.trainerworkload;

import com.gymcrm.dto.workload.DurationRequestDto;
import com.gymcrm.dto.workload.DurationResponseDto;
import com.gymcrm.dto.workload.WorkloadRequestDto;
import com.gymcrm.trainerworkload.messaging.TrainerWorkloadSender;
import com.gymcrm.trainerworkload.rest.TrainerWorkloadREST;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class TrainerWorkloadClientImpl implements TrainerWorkloadClient {
    private final TrainerWorkloadSender rabbitMQSender;
    private final TrainerWorkloadREST restClient;

    @Override
    public ResponseEntity<?> updateTrainerSummary(WorkloadRequestDto request) {
        // RabbitMQ for updates
        try {
            rabbitMQSender.sendWorkloadUpdate(request);
            return ResponseEntity.accepted().build();
        } catch (Exception e) {
            log.error("Failed to send update via RabbitMQ", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @Override
    public ResponseEntity<DurationResponseDto> getTrainerWorkload(DurationRequestDto request) {
        // REST for reads
        return restClient.getTrainerWorkload(request);
    }
}
