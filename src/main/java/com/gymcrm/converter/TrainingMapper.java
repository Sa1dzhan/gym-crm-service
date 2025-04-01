package com.gymcrm.converter;

import com.gymcrm.dto.trainee.AddTrainingRequestDto;
import com.gymcrm.dto.training.TraineeTrainingsListResponseDto;
import com.gymcrm.dto.training.TrainerTrainingsListResponseDto;
import com.gymcrm.model.Training;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TrainingMapper {
    AddTrainingRequestDto toResponseDTO(Training source);

    Training toEntity(AddTrainingRequestDto source);

    TraineeTrainingsListResponseDto toTraineeTrainingsListDto(Training source);

    TrainerTrainingsListResponseDto toTrainerTrainingsListDto(Training source);
}

