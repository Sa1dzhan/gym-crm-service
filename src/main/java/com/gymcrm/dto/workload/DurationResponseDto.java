package com.gymcrm.dto.workload;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DurationResponseDto {
    @NotNull
    private String username;

    @NotNull
    private Long duration;
}
