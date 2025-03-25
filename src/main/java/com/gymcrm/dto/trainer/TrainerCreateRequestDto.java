package com.gymcrm.dto.trainer;

import com.gymcrm.dto.training_type.TrainingTypeDto;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrainerCreateRequestDto {
    @NotNull
    private String firstName;
    @NotNull
    private String lastName;
    @NotNull
    private TrainingTypeDto specialization;
}
