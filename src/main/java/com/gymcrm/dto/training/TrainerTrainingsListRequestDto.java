package com.gymcrm.dto.training;

import com.gymcrm.dto.AuthenticatedRequestDto;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class TrainerTrainingsListRequestDto extends AuthenticatedRequestDto {

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date periodFrom;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date periodTo;

    private String traineeName;
}
