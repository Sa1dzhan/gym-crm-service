package com.gymcrm.client;

import com.gymcrm.client.command.CommandClient;
import com.gymcrm.client.query.QueryClient;
import com.gymcrm.dto.workload.DurationRequestDto;
import com.gymcrm.dto.workload.DurationResponseDto;
import com.gymcrm.dto.workload.WorkloadRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class WorkloadClientImpl implements WorkloadClient {
    private final CommandClient commandClient;
    private final QueryClient queryClient;

    @Override
    public void updateTrainerSummary(WorkloadRequestDto request) {
        // RabbitMQ for updates
        try {
            commandClient.updateTrainerSummary(request);
        } catch (Exception e) {
            log.error("Failed to send update via RabbitMQ", e);
        }
    }

    @Override
    public ResponseEntity<DurationResponseDto> getTrainerWorkload(DurationRequestDto request) {
        // REST for reads
        return queryClient.getTrainerWorkload(request);
    }
}
