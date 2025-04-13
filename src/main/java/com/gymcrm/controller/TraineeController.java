package com.gymcrm.controller;

import com.gymcrm.dto.UserCreatedResponseDto;
import com.gymcrm.dto.trainee.TraineeCreateRequestDto;
import com.gymcrm.dto.trainee.TraineeNotAssignedTrainersDto;
import com.gymcrm.dto.trainee.TraineeProfileResponseDto;
import com.gymcrm.dto.trainee.TraineeUpdateRequestDto;
import com.gymcrm.dto.trainer.TrainerShortProfileDto;
import com.gymcrm.service.TraineeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Trainee", description = "Trainee management API")
@RestController
@RequestMapping("/api/trainee")
@Slf4j
@RequiredArgsConstructor
public class TraineeController {

    private final TraineeService traineeService;

    @Operation(summary = "Trainee Registration")
    @PostMapping("/register")
    public ResponseEntity<?> createTrainee(@RequestBody TraineeCreateRequestDto dto) {
        log.info("POST /api/trainee/register - firstName={}, lastName={}", dto.getFirstName(), dto.getLastName());

        UserCreatedResponseDto response = traineeService.createTrainee(dto);
        log.info("POST /api/trainee/register completed successfully");
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Login a trainee")
    @GetMapping("/{username}/login")
    public ResponseEntity<Void> login(@PathVariable("username") String username, @RequestParam("password") String password) {
        log.info("GET /api/trainee/login - username={}", username);

        traineeService.login(username, password);
        log.info("GET /api/trainee/login completed successfully");
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Change password")
    @PutMapping("/{username}/update/password")
    public ResponseEntity<Void> changePassword(
            @PathVariable("username") String username,
            @RequestParam("oldPassword") String oldPassword,
            @RequestParam("newPassword") String newPassword
    ) {
        log.info("PUT /api/trainee/update/password - username = {}", username);

        traineeService.changePassword(username, oldPassword, newPassword);
        log.info("PUT /api/trainee/update/password completed successfully");
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Get Trainee Profile")
    @GetMapping("/{username}")
    public ResponseEntity<TraineeProfileResponseDto> getTraineeProfile(@PathVariable("username") String username, @RequestParam("password") String password) {
        log.info("GET /api/trainee/{}", username);

        TraineeProfileResponseDto response = traineeService.getByUsername(username, password);
        log.info("GET /api/trainee/{} completed successfully", username);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Update Trainee Profile")
    @PutMapping("{username}/update/profile")
    public ResponseEntity<TraineeProfileResponseDto> updateTrainee(
            @PathVariable("username") String username,
            @RequestBody TraineeUpdateRequestDto dto
    ) {
        log.info("PUT /api/trainee/update/profile - username = {}", username);

        dto.setUsername(username);

        TraineeProfileResponseDto response = traineeService.updateTrainee(dto);
        log.info("PUT /api/trainee/{}/update/profile completed successfully", response.getUsername());
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Delete Trainee Profile")
    @DeleteMapping("/{username}")
    public ResponseEntity<Void> deleteTrainee(
            @PathVariable("username") String username,
            @RequestParam("password") String password
    ) {
        log.warn("DELETE /api/trainee/{}", username);

        traineeService.deleteTraineeByUsername(username, password);
        log.warn("DELETE /api/trainee/{} completed successfully", username);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Get Not Assigned (active) Trainers for a Trainee")
    @GetMapping("/{username}/not-assigned-trainers")
    public ResponseEntity<TraineeNotAssignedTrainersDto> getNotAssignedTrainers(
            @PathVariable("username") String username,
            @RequestParam("password") String password) {
        log.info("GET /api/trainee/{}/not-assigned-trainers", username);

        TraineeNotAssignedTrainersDto response = traineeService.getTrainersNotAssigned(username, password);
        log.info("GET /api/trainee/{}/not-assigned-trainers completed successfully.", username);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Update Trainee's Trainers List")
    @PutMapping("/{username}/trainers")
    public ResponseEntity<List<TrainerShortProfileDto>> updateTrainersList(
            @PathVariable("username") String username,
            @RequestParam("password") String password,
            @RequestBody List<String> trainerUsernames) {
        log.info("PUT /api/trainee/{}/trainers - trainersCount={}", username, trainerUsernames.size());

        List<TrainerShortProfileDto> response = traineeService.updateTrainersList(username, password, trainerUsernames);
        log.info("PUT /api/trainee/{}/trainers completed successfully.", username);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Activate/De-Activate Trainee")
    @PatchMapping("/{username}/activate")
    public ResponseEntity<Void> toggleTraineeActive(
            @PathVariable("username") String username,
            @RequestParam("password") String password
    ) {
        log.info("PATCH /api/trainee/{}/activate", username);

        traineeService.toggleActive(username, password);
        log.info("PATCH /api/trainee/{}/activate completed successfully", username);
        return ResponseEntity.ok().build();
    }
}
