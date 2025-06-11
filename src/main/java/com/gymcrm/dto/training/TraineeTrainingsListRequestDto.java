package com.gymcrm.dto.training;

import com.gymcrm.dto.UserCreatedResponseDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class TraineeTrainingsListRequestDto extends UserCreatedResponseDto {
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate periodFrom;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate periodTo;

    private String trainerName;

    private String trainingTypeName;
}
