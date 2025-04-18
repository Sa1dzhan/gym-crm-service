package com.gymcrm.service.impl;

import com.gymcrm.converter.TrainingTypeMapper;
import com.gymcrm.dao.TrainingTypeRepository;
import com.gymcrm.dto.training_type.TrainingTypeDto;
import com.gymcrm.service.TrainingTypesService;
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

    @Override
    public List<TrainingTypeDto> getTrainingTypesList(String username) {
        return repository.findAll().stream().map(trainingTypeMapper::toResponseDTO).collect(Collectors.toList());
    }
}
