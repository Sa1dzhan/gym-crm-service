package com.gymcrm.service;

import com.gymcrm.dto.AuthenticatedRequestDto;
import com.gymcrm.dto.trainee.TraineeCreateRequestDto;
import com.gymcrm.dto.trainee.TraineeNotAssignedTrainersDto;
import com.gymcrm.dto.trainee.TraineeProfileResponseDto;
import com.gymcrm.dto.trainee.TraineeUpdateRequestDto;
import com.gymcrm.dto.trainer.TrainerShortProfileDto;

import java.util.List;

public interface TraineeService {
    AuthenticatedRequestDto createTrainee(TraineeCreateRequestDto trainee);

    void login(String username, String password);

    TraineeProfileResponseDto updateTrainee(TraineeUpdateRequestDto trainee);

    TraineeProfileResponseDto getTrainee(Long id);

    TraineeProfileResponseDto getByUsername(String username, String password);

    void changePassword(String username, String oldPassword, String newPassword);

    void toggleActive(String username, String password);

    void deleteTraineeByUsername(String username, String password);

    TraineeNotAssignedTrainersDto getTrainersNotAssigned(String username, String password);

    List<TrainerShortProfileDto> updateTrainersList(String username, String password, List<String> trainerUsernames);
}
