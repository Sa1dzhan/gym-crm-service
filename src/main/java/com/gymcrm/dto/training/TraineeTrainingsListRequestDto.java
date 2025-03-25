package com.gymcrm.dto.training;

import com.gymcrm.dto.AuthenticatedRequestDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class TraineeTrainingsListRequestDto extends AuthenticatedRequestDto {
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date periodFrom;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date periodTo;

    private String trainerName;

    private String trainingTypeName;
}
