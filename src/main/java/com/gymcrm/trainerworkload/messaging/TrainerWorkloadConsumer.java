package com.gymcrm.trainerworkload.messaging;

import com.gymcrm.dto.message.WorkloadResponseMessage;
import com.gymcrm.util.Constants;
import com.gymcrm.util.WorkloadStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class TrainerWorkloadConsumer {

    @RabbitListener(queues = Constants.QUEUE_RESPONSE)
    public void processWorkloadUpdate(WorkloadResponseMessage response) {
        try {
            MDC.put("transactionId", response.getTransactionId());

            if (response.getStatus() == WorkloadStatus.SUCCESS) {
                log.info("Successful workload update for: {}", response.getUsername());
            } else {
                log.error("Workload update failed for: {}", response.getUsername());
            }

        } catch (Exception e) {
            log.error("Failed to process workload response for: {}", response.getUsername(), e);
            throw e;
        }
    }
}
