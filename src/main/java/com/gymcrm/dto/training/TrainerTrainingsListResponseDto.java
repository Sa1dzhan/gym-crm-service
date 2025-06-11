package com.gymcrm.dto.training;

import com.gymcrm.dto.training_type.TrainingTypeDto;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
public class TrainerTrainingsListResponseDto {
    private String trainingName;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate trainingDate;
    private TrainingTypeDto trainingType;
    private Long trainingDuration;
    private String traineeName;
}
