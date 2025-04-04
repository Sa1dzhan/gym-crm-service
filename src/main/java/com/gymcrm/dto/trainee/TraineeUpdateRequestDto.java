package com.gymcrm.dto.trainee;

import com.gymcrm.dto.UserCreatedResponseDto;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class TraineeUpdateRequestDto extends UserCreatedResponseDto {

    @NotNull
    private String firstName;

    @NotNull
    private String lastName;

    private Date dateOfBirth;

    private String address;

    @NotNull
    private Boolean isActive;
}
