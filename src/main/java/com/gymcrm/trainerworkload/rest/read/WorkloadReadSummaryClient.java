package com.gymcrm.trainerworkload.rest.read;

import com.gymcrm.dto.workload.DurationRequestDto;
import com.gymcrm.dto.workload.DurationResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Primary;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;


@FeignClient(name = "workload-service", path = "/api/v1/workload",
        contextId = "workload-read", fallback = WorkloadReadFallback.class)
@Primary
public interface WorkloadReadSummaryClient {

    @GetMapping
    ResponseEntity<DurationResponseDto> getTrainerWorkload(
            @RequestBody DurationRequestDto request,
            @RequestHeader("Authorization") String authorization,
            @RequestHeader("transactionId") String transactionId
    );
}
