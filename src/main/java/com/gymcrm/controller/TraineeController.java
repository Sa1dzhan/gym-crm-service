package com.gymcrm.controller;

import com.gymcrm.dto.AuthenticatedRequestDto;
import com.gymcrm.dto.trainee.TraineeCreateRequestDto;
import com.gymcrm.dto.trainee.TraineeNotAssignedTrainersDto;
import com.gymcrm.dto.trainee.TraineeProfileResponseDto;
import com.gymcrm.dto.trainee.TraineeUpdateRequestDto;
import com.gymcrm.dto.trainer.TrainerShortProfileDto;
import com.gymcrm.service.TraineeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Api(tags = "Trainee")
@RestController
@RequestMapping("/api/trainee")
@Slf4j
@RequiredArgsConstructor
public class TraineeController {

    private final TraineeService traineeService;

    @ApiOperation("Trainee Registration")
    @PostMapping("/register")
    public ResponseEntity<?> createTrainee(@RequestBody TraineeCreateRequestDto dto) {
        String transactionId = UUID.randomUUID().toString();
        log.info("[txId={}] POST /api/trainee/register - firstName={}, lastName={}",
                transactionId, dto.getFirstName(), dto.getLastName());

        AuthenticatedRequestDto response = traineeService.createTrainee(dto);
        log.info("Transaction {} - POST /api/trainee/register completed successfully", transactionId);
        return ResponseEntity.ok(response);
    }

    @ApiOperation(value = "Login a trainee")
    @GetMapping("/{username}/login")
    public ResponseEntity<Void> login(@PathVariable("username") String username, @RequestParam("password") String password) {
        String transactionId = UUID.randomUUID().toString();
        log.info("[txId={}] GET /api/trainee/login - username={}", transactionId, username);

        traineeService.login(username, password);
        log.info("Transaction {} - GET /api/trainee/login completed successfully", transactionId);
        return ResponseEntity.ok().build();
    }

    @ApiOperation(value = "Change password")
    @PutMapping("/{username}/update/password")
    public ResponseEntity<Void> changePassword(
            @PathVariable("username") String username,
            @RequestParam("oldPassword") String oldPassword,
            @RequestParam("newPassword") String newPassword
    ) {
        String transactionId = UUID.randomUUID().toString();
        log.info("[txId={}] PUT /api/trainee/update/password - username = {}", transactionId, username);

        traineeService.changePassword(username, oldPassword, newPassword);
        log.info("Transaction {} - PUT /api/trainee/update/password completed successfully", transactionId);
        return ResponseEntity.ok().build();
    }

    @ApiOperation("Get Trainee Profile")
    @GetMapping("/{username}")
    public ResponseEntity<TraineeProfileResponseDto> getTraineeProfile(@PathVariable("username") String username, @RequestParam("password") String password) {
        String transactionId = UUID.randomUUID().toString();
        log.info("[txId={}] GET /api/trainee/{}", transactionId, username);

        TraineeProfileResponseDto response = traineeService.getByUsername(username, password);
        log.info("Transaction {} - GET /api/trainee/{} completed successfully", transactionId, username);
        return ResponseEntity.ok(response);
    }

    @ApiOperation("Update Trainee Profile")
    @PutMapping("{username}/update/profile")
    public ResponseEntity<TraineeProfileResponseDto> updateTrainee(
            @PathVariable("username") String username,
            @RequestBody TraineeUpdateRequestDto dto
    ) {
        String transactionId = UUID.randomUUID().toString();
        log.info("[txId={}] PUT /api/trainee/update/profile - username = {}", transactionId, username);

        dto.setUsername(username);

        TraineeProfileResponseDto response = traineeService.updateTrainee(dto);
        log.info("Transaction {} - PUT /api/trainee/{}/update/profile completed successfully", transactionId, response.getUsername());
        return ResponseEntity.ok().build();
    }

    @ApiOperation("Delete Trainee Profile")
    @DeleteMapping("/{username}")
    public ResponseEntity<Void> deleteTrainee(
            @PathVariable("username") String username,
            @RequestParam("password") String password
    ) {
        String transactionId = UUID.randomUUID().toString();
        log.warn("[txId={}] DELETE /api/trainee/{}", transactionId, username);

        traineeService.deleteTraineeByUsername(username, password);
        log.warn("Transaction {} - DELETE /api/trainee/{} completed successfully", transactionId, username);
        return ResponseEntity.ok().build();
    }

    @ApiOperation("Get Not Assigned (active) Trainers for a Trainee")
    @GetMapping("/{username}/not-assigned-trainers")
    public ResponseEntity<TraineeNotAssignedTrainersDto> getNotAssignedTrainers(
            @PathVariable("username") String username,
            @RequestParam("password") String password) {
        String transactionId = UUID.randomUUID().toString();
        log.info("[txId={}] GET /api/trainee/{}/not-assigned-trainers", transactionId, username);

        TraineeNotAssignedTrainersDto response = traineeService.getTrainersNotAssigned(username, password);
        log.info("Transaction {} - GET /api/trainee/{}/not-assigned-trainers completed successfully.", transactionId, username);
        return ResponseEntity.ok(response);
    }

    @ApiOperation("Update Trainee's Trainers List")
    @PutMapping("/{username}/trainers")
    public ResponseEntity<List<TrainerShortProfileDto>> updateTrainersList(
            @PathVariable("username") String username,
            @RequestParam("password") String password,
            @RequestBody List<String> trainerUsernames) {
        String transactionId = UUID.randomUUID().toString();
        log.info("[txId={}] PUT /api/trainee/{}/trainers - trainersCount={}", transactionId, username, trainerUsernames.size());

        List<TrainerShortProfileDto> response = traineeService.updateTrainersList(username, password, trainerUsernames);
        log.info("Transaction {} - PUT /api/trainee/{}/trainers completed successfully.", transactionId, username);
        return ResponseEntity.ok(response);
    }

    @ApiOperation("Activate/De-Activate Trainee")
    @PatchMapping("/{username}/activate")
    public ResponseEntity<Void> toggleTraineeActive(
            @PathVariable("username") String username,
            @RequestParam("password") String password
    ) {
        String transactionId = UUID.randomUUID().toString();
        log.info("[txId={}] PATCH /api/trainee/{}/activate", transactionId, username);

        traineeService.toggleActive(username, password);
        log.info("Transaction {} - PATCH /api/trainee/{}/activate completed successfully", transactionId, username);
        return ResponseEntity.ok().build();
    }
}
