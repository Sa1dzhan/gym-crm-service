package com.gymcrm.dto.trainer;

import com.gymcrm.dto.training_type.TrainingTypeDto;
import lombok.Data;

@Data
public class TrainerShortProfileDto {
    private String username;
    private String firstName;
    private String lastName;
    private TrainingTypeDto specialization;
}
