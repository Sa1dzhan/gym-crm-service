package com.gymcrm.integration.service;

import com.gymcrm.client.WorkloadClient;
import com.gymcrm.dto.workload.WorkloadRequestDto;
import com.gymcrm.model.Trainer;
import com.gymcrm.model.Training;
import com.gymcrm.service.WorkloadService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@SpringBootTest
@Transactional
public class WorkloadServiceIntegrTest {

    @Autowired
    private WorkloadService workloadService;

    @MockBean
    private WorkloadClient workloadClient;

    @Test
    void updateTrainerWorkload() {
        Trainer trainer = new Trainer();
        trainer.setUsername("workload.trainer");
        trainer.setFirstName("Workload");
        trainer.setLastName("Trainer");
        trainer.setIsActive(true);

        Training training = new Training();
        training.setTrainer(trainer);
        training.setTrainingDate(LocalDate.of(2025, 10, 10));
        training.setTrainingDuration(55L);

        workloadService.updateTrainerWorkload(training, WorkloadRequestDto.ActionType.ADD);

        ArgumentCaptor<WorkloadRequestDto> captor = ArgumentCaptor.forClass(WorkloadRequestDto.class);
        verify(workloadClient, times(1)).updateTrainerSummary(captor.capture());

        WorkloadRequestDto capturedRequest = captor.getValue();
        assertThat(capturedRequest.getUsername()).isEqualTo("workload.trainer");
        assertThat(capturedRequest.getTrainingDuration()).isEqualTo(55L);
        assertThat(capturedRequest.getActionType()).isEqualTo(WorkloadRequestDto.ActionType.ADD);
    }

    @Test
    void updateTrainerWorkload_whenClientFails() {
        Training training = new Training();
        training.setTrainer(new Trainer());

        doThrow(new RuntimeException()).when(workloadClient).updateTrainerSummary(any());

        workloadService.updateTrainerWorkload(training, WorkloadRequestDto.ActionType.ADD);
    }
}
