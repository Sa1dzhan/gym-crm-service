package com.gymcrm.controller;

import com.gymcrm.config.JwtUtil;
import com.gymcrm.config.LoginAttemptService;
import com.gymcrm.dto.ChangePasswordRequestDto;
import com.gymcrm.dto.LoginRequestDto;
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
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "Trainee", description = "Trainee management API")
@RestController
@RequestMapping("/api/trainee")
@Slf4j
@RequiredArgsConstructor
public class TraineeController {

    private final TraineeService traineeService;
    private final JwtUtil jwtUtil;
    private final LoginAttemptService loginAttemptService;

    @Operation(summary = "Trainee Registration")
    @PostMapping("/register")
    public ResponseEntity<?> registerTrainee(@RequestBody TraineeCreateRequestDto dto) {
        log.info("POST /api/trainee/register - firstName={}, lastName={}", dto.getFirstName(), dto.getLastName());
        UserCreatedResponseDto response = traineeService.createTrainee(dto);
        String token = jwtUtil.generateToken(response.getUsername());
        log.info("POST /api/trainee/register completed successfully");
        return ResponseEntity.ok(Map.of("username", response.getUsername(), "password", response.getPassword(), "token", token));
    }

    @Operation(summary = "Login a trainee")
    @PostMapping("/login")
    public ResponseEntity<?> loginTrainee(@RequestBody LoginRequestDto dto) {
        String username = dto.getUsername();
        String password = dto.getPassword();
        if (loginAttemptService.isBlocked(username)) {
            return ResponseEntity.status(429).body("User is blocked for 5 minutes due to failed login attempts");
        }
        log.info("GET /api/trainee/login - username={}", username);
        try {
            traineeService.login(username, password);
            loginAttemptService.loginSucceeded(username);
            String token = jwtUtil.generateToken(username);
            log.info("GET /api/trainee/login completed successfully");
            return ResponseEntity.ok(Map.of("token", token));
        } catch (Exception e) {
            loginAttemptService.loginFailed(username);
            return ResponseEntity.status(401).body("Invalid credentials");
        }
    }

    @Operation(summary = "Change password")
    @PutMapping("/update/password")
    public ResponseEntity<Void> changePassword(Authentication authentication, @RequestBody ChangePasswordRequestDto dto) {
        String username = authentication.getName();
        log.info("PUT /api/trainee/update/password - username = {}", username);
        traineeService.changePassword(username, dto.getOldPassword(), dto.getNewPassword());
        log.info("PUT /api/trainee/update/password completed successfully");
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Get Trainee Profile")
    @GetMapping("/profile")
    public ResponseEntity<TraineeProfileResponseDto> getTraineeProfile(Authentication authentication) {
        String username = authentication.getName();
        log.info("GET /api/trainee/{}", username);
        TraineeProfileResponseDto response = traineeService.getByUsername(username);
        log.info("GET /api/trainee/{} completed successfully", username);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Update Trainee Profile")
    @PutMapping("/update/profile")
    public ResponseEntity<TraineeProfileResponseDto> updateTrainee(Authentication authentication, @RequestBody TraineeUpdateRequestDto dto) {
        String username = authentication.getName();
        log.info("PUT /api/trainee/update/profile - username = {}", username);
        dto.setUsername(username);
        TraineeProfileResponseDto response = traineeService.updateTrainee(dto);
        log.info("PUT /api/trainee/{}/update/profile completed successfully", response.getUsername());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Delete Trainee Profile")
    @DeleteMapping("/profile")
    public ResponseEntity<Void> deleteTrainee(Authentication authentication, @RequestBody LoginRequestDto dto) {
        String username = authentication.getName();
        log.warn("DELETE /api/trainee/{}", username);
        traineeService.deleteTraineeByUsername(username, dto.getPassword());
        log.warn("DELETE /api/trainee/{} completed successfully", username);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Get Not Assigned (active) Trainers for a Trainee")
    @GetMapping("/not-assigned-trainers")
    public ResponseEntity<TraineeNotAssignedTrainersDto> getNotAssignedTrainers(Authentication authentication) {
        String username = authentication.getName();
        log.info("GET /api/trainee/{}/not-assigned-trainers", username);
        TraineeNotAssignedTrainersDto response = traineeService.getTrainersNotAssigned(username);
        log.info("GET /api/trainee/{}/not-assigned-trainers completed successfully.", username);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Update Trainee's Trainers List")
    @PutMapping("/trainers")
    public ResponseEntity<List<TrainerShortProfileDto>> updateTrainersList(Authentication authentication, @RequestBody List<String> trainerUsernames) {
        String username = authentication.getName();
        log.info("PUT /api/trainee/{}/trainers - trainersCount={}", username, trainerUsernames.size());
        List<TrainerShortProfileDto> response = traineeService.updateTrainersList(username, trainerUsernames);
        log.info("PUT /api/trainee/{}/trainers completed successfully.", username);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Activate/De-Activate Trainee")
    @PatchMapping("/activate")
    public ResponseEntity<Void> toggleTraineeActive(Authentication authentication) {
        String username = authentication.getName();
        log.info("PATCH /api/trainee/{}/activate", username);
        traineeService.toggleActive(username);
        log.info("PATCH /api/trainee/{}/activate completed successfully", username);
        return ResponseEntity.ok().build();
    }
}
