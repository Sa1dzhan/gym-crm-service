package com.gymcrm.controller;

import com.gymcrm.dto.trainee.AddTrainingRequestDto;
import com.gymcrm.dto.training.TraineeTrainingsListRequestDto;
import com.gymcrm.dto.training.TraineeTrainingsListResponseDto;
import com.gymcrm.dto.training.TrainerTrainingsListRequestDto;
import com.gymcrm.dto.training.TrainerTrainingsListResponseDto;
import com.gymcrm.service.TrainingService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/training")
@Slf4j
@RequiredArgsConstructor
public class TrainingController {

    private final TrainingService trainingService;

    @ApiOperation("Get Trainee Trainings List")
    @GetMapping("/{username}/trainee/trainings")
    public ResponseEntity<List<TraineeTrainingsListResponseDto>> getTraineeTrainings(
            @RequestBody TraineeTrainingsListRequestDto request
    ) {
        log.info("GET /api/trainees/{}/trainings", request.getUsername());

        List<TraineeTrainingsListResponseDto> trainings = trainingService.getTraineeTrainings(request);
        log.info("GET /api/trainees/{}/trainings completed successfully.", request.getUsername());
        return ResponseEntity.ok(trainings);
    }

    @ApiOperation("Get Trainer Trainings List")
    @GetMapping("/{username}/trainer/trainings")
    public ResponseEntity<List<TrainerTrainingsListResponseDto>> getTrainerTrainings(
            @RequestBody TrainerTrainingsListRequestDto request
    ) {
        log.info("GET /api/trainees/{}/trainings", request.getUsername());

        List<TrainerTrainingsListResponseDto> trainings = trainingService.getTrainerTrainings(request);
        log.info("GET /api/trainees/{}/trainings completed successfully.", request.getUsername());
        return ResponseEntity.ok(trainings);
    }

    @ApiOperation("Add Training")
    @PostMapping("/add/trainings")
    public ResponseEntity<Void> addTrainings(
            @RequestBody AddTrainingRequestDto request
    ) {
        log.info("GET /api/trainees/{}/trainings", request.getUsername());

        trainingService.addTraining(request);
        log.info("GET /api/trainees/{}/trainings completed successfully.", request.getUsername());
        return ResponseEntity.ok().build();
    }
}
