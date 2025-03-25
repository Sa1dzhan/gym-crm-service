package com.gymcrm.controller;

import com.gymcrm.dto.AuthenticatedRequestDto;
import com.gymcrm.dto.trainer.TrainerCreateRequestDto;
import com.gymcrm.dto.trainer.TrainerProfileResponseDto;
import com.gymcrm.dto.trainer.TrainerUpdateRequestDto;
import com.gymcrm.service.TrainerService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Api(tags = "Trainer")
@RestController
@RequestMapping("api/trainer")
@Slf4j
@RequiredArgsConstructor
public class TrainerController {

    private final TrainerService trainerService;

    @ApiOperation("Trainer Registration")
    @PostMapping("/register")
    public ResponseEntity<?> createTrainer(@RequestBody TrainerCreateRequestDto dto) {
        String transactionId = UUID.randomUUID().toString();
        log.info("[txId={}] POST /api/trainer/register - firstName={}, lastName={}",
                transactionId, dto.getFirstName(), dto.getLastName());

        AuthenticatedRequestDto response = trainerService.createTrainer(dto);
        log.info("Transaction {} - POST /api/trainer/register completed successfully", transactionId);
        return ResponseEntity.ok(response);
    }

    @ApiOperation(value = "Login a trainer")
    @GetMapping("/{username}/login")
    public ResponseEntity<Void> login(@PathVariable("username") String username, @RequestParam("password") String password) {
        String transactionId = UUID.randomUUID().toString();
        log.info("[txId={}] GET /api/trainer/login - username={}", transactionId, username);

        trainerService.login(username, password);
        log.info("Transaction {} - GET /api/trainer/login completed successfully", transactionId);
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
        log.info("[txId={}] PUT /api/trainer/update/password - username = {}", transactionId, username);

        trainerService.changePassword(username, oldPassword, newPassword);
        log.info("Transaction {} - PUT /api/trainer/update/password completed successfully", transactionId);
        return ResponseEntity.ok().build();
    }

    @ApiOperation("Get Trainer Profile")
    @GetMapping("/{username}")
    public ResponseEntity<TrainerProfileResponseDto> getTrainerProfile(@PathVariable("username") String username, @RequestParam("password") String password) {
        String transactionId = UUID.randomUUID().toString();
        log.info("[txId={}] GET /api/trainer/{}", transactionId, username);

        TrainerProfileResponseDto response = trainerService.getByUsername(username, password);
        log.info("Transaction {} - GET /api/trainer/{} completed successfully", transactionId, username);
        return ResponseEntity.ok(response);
    }

    @ApiOperation("Update Trainer Profile")
    @PutMapping("/{username}/update/profile")
    public ResponseEntity<TrainerProfileResponseDto> updateTrainer(
            @PathVariable("username") String username,
            @RequestBody TrainerUpdateRequestDto dto
    ) {
        String transactionId = UUID.randomUUID().toString();
        log.info("[txId={}] PUT /api/trainer/update/profile - username = {}", transactionId, username);

        dto.setUsername(username);

        TrainerProfileResponseDto response = trainerService.updateTrainer(dto);
        log.info("Transaction {} - PUT /api/trainer/{}/update/profile completed successfully", transactionId, response.getUsername());
        return ResponseEntity.ok().build();
    }

    @ApiOperation("Activate/De-Activate Trainer")
    @PatchMapping("/{username}/activate")
    public ResponseEntity<Void> toggleTrainerActive(
            @PathVariable("username") String username,
            @RequestParam("password") String password
    ) {
        String transactionId = UUID.randomUUID().toString();
        log.info("[txId={}] PATCH /api/trainer/{}/activate", transactionId, username);

        trainerService.toggleActive(username, password);
        log.info("Transaction {} - PATCH /api/trainer/{}/activate completed successfully", transactionId, username);
        return ResponseEntity.ok().build();
    }

}
