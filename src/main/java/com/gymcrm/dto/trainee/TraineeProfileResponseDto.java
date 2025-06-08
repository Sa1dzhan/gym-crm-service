package com.gymcrm.dto.trainee;

import com.gymcrm.dto.trainer.TrainerShortProfileDto;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.List;


@Data
public class TraineeProfileResponseDto {
    private Long id;
    private String username;
    private String firstName;
    private String lastName;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateOfBirth;
    private String address;
    private Boolean isActive;
    List<TrainerShortProfileDto> trainers;

}
