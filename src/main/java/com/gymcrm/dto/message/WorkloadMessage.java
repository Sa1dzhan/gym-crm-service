package com.gymcrm.dto.message;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WorkloadMessage<T> {
    private String username;
    private String transactionId;
    private T payload;
}
