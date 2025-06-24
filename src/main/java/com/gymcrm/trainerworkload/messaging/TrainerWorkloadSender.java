package com.gymcrm.trainerworkload.messaging;

import com.gymcrm.dto.message.WorkloadMessage;
import com.gymcrm.dto.message.WorkloadResponseMessage;
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

    public void sendWorkloadUpdate(WorkloadRequestDto request) {
        String transactionId = MDC.get("transactionId");

        WorkloadMessage<WorkloadRequestDto> msg = WorkloadMessage.<WorkloadRequestDto>builder()
                .username(request.getUsername())
                .transactionId(transactionId)
                .payload(request)
                .build();


        WorkloadResponseMessage response = (WorkloadResponseMessage) rabbitTemplate.convertSendAndReceive(Constants.QUEUE_UPDATE, msg);
        log.info("Reply received for user {} with status {}", response.getUsername(), response.getStatus());
    }
}
