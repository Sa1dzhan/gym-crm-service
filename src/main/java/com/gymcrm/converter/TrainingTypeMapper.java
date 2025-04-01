package com.gymcrm.converter;

import com.gymcrm.dto.training_type.TrainingTypeDto;
import com.gymcrm.model.TrainingType;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TrainingTypeMapper {
    TrainingTypeDto toResponseDTO(TrainingType source);

    TrainingType toEntity(TrainingTypeDto source);
}

