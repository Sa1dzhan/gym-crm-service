package com.gymcrm.converter;

import com.gymcrm.dto.UserCreatedResponseDto;
import com.gymcrm.dto.trainer.TrainerCreateRequestDto;
import com.gymcrm.dto.trainer.TrainerProfileResponseDto;
import com.gymcrm.dto.trainer.TrainerShortProfileDto;
import com.gymcrm.dto.trainer.TrainerUpdateRequestDto;
import com.gymcrm.model.Trainer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface TrainerMapper {
    TrainerProfileResponseDto toProfileDTO(Trainer source);

    @Mapping(target = "specialization", ignore = true)
    Trainer toEntity(TrainerCreateRequestDto source);

    @Mapping(target = "specialization", ignore = true)
    Trainer toEntity(TrainerUpdateRequestDto source);

    @Mappings({
            @Mapping(target = "username", source = "username"),
            @Mapping(target = "password", source = "password")
    })
    UserCreatedResponseDto toRegisteredDto(Trainer source);

    TrainerShortProfileDto toShortProfileDto(Trainer source);
}
