package com.gymcrm.service.impl;

import com.gymcrm.converter.TrainingTypeMapper;
import com.gymcrm.dao.GeneralUserRepository;
import com.gymcrm.dao.TrainingTypeRepository;
import com.gymcrm.dto.training_type.TrainingTypeDto;
import com.gymcrm.service.TrainingTypesService;
import com.gymcrm.util.Authentication;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class TrainingTypesServiceImpl implements TrainingTypesService {

    private final TrainingTypeRepository repository;
    private final TrainingTypeMapper trainingTypeMapper;
    private final GeneralUserRepository userRepository;

    @Override
    public List<TrainingTypeDto> getTrainingTypesList(String username, String password) {
        Authentication.authenticateUser(username, password, userRepository::findByUsername);

        return repository.findAll().stream().map(trainingTypeMapper::toResponseDTO).collect(Collectors.toList());
    }
}
