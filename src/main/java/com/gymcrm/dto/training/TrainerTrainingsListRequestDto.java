package com.gymcrm.dto.training;

import com.gymcrm.dto.UserCreatedResponseDto;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class TrainerTrainingsListRequestDto extends UserCreatedResponseDto {

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate periodFrom;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate periodTo;

    private String traineeName;
}
