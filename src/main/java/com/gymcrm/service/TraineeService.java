package com.gymcrm.service;

import com.gymcrm.dto.UserCreatedResponseDto;
import com.gymcrm.dto.trainee.TraineeCreateRequestDto;
import com.gymcrm.dto.trainee.TraineeNotAssignedTrainersDto;
import com.gymcrm.dto.trainee.TraineeProfileResponseDto;
import com.gymcrm.dto.trainee.TraineeUpdateRequestDto;
import com.gymcrm.dto.trainer.TrainerShortProfileDto;

import java.util.List;

public interface TraineeService {
    UserCreatedResponseDto createTrainee(TraineeCreateRequestDto trainee);

    void login(String username, String password);

    TraineeProfileResponseDto updateTrainee(TraineeUpdateRequestDto trainee);

    TraineeProfileResponseDto getByUsername(String username);

    void changePassword(String username, String oldPassword, String newPassword);

    void toggleActive(String username);

    void deleteTraineeByUsername(String username, String password);

    TraineeNotAssignedTrainersDto getTrainersNotAssigned(String username);

    List<TrainerShortProfileDto> updateTrainersList(String username, List<String> trainerUsernames);
}
