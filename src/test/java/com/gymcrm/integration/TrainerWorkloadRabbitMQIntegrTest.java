package com.gymcrm.integration;

import com.gymcrm.client.command.CommandClient;
import com.gymcrm.dto.message.WorkloadMessage;
import com.gymcrm.dto.message.WorkloadResponseMessage;
import com.gymcrm.dto.workload.WorkloadRequestDto;
import com.gymcrm.util.Constants;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
public class TrainerWorkloadRabbitMQIntegrTest {

    @Autowired
    private CommandClient commandClient;

    @MockBean
    private RabbitTemplate rabbitTemplate;

    @Test
    void shouldAttemptToSendUpdateSummaryMessage() {
        WorkloadResponseMessage mockResponse = new WorkloadResponseMessage();
        mockResponse.setStatus(WorkloadResponseMessage.WorkloadStatus.SUCCESS);
        mockResponse.setUsername("mock.trainer");
        when(rabbitTemplate.convertSendAndReceive(anyString(), any(WorkloadMessage.class)))
                .thenReturn(mockResponse);

        WorkloadRequestDto request = new WorkloadRequestDto();
        request.setUsername("mock.trainer");
        request.setFirstName("Mock");
        request.setLastName("Trainer");
        request.setIsActive(true);
        request.setTrainingDuration(120L);

        commandClient.updateTrainerSummary(request);

        ArgumentCaptor<WorkloadMessage> messageCaptor = ArgumentCaptor.forClass(WorkloadMessage.class);
        verify(rabbitTemplate).convertSendAndReceive(
                (String) org.mockito.ArgumentMatchers.eq(Constants.QUEUE_UPDATE),
                messageCaptor.capture()
        );

        WorkloadMessage<?> capturedMessage = messageCaptor.getValue();
        assertEquals("mock.trainer", capturedMessage.getUsername());

        WorkloadRequestDto payload = (WorkloadRequestDto) capturedMessage.getPayload();
        assertEquals("Mock", payload.getFirstName());
        assertEquals(120, payload.getTrainingDuration());
    }
}
