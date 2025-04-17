package com.gymcrm.controller;

import com.gymcrm.config.JwtUtil;
import com.gymcrm.config.LoginAttemptService;
import com.gymcrm.dto.ChangePasswordRequestDto;
import com.gymcrm.dto.LoginRequestDto;
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
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "Trainer", description = "Trainer management API")
@RestController
@RequestMapping("api/trainer")
@Slf4j
@RequiredArgsConstructor
public class TrainerController {

    private final TrainerService trainerService;
    private final JwtUtil jwtUtil;
    private final LoginAttemptService loginAttemptService;

    @Operation(summary = "Trainer Registration")
    @PostMapping("/register")
    public ResponseEntity<?> createTrainer(@RequestBody TrainerCreateRequestDto dto) {
        log.info("POST /api/trainer/register - firstName={}, lastName={}", dto.getFirstName(), dto.getLastName());

        UserCreatedResponseDto response = trainerService.createTrainer(dto);
        log.info("POST /api/trainer/register completed successfully");
        String token = jwtUtil.generateToken(response.getUsername());
        return ResponseEntity.ok(Map.of("username", response.getUsername(), "password", response.getPassword(), "token", token));
    }

    @Operation(summary = "Login a trainer")
    @PostMapping("/login")
    public ResponseEntity<?> loginTrainer(@RequestBody LoginRequestDto dto) {
        String username = dto.getUsername();
        String password = dto.getPassword();
        if (loginAttemptService.isBlocked(username)) {
            return ResponseEntity.status(429).body("User is blocked for 5 minutes due to failed login attempts");
        }
        log.info("GET /api/trainer/login - username={}", username);
        try {
            trainerService.login(username, password);
            loginAttemptService.loginSucceeded(username);
            String token = jwtUtil.generateToken(username);
            log.info("GET /api/trainer/login completed successfully");
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
        log.info("PUT /api/trainer/update/password - username = {}", username);
        trainerService.changePassword(username, dto.getOldPassword(), dto.getNewPassword());
        log.info("PUT /api/trainer/update/password completed successfully");
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Get Trainer Profile")
    @GetMapping("/profile")
    public ResponseEntity<TrainerProfileResponseDto> getTrainerProfile(Authentication authentication) {
        String username = authentication.getName();
        log.info("GET /api/trainer/{}", username);
        TrainerProfileResponseDto response = trainerService.getByUsername(username);
        log.info("GET /api/trainer/{} completed successfully", username);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Update Trainer Profile")
    @PutMapping("/update/profile")
    public ResponseEntity<TrainerProfileResponseDto> updateTrainer(Authentication authentication, @RequestBody TrainerUpdateRequestDto dto) {
        String username = authentication.getName();
        log.info("PUT /api/trainer/update/profile - username = {}", username);
        dto.setUsername(username);
        TrainerProfileResponseDto response = trainerService.updateTrainer(dto);
        log.info("PUT /api/trainer/{}/update/profile completed successfully", response.getUsername());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Activate/De-Activate Trainer")
    @PatchMapping("/activate")
    public ResponseEntity<Void> toggleTrainerActive(Authentication authentication) {
        String username = authentication.getName();
        log.info("PATCH /api/trainer/{}/activate", username);
        trainerService.toggleActive(username);
        log.info("PATCH /api/trainer/{}/activate completed successfully", username);
        return ResponseEntity.ok().build();
    }
}
