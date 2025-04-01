package com.gymcrm.dto.trainee;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TraineeCreateRequestDto {
    @NotNull
    private String firstName;

    @NotNull
    private String lastName;

    private Date dateOfBirth;

    private String address;
}
