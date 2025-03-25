package com.gymcrm.dto.trainee;

import com.gymcrm.dto.trainer.TrainerShortProfileDto;
import lombok.Data;

import java.util.Date;
import java.util.List;


@Data
public class TraineeProfileResponseDto {
    private Long id;
    private String username;
    private String firstName;
    private String lastName;
    private Date dateOfBirth;
    private String address;
    private Boolean isActive;
    List<TrainerShortProfileDto> trainers;

}
