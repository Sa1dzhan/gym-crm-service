package com.gymcrm.service;

import com.gymcrm.dto.UserCreatedResponseDto;
import com.gymcrm.dto.trainer.TrainerCreateRequestDto;
import com.gymcrm.dto.trainer.TrainerProfileResponseDto;
import com.gymcrm.dto.trainer.TrainerUpdateRequestDto;
import com.gymcrm.model.Trainer;

import java.util.List;

public interface TrainerService {
    UserCreatedResponseDto createTrainer(TrainerCreateRequestDto trainer);

    void login(String username, String password);

    TrainerProfileResponseDto updateTrainer(TrainerUpdateRequestDto trainer);

    TrainerProfileResponseDto getTrainer(Long id);

    TrainerProfileResponseDto getByUsername(String username, String password);

    void changePassword(String username, String oldPassword, String newPassword);

    void toggleActive(String username, String password);

    List<Trainer> getAllTrainers();
}
