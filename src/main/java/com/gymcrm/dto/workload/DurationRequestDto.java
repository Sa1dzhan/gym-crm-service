package com.gymcrm.dto.workload;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class DurationRequestDto {
    @NotNull
    private String username;

    @NotNull
    public LocalDate trainingDate;
}
