package com.gymcrm.trainerworkload.messaging;

import com.gymcrm.dto.message.WorkloadMessage;
import com.gymcrm.dto.workload.WorkloadRequestDto;
import com.gymcrm.util.Constants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TrainerWorkloadSender {

    private final RabbitTemplate rabbitTemplate;
    private final static String QUEUE_NAME = Constants.QUEUE_UPDATE;

    public void sendWorkloadUpdate(WorkloadRequestDto request) {
        String transactionId = MDC.get("transactionId");
        WorkloadMessage msg = WorkloadMessage.builder()
                .username(request.getUsername())
                .transactionId(transactionId)
                .payload(request)
                .build();

        log.info("Sending workload update to queue for: {}", request.getUsername());
        rabbitTemplate.convertAndSend(QUEUE_NAME, msg);
    }
}
