package com.gymcrm.dto.message;

import com.gymcrm.util.WorkloadStatus;
import lombok.Data;

@Data
public class WorkloadResponseMessage {
    private String transactionId;
    private String username;
    private WorkloadStatus status;
}
