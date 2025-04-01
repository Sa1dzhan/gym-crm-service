package com.gymcrm.converter;

import com.gymcrm.dto.UserCreatedResponseDto;
import com.gymcrm.dto.trainee.TraineeCreateRequestDto;
import com.gymcrm.dto.trainee.TraineeProfileResponseDto;
import com.gymcrm.dto.trainee.TraineeShortProfileDto;
import com.gymcrm.dto.trainee.TraineeUpdateRequestDto;
import com.gymcrm.model.Trainee;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface TraineeMapper {
    TraineeProfileResponseDto toProfileDTO(Trainee source);

    Trainee toEntity(TraineeCreateRequestDto source);

    Trainee toEntity(TraineeUpdateRequestDto source);

    @Mappings({
            @Mapping(target = "username", source = "username"),
            @Mapping(target = "password", source = "password")
    })
    UserCreatedResponseDto toRegisteredDto(Trainee source);

    TraineeShortProfileDto toShortProfileDto(Trainee source);
}

