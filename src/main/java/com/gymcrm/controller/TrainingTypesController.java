package com.gymcrm.controller;

import com.gymcrm.dto.training_type.TrainingTypeDto;
import com.gymcrm.service.TrainingTypesService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Training Types", description = "Training Types management API")
@RestController
@RequestMapping("/api/training-types")
@Slf4j
@RequiredArgsConstructor
public class TrainingTypesController {

    private final TrainingTypesService trainingTypesService;

    @Operation(summary = "Get all Training Types")
    @GetMapping
    public ResponseEntity<List<TrainingTypeDto>> getTrainingTypes(Authentication authentication) {
        String username = authentication.getName();
        log.info("GET /api/training-types - username {}", username);

        List<TrainingTypeDto> response = trainingTypesService.getTrainingTypesList(username);
        log.info("GET /api/training-types completed successfully.");
        return ResponseEntity.ok(response);
    }
}
