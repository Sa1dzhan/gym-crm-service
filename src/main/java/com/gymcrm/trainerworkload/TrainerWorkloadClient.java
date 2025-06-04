package com.gymcrm.trainerworkload;

import com.gymcrm.dto.workload.WorkloadRequestDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Primary;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "workload-service", url = "http://localhost:8081", fallback = TrainerWorkloadClientFallback.class)
@Primary
public interface TrainerWorkloadClient {

    @PostMapping("/api/v1/workload/update")
    ResponseEntity<?> updateTrainerWorkload(
            @RequestBody WorkloadRequestDto request,
            @RequestHeader("Authorization") String token,
            @RequestHeader("transactionId") String transactionId
    );
}
