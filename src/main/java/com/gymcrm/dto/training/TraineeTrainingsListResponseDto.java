package com.gymcrm.dto.training;

import com.gymcrm.dto.training_type.TrainingTypeDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TraineeTrainingsListResponseDto {

    private String trainingName;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date trainingDate;

    private TrainingTypeDto trainingType;

    private Long trainingDuration;

    private String trainerName;
}
