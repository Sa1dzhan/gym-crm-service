package com.gymcrm.service.impl;

import com.gymcrm.converter.Converter;
import com.gymcrm.dao.TraineeRepository;
import com.gymcrm.dao.TrainerRepository;
import com.gymcrm.dto.AuthenticatedRequestDto;
import com.gymcrm.dto.trainee.TraineeCreateRequestDto;
import com.gymcrm.dto.trainee.TraineeNotAssignedTrainersDto;
import com.gymcrm.dto.trainee.TraineeProfileResponseDto;
import com.gymcrm.dto.trainee.TraineeUpdateRequestDto;
import com.gymcrm.dto.trainer.TrainerShortProfileDto;
import com.gymcrm.model.Trainee;
import com.gymcrm.model.Trainer;
import com.gymcrm.service.TraineeService;
import com.gymcrm.util.Authentication;
import com.gymcrm.util.UserCredentialGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class TraineeServiceImpl implements TraineeService {
    private final TraineeRepository traineeRepository;
    private final TrainerRepository trainerRepository;
    private final Converter converter;

    @Override
    @Transactional
    public AuthenticatedRequestDto createTrainee(TraineeCreateRequestDto dto) {
        Trainee trainee = converter.toEntity(dto);
        UserCredentialGenerator.generateUserCredentials(trainee, traineeRepository::existsByUsername);

        Trainee savedTrainee = traineeRepository.save(trainee);
        log.info("Created Trainee with ID={}, username={}", savedTrainee.getId(), savedTrainee.getUsername());

        return converter.toRegisteredDto(savedTrainee);
    }

    @Override
    public void login(String username, String password) {
        Authentication.authenticateUser(username, password, traineeRepository::findByUsername);
    }

    @Override
    @Transactional
    public TraineeProfileResponseDto updateTrainee(TraineeUpdateRequestDto dto) {
        Authentication.authenticateUser(dto.getUsername(), dto.getPassword(), traineeRepository::findByUsername);
        Trainee updatedTrainee = converter.toEntity(dto);

        Trainee savedTrainee = traineeRepository.save(updatedTrainee);
        log.info("Updated {}", savedTrainee);
        return converter.toProfileDTO(updatedTrainee);
    }

    @Override
    public TraineeProfileResponseDto getTrainee(Long id) {
        return converter.toProfileDTO(
                traineeRepository.findById(id).orElseThrow(() -> new RuntimeException("No such trainee found"))
        );
    }

    @Override
    public TraineeProfileResponseDto getByUsername(String username, String password) {
        Trainee trainee = Authentication.authenticateUser(username, password, traineeRepository::findByUsername);
        return converter.toProfileDTO(trainee);
    }

    @Override
    @Transactional
    public void changePassword(String username, String oldPassword, String newPassword) {
        Trainee trainee = Authentication.authenticateUser(username, oldPassword, traineeRepository::findByUsername);
        UserCredentialGenerator.checkNewPassword(newPassword);

        trainee.setPassword(newPassword);
        traineeRepository.save(trainee);

        log.info("Update password for {}", username);
    }

    @Override
    @Transactional
    public void toggleActive(String username, String password) {
        Trainee trainee = Authentication.authenticateUser(username, password, traineeRepository::findByUsername);

        Boolean current = trainee.getIsActive();
        trainee.setIsActive(!current);
        traineeRepository.save(trainee);

        log.info("Username = {} toggled from {} to {}",
                username, current, !current);
    }

    @Override
    @Transactional
    public void deleteTraineeByUsername(String username, String password) {
        Trainee trainee = Authentication.authenticateUser(username, password, traineeRepository::findByUsername);

        traineeRepository.delete(trainee);
        log.warn("Deleted Trainee with username={}", username);
    }

    @Override
    public TraineeNotAssignedTrainersDto getTrainersNotAssigned(String username, String password) {
        Authentication.authenticateUser(username, password, traineeRepository::findByUsername);

        return new TraineeNotAssignedTrainersDto(
                trainerRepository.findAllTrainersNotAssigned(username)
                        .stream()
                        .map(converter::toShortProfileDto)
                        .collect(Collectors.toList())
        );
    }

    @Override
    @Transactional
    public List<TrainerShortProfileDto> updateTrainersList(String username, String password, List<String> trainerUsernames) {
        Trainee trainee = Authentication.authenticateUser(username, password, traineeRepository::findByUsername);

        List<Trainer> trainersList = trainerRepository.findAllByUsername(trainerUsernames);
        trainee.setTrainers(new HashSet<>(trainersList));
        traineeRepository.save(trainee);
        log.info("Updated trainers for Trainee username={}", username);

        return trainersList.stream().map(converter::toShortProfileDto).collect(Collectors.toList());
    }
}
