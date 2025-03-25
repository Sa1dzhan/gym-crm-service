package com.gymcrm.dto.trainer;

import com.gymcrm.dto.trainee.TraineeShortProfileDto;
import com.gymcrm.dto.training_type.TrainingTypeDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrainerProfileResponseDto {
    private Long id;
    private String username;
    private String firstName;
    private String lastName;
    private TrainingTypeDto specialization;
    private Boolean isActive;
    private List<TraineeShortProfileDto> trainees;
}
