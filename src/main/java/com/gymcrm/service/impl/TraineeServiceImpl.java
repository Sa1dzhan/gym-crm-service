package com.gymcrm.service.impl;

import com.gymcrm.converter.TraineeMapper;
import com.gymcrm.converter.TrainerMapper;
import com.gymcrm.dao.TraineeRepository;
import com.gymcrm.dao.TrainerRepository;
import com.gymcrm.dto.UserCreatedResponseDto;
import com.gymcrm.dto.trainee.TraineeCreateRequestDto;
import com.gymcrm.dto.trainee.TraineeNotAssignedTrainersDto;
import com.gymcrm.dto.trainee.TraineeProfileResponseDto;
import com.gymcrm.dto.trainee.TraineeUpdateRequestDto;
import com.gymcrm.dto.trainer.TrainerShortProfileDto;
import com.gymcrm.metrics.UserMetrics;
import com.gymcrm.model.Trainee;
import com.gymcrm.model.Trainer;
import com.gymcrm.service.TraineeService;
import com.gymcrm.util.UserCredentialGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class TraineeServiceImpl implements TraineeService {
    private final UserMetrics userMetrics;

    private final TraineeRepository traineeRepository;
    private final TrainerRepository trainerRepository;
    private final TraineeMapper traineeMapper;
    private final TrainerMapper trainerMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public UserCreatedResponseDto createTrainee(TraineeCreateRequestDto dto) {
        Trainee trainee = traineeMapper.toEntity(dto);
        UserCredentialGenerator.generateUserCredentials(trainee, traineeRepository::existsByUsername);
        String generatedPassword = trainee.getPassword();
        trainee.setPassword(passwordEncoder.encode(generatedPassword));

        Trainee savedTrainee = traineeRepository.save(trainee);
        log.info("Created Trainee with ID={}, username={}", savedTrainee.getId(), savedTrainee.getUsername());
        userMetrics.incrementUserRegistration();

        return new UserCreatedResponseDto(savedTrainee.getUsername(), generatedPassword);
    }

    @Override
    public void login(String username, String password) {
        Trainee trainee = traineeRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
        if (!passwordEncoder.matches(password, trainee.getPassword())) {
            log.warn("Failed login attempt for Trainee username={}", username);
            throw new RuntimeException("Invalid credentials");
        }
        userMetrics.incrementUserLogin();
        log.info("Successful login for Trainee username={}", username);
    }

    @Override
    @Transactional
    public TraineeProfileResponseDto updateTrainee(TraineeUpdateRequestDto dto) {
        Trainee oldTrainee = traineeRepository.findByUsername(dto.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found: " + dto.getUsername()));
        oldTrainee.setFirstName(dto.getFirstName());
        oldTrainee.setLastName(dto.getLastName());
        Trainee savedTrainee = traineeRepository.save(oldTrainee);
        log.info("Updated Trainee profile for username={}", oldTrainee.getUsername());
        userMetrics.incrementUserProfileUpdate();
        return traineeMapper.toProfileDTO(savedTrainee);
    }

    @Override
    public TraineeProfileResponseDto getByUsername(String username) {
        Trainee trainee = traineeRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
        log.info("Fetched profile for Trainee username={}", username);
        return traineeMapper.toProfileDTO(trainee);
    }

    @Override
    @Transactional
    public void changePassword(String username, String oldPassword, String newPassword) {
        Trainee trainee = traineeRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
        if (!passwordEncoder.matches(oldPassword, trainee.getPassword())) {
            log.warn("Failed password change attempt for Trainee username={}", username);
            throw new RuntimeException("Old password is incorrect");
        }
        UserCredentialGenerator.checkNewPassword(newPassword);
        trainee.setPassword(passwordEncoder.encode(newPassword));
        traineeRepository.save(trainee);
        log.info("Password updated for Trainee username={}", username);
    }

    @Override
    @Transactional
    public void toggleActive(String username) {
        Trainee trainee = traineeRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
        Boolean current = trainee.getIsActive();
        trainee.setIsActive(!current);
        traineeRepository.save(trainee);
        log.info("Trainee username={} toggled active from {} to {}", username, current, !current);
    }

    @Override
    @Transactional
    public void deleteTraineeByUsername(String username, String password) {
        Trainee trainee = traineeRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
        if (!passwordEncoder.matches(password, trainee.getPassword())) {
            log.warn("Failed delete attempt for Trainee username={}", username);
            throw new RuntimeException("Invalid credentials");
        }
        traineeRepository.delete(trainee);
        log.warn("Deleted Trainee with username={}", username);
    }

    @Override
    public TraineeNotAssignedTrainersDto getTrainersNotAssigned(String username) {
        traineeRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
        log.info("Fetched not-assigned trainers for Trainee username={}", username);
        return new TraineeNotAssignedTrainersDto(
                trainerRepository.findAllTrainersNotAssigned(username)
                        .stream()
                        .map(trainerMapper::toShortProfileDto)
                        .collect(Collectors.toList())
        );
    }

    @Override
    @Transactional
    public List<TrainerShortProfileDto> updateTrainersList(String username, List<String> trainerUsernames) {
        Trainee trainee = traineeRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
        List<Trainer> trainersList = trainerRepository.findAllByUsername(trainerUsernames);
        trainee.setTrainers(new HashSet<>(trainersList));
        traineeRepository.save(trainee);
        log.info("Updated trainers for Trainee username={}", username);
        return trainersList.stream().map(trainerMapper::toShortProfileDto).collect(Collectors.toList());
    }
}
