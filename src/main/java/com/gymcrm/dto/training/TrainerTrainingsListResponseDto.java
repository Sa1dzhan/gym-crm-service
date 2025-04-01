package com.gymcrm.dto.training;

import com.gymcrm.dto.training_type.TrainingTypeDto;
import lombok.Data;

import java.util.Date;

@Data
public class TrainerTrainingsListResponseDto {
    private String trainingName;
    private Date trainingDate;
    private TrainingTypeDto trainingType;
    private Long trainingDuration;
    private String traineeName;
}
