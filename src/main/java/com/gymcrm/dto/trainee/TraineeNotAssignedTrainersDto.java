package com.gymcrm.dto.trainee;

import com.gymcrm.dto.trainer.TrainerShortProfileDto;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class TraineeNotAssignedTrainersDto {
    List<TrainerShortProfileDto> trainers;
}
