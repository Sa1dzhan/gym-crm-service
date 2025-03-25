package com.gymcrm.service.impl;

import com.gymcrm.converter.Converter;
import com.gymcrm.dao.TrainerRepository;
import com.gymcrm.dto.AuthenticatedRequestDto;
import com.gymcrm.dto.trainer.TrainerCreateRequestDto;
import com.gymcrm.dto.trainer.TrainerProfileResponseDto;
import com.gymcrm.dto.trainer.TrainerUpdateRequestDto;
import com.gymcrm.model.Trainer;
import com.gymcrm.service.TrainerService;
import com.gymcrm.util.Authentication;
import com.gymcrm.util.UserCredentialGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class TrainerServiceImpl implements TrainerService {

    private final TrainerRepository trainerRepository;
    private final Converter converter;

    @Override
    @Transactional
    public AuthenticatedRequestDto createTrainer(TrainerCreateRequestDto dto) {
        Trainer trainer = converter.toEntity(dto);
        UserCredentialGenerator.generateUserCredentials(trainer, trainerRepository::existsByUsername);

        Trainer savedTrainee = trainerRepository.save(trainer);
        log.info("Created Trainer with ID={}, username={}", savedTrainee.getId(), savedTrainee.getUsername());

        return converter.toRegisteredDto(savedTrainee);
    }

    @Override
    public void login(String username, String password) {
        Authentication.authenticateUser(username, password, trainerRepository::findByUsername);
    }

    @Override
    @Transactional
    public TrainerProfileResponseDto updateTrainer(TrainerUpdateRequestDto dto) {
        Trainer trainer = converter.toEntity(dto);
        Authentication.authenticateUser(trainer.getUsername(), trainer.getPassword(), trainerRepository::findByUsername);

        Trainer savedTrainer = trainerRepository.save(trainer);
        log.info("Updated {}", savedTrainer);
        return converter.toProfileDTO(savedTrainer);
    }

    @Override
    public TrainerProfileResponseDto getTrainer(Long id) {
        return converter.toProfileDTO(
                trainerRepository.findById(id).orElseThrow(() -> new RuntimeException("No trainer found"))
        );
    }

    @Override
    public TrainerProfileResponseDto getByUsername(String username, String password) {
        Trainer trainer = Authentication.authenticateUser(username, password, trainerRepository::findByUsername);
        return converter.toProfileDTO(trainer);
    }

    @Override
    @Transactional
    public void changePassword(String username, String oldPassword, String newPassword) {
        Trainer trainer = Authentication.authenticateUser(username, oldPassword, trainerRepository::findByUsername);
        UserCredentialGenerator.checkNewPassword(newPassword);

        trainer.setPassword(newPassword);
        trainerRepository.save(trainer);

        log.info("Update password for {}", username);
    }

    @Override
    @Transactional
    public void toggleActive(String username, String password) {
        Trainer trainer = Authentication.authenticateUser(username, password, trainerRepository::findByUsername);

        Boolean current = trainer.getIsActive();
        trainer.setIsActive(!current);
        trainerRepository.save(trainer);

        log.info("Username = {} toggled from {} to {}",
                username, current, !current);
    }


    @Override
    public List<Trainer> getAllTrainers() {
        return trainerRepository.findAll();
    }
}
