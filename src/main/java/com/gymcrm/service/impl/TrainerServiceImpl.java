package com.gymcrm.service.impl;

import com.gymcrm.converter.TrainerMapper;
import com.gymcrm.dao.TrainerRepository;
import com.gymcrm.dao.TrainingTypeRepository;
import com.gymcrm.dto.UserCreatedResponseDto;
import com.gymcrm.dto.trainer.TrainerCreateRequestDto;
import com.gymcrm.dto.trainer.TrainerProfileResponseDto;
import com.gymcrm.dto.trainer.TrainerUpdateRequestDto;
import com.gymcrm.metrics.UserMetrics;
import com.gymcrm.model.Trainer;
import com.gymcrm.model.TrainingType;
import com.gymcrm.service.TrainerService;
import com.gymcrm.util.UserCredentialGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class TrainerServiceImpl implements TrainerService {
    private final UserMetrics userMetrics;

    private final TrainerRepository trainerRepository;
    private final TrainerMapper trainerMapper;
    private final TrainingTypeRepository trainingTypeRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public UserCreatedResponseDto createTrainer(TrainerCreateRequestDto dto) {
        Trainer trainer = trainerMapper.toEntity(dto);
        UserCredentialGenerator.generateUserCredentials(trainer, trainerRepository::existsByUsername);
        String generatedPassword = trainer.getPassword();

        TrainingType trainingType = trainingTypeRepository.findById(dto.getSpecializationId())
                .orElseThrow(() -> new RuntimeException("TrainingType not found"));
        trainer.setSpecialization(trainingType);

        trainer.setPassword(passwordEncoder.encode(generatedPassword));
        Trainer savedTrainer = trainerRepository.save(trainer);
        log.info("Created Trainer with ID={}, username={}", savedTrainer.getId(), savedTrainer.getUsername());
        userMetrics.incrementUserRegistration();

        // Return DTO directly, do not set plain password on entity
        return new UserCreatedResponseDto(savedTrainer.getUsername(), generatedPassword);
    }

    @Override
    public void login(String username, String password) {
        Trainer trainer = trainerRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
        if (!passwordEncoder.matches(password, trainer.getPassword())) {
            log.warn("Failed login attempt for Trainer username={}", username);
            throw new RuntimeException("Invalid credentials");
        }
        userMetrics.incrementUserLogin();
        log.info("Successful login for Trainer username={}", username);
    }

    @Override
    @Transactional
    public TrainerProfileResponseDto updateTrainer(TrainerUpdateRequestDto dto) {
        Trainer oldTrainer = trainerRepository.findByUsername(dto.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found: " + dto.getUsername()));
        oldTrainer.setFirstName(dto.getFirstName());
        oldTrainer.setLastName(dto.getLastName());
        oldTrainer.setIsActive(dto.getIsActive());

        TrainingType trainingType = trainingTypeRepository.findById(dto.getSpecializationId())
                .orElseThrow(() -> new RuntimeException("TrainingType not found"));
        oldTrainer.setSpecialization(trainingType);

        Trainer savedTrainer = trainerRepository.save(oldTrainer);
        log.info("Updated Trainer profile for username={}", oldTrainer.getUsername());
        userMetrics.incrementUserProfileUpdate();

        return trainerMapper.toProfileDTO(savedTrainer);
    }

    @Override
    public TrainerProfileResponseDto getByUsername(String username) {
        Trainer trainer = trainerRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
        log.info("Fetched profile for Trainer username={}", username);
        return trainerMapper.toProfileDTO(trainer);
    }

    @Override
    @Transactional
    public void changePassword(String username, String oldPassword, String newPassword) {
        Trainer trainer = trainerRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
        if (!passwordEncoder.matches(oldPassword, trainer.getPassword())) {
            log.warn("Failed password change attempt for Trainer username={}", username);
            throw new RuntimeException("Old password is incorrect");
        }
        UserCredentialGenerator.checkNewPassword(newPassword);
        trainer.setPassword(passwordEncoder.encode(newPassword));
        trainerRepository.save(trainer);
        log.info("Password updated for Trainer username={}", username);
    }

    @Override
    @Transactional
    public void toggleActive(String username) {
        Trainer trainer = trainerRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
        Boolean current = trainer.getIsActive();
        trainer.setIsActive(!current);
        trainerRepository.save(trainer);
        log.info("Trainer username={} toggled active from {} to {}", username, current, !current);
    }

    @Override
    public List<Trainer> getAllTrainers() {
        log.info("Fetched all trainers");
        return trainerRepository.findAll();
    }
}
