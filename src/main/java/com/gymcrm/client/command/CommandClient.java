package com.gymcrm.client.command;

import com.gymcrm.dto.workload.WorkloadRequestDto;

public interface CommandClient {
    void updateTrainerSummary(WorkloadRequestDto request);
}
