package com.gymcrm.trainerworkload.update;

import com.gymcrm.dto.workload.WorkloadRequestDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Primary;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;


@FeignClient(name = "workload-service", path = "/api/v1/workload",
        contextId = "workload-update", fallback = WorkloadUpdateFallback.class)
@Primary
public interface WorkloadUpdateSummaryClient {

    @PostMapping("/update")
    ResponseEntity<?> updateTrainerSummary(
            @RequestBody WorkloadRequestDto request,
            @RequestHeader("Authorization") String authorization,
            @RequestHeader("transactionId") String transactionId
    );
}
