package com.gymcrm.controller;

import com.gymcrm.dto.UserCreatedResponseDto;
import com.gymcrm.dto.trainer.TrainerCreateRequestDto;
import com.gymcrm.dto.trainer.TrainerProfileResponseDto;
import com.gymcrm.dto.trainer.TrainerUpdateRequestDto;
import com.gymcrm.service.TrainerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Trainer", description = "Trainer management API")
@RestController
@RequestMapping("api/trainer")
@Slf4j
@RequiredArgsConstructor
public class TrainerController {

    private final TrainerService trainerService;

    @Operation(summary = "Trainer Registration")
    @PostMapping("/register")
    public ResponseEntity<?> createTrainer(@RequestBody TrainerCreateRequestDto dto) {
        log.info("POST /api/trainer/register - firstName={}, lastName={}", dto.getFirstName(), dto.getLastName());

        UserCreatedResponseDto response = trainerService.createTrainer(dto);
        log.info("POST /api/trainer/register completed successfully");
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Login a trainer")
    @GetMapping("/{username}/login")
    public ResponseEntity<Void> login(@PathVariable("username") String username, @RequestParam("password") String password) {
        log.info("GET /api/trainer/login - username={}", username);

        trainerService.login(username, password);
        log.info("GET /api/trainer/login completed successfully");
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Change password")
    @PutMapping("/{username}/update/password")
    public ResponseEntity<Void> changePassword(
            @PathVariable("username") String username,
            @RequestParam("oldPassword") String oldPassword,
            @RequestParam("newPassword") String newPassword
    ) {
        log.info("PUT /api/trainer/update/password - username = {}", username);

        trainerService.changePassword(username, oldPassword, newPassword);
        log.info("PUT /api/trainer/update/password completed successfully");
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Get Trainer Profile")
    @GetMapping("/{username}")
    public ResponseEntity<TrainerProfileResponseDto> getTrainerProfile(@PathVariable("username") String username, @RequestParam("password") String password) {
        log.info("GET /api/trainer/{}", username);

        TrainerProfileResponseDto response = trainerService.getByUsername(username, password);
        log.info("GET /api/trainer/{} completed successfully", username);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Update Trainer Profile")
    @PutMapping("/{username}/update/profile")
    public ResponseEntity<TrainerProfileResponseDto> updateTrainer(
            @PathVariable("username") String username,
            @RequestBody TrainerUpdateRequestDto dto
    ) {
        log.info("PUT /api/trainer/update/profile - username = {}", username);

        dto.setUsername(username);

        TrainerProfileResponseDto response = trainerService.updateTrainer(dto);
        log.info("PUT /api/trainer/{}/update/profile completed successfully", response.getUsername());
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Activate/De-Activate Trainer")
    @PatchMapping("/{username}/activate")
    public ResponseEntity<Void> toggleTrainerActive(
            @PathVariable("username") String username,
            @RequestParam("password") String password
    ) {
        log.info("PATCH /api/trainer/{}/activate", username);

        trainerService.toggleActive(username, password);
        log.info("PATCH /api/trainer/{}/activate completed successfully", username);
        return ResponseEntity.ok().build();
    }
}
