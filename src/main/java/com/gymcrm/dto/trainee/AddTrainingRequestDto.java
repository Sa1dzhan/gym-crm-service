package com.gymcrm.dto.trainee;

import com.gymcrm.dto.UserCreatedResponseDto;
import com.gymcrm.dto.training_type.TrainingTypeDto;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class AddTrainingRequestDto extends UserCreatedResponseDto {
    @NotNull
    private String traineeUsername;

    @NotNull
    private String trainerUsername;

    @NotNull
    private String trainingName;

    @NotNull
    private TrainingTypeDto trainingType;

    @NotNull
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate trainingDate;

    @NotNull
    private Long trainingDuration;
}
