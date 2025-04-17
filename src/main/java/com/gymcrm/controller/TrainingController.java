package com.gymcrm.controller;

import com.gymcrm.dto.trainee.AddTrainingRequestDto;
import com.gymcrm.dto.training.TraineeTrainingsListRequestDto;
import com.gymcrm.dto.training.TraineeTrainingsListResponseDto;
import com.gymcrm.dto.training.TrainerTrainingsListRequestDto;
import com.gymcrm.dto.training.TrainerTrainingsListResponseDto;
import com.gymcrm.service.TrainingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Training", description = "Training management API")
@RestController
@RequestMapping("/api/training")
@Slf4j
@RequiredArgsConstructor
public class TrainingController {

    private final TrainingService trainingService;

    @Operation(summary = "Get Trainee Trainings List")
    @GetMapping("/trainee/trainings")
    public ResponseEntity<List<TraineeTrainingsListResponseDto>> getTraineeTrainings(
            Authentication authentication,
            @RequestBody TraineeTrainingsListRequestDto request
    ) {
        String username = authentication.getName();
        log.info("GET /api/trainee/trainings for {}", username);

        List<TraineeTrainingsListResponseDto> trainings = trainingService.getTraineeTrainings(username, request);
        log.info("GET /api/trainee/trainings completed successfully for {}.", username);
        return ResponseEntity.ok(trainings);
    }

    @Operation(summary = "Get Trainer Trainings List")
    @GetMapping("/trainer/trainings")
    public ResponseEntity<List<TrainerTrainingsListResponseDto>> getTrainerTrainings(
            Authentication authentication,
            @RequestBody TrainerTrainingsListRequestDto request
    ) {
        String username = authentication.getName();
        log.info("GET /api/trainer/trainings for {}", username);

        List<TrainerTrainingsListResponseDto> trainings = trainingService.getTrainerTrainings(username, request);
        log.info("GET /api/trainer/trainings completed successfully for {}.", username);
        return ResponseEntity.ok(trainings);
    }

    @Operation(summary = "Add Training")
    @PostMapping("/add/trainings")
    public ResponseEntity<Void> addTrainings(
            Authentication authentication,
            @RequestBody AddTrainingRequestDto request
    ) {
        String username = authentication.getName();
        log.info("POST /api/training/add/trainings for {}", username);

        trainingService.addTraining(username, request);
        log.info("POST /api/training/add/trainings completed successfully for {}.", username);
        return ResponseEntity.ok().build();
    }
}
