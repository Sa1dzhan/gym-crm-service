package com.gymcrm.controller;

import com.gymcrm.dto.training_type.TrainingTypeDto;
import com.gymcrm.service.TrainingTypesService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@Api(tags = "Training Types")
@RestController
@RequestMapping("/api/training-types")
@Slf4j
@RequiredArgsConstructor
public class TrainingTypesController {

    private final TrainingTypesService trainingTypesService;

    @ApiOperation("Get all Training Types")
    @GetMapping
    public ResponseEntity<List<TrainingTypeDto>> getTrainingTypes(
            @RequestParam("username") String username,
            @RequestParam("password") String password
    ) {
        String transactionId = UUID.randomUUID().toString();
        log.info("[txId={}] GET /api/training-types - username{}", transactionId, username);

        List<TrainingTypeDto> response = trainingTypesService.getTrainingTypesList(username, password);
        log.info("Transaction {} - GET /api/training-types completed successfully.", transactionId);
        return ResponseEntity.ok(response);
    }
}
