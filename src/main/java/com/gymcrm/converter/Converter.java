package com.gymcrm.converter;

import com.gymcrm.dto.AuthenticatedRequestDto;
import com.gymcrm.dto.trainee.*;
import com.gymcrm.dto.trainer.TrainerCreateRequestDto;
import com.gymcrm.dto.trainer.TrainerProfileResponseDto;
import com.gymcrm.dto.trainer.TrainerShortProfileDto;
import com.gymcrm.dto.trainer.TrainerUpdateRequestDto;
import com.gymcrm.dto.training.TraineeTrainingsListResponseDto;
import com.gymcrm.dto.training.TrainerTrainingsListResponseDto;
import com.gymcrm.dto.training_type.TrainingTypeDto;
import com.gymcrm.model.Trainee;
import com.gymcrm.model.Trainer;
import com.gymcrm.model.Training;
import com.gymcrm.model.TrainingType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public abstract class Converter {
    public abstract TraineeProfileResponseDto toProfileDTO(Trainee source);

    public abstract TrainerProfileResponseDto toProfileDTO(Trainer source);

    public abstract Trainee toEntity(TraineeCreateRequestDto source);

    public abstract Trainee toEntity(TraineeUpdateRequestDto source);

    @Mappings({
            @Mapping(target = "username", source = "username"),
            @Mapping(target = "password", source = "password")
    })
    public abstract AuthenticatedRequestDto toRegisteredDto(Trainee source);

    @Mappings({
            @Mapping(target = "username", source = "username"),
            @Mapping(target = "password", source = "password")
    })
    public abstract AuthenticatedRequestDto toRegisteredDto(Trainer source);

    public abstract Trainer toEntity(TrainerCreateRequestDto source);

    public abstract Trainer toEntity(TrainerUpdateRequestDto source);

    public abstract AddTrainingRequestDto toResponseDTO(Training source);

    public abstract Training toEntity(AddTrainingRequestDto source);

    public abstract TrainingTypeDto toResponseDTO(TrainingType source);

    public abstract TrainingType toEntity(TrainingTypeDto source);

    public abstract TraineeShortProfileDto toShortProfileDto(Trainee source);

    public abstract TrainerShortProfileDto toShortProfileDto(Trainer source);

    public abstract TraineeTrainingsListResponseDto toTraineeTrainingsListDto(Training source);

    public abstract TrainerTrainingsListResponseDto toTrainerTrainingsListDto(Training source);
}
