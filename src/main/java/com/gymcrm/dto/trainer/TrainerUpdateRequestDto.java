package com.gymcrm.dto.trainer;

import com.gymcrm.dto.UserCreatedResponseDto;
import com.gymcrm.dto.training_type.TrainingTypeDto;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Builder
public class TrainerUpdateRequestDto extends UserCreatedResponseDto {

    @NotNull
    private String firstName;

    @NotNull
    private String lastName;

    @NotNull
    private TrainingTypeDto specialization;

    @NotNull
    private Boolean isActive;
}
