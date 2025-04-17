package com.gymcrm.gymservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gymcrm.config.TestConfig;
import com.gymcrm.controller.GlobalExceptionHandler;
import com.gymcrm.controller.TrainerController;
import com.gymcrm.dto.UserCreatedResponseDto;
import com.gymcrm.dto.trainer.TrainerCreateRequestDto;
import com.gymcrm.dto.trainer.TrainerProfileResponseDto;
import com.gymcrm.dto.trainer.TrainerUpdateRequestDto;
import com.gymcrm.dto.training_type.TrainingTypeDto;
import com.gymcrm.service.TrainerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = TrainerController.class)
@Import({GlobalExceptionHandler.class, TestConfig.class, TrainerControllerTest.TestConfig.class})
public class TrainerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TrainerService trainerService;

    @Autowired
    private ObjectMapper objectMapper;

    @TestConfiguration
    static class TestConfig {
        @Primary
        @Bean
        public TrainerService trainerService() {
            return org.mockito.Mockito.mock(TrainerService.class);
        }
    }

    @Test
    void testCreateTrainer_Success() throws Exception {
        TrainerCreateRequestDto createDto = new TrainerCreateRequestDto();
        createDto.setFirstName("John");
        createDto.setLastName("Doe");
        createDto.setSpecializationId(1L);

        UserCreatedResponseDto authResponse = new UserCreatedResponseDto();
        authResponse.setUsername("john.doe");
        when(trainerService.createTrainer(any(TrainerCreateRequestDto.class)))
                .thenReturn(authResponse);

        String url = "/api/trainer/register";

        mockMvc.perform(post(url)
                        .content(objectMapper.writeValueAsString(createDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("john.doe"));
    }

    @Test
    void testLogin_Success() throws Exception {
        doNothing().when(trainerService).login(eq("john.doe"), eq("secret"));

        String url = "/api/trainer/john.doe/login?password=secret";

        mockMvc.perform(get(url)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void testChangePassword_Success() throws Exception {
        doNothing().when(trainerService).changePassword(eq("john.doe"), eq("oldPassword"), eq("newPassword"));

        String url = "/api/trainer/john.doe/update/password?oldPassword=oldPassword&newPassword=newPassword";

        mockMvc.perform(put(url)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void testGetTrainerProfile_Success() throws Exception {
        TrainerProfileResponseDto profile = new TrainerProfileResponseDto();
        profile.setId(1L);
        profile.setUsername("john.doe");
        profile.setFirstName("John");
        profile.setLastName("Doe");
        TrainingTypeDto trainingTypeDto = new TrainingTypeDto();
        trainingTypeDto.setId(1L);
        trainingTypeDto.setTrainingTypeName("Strength training");
        profile.setSpecialization(trainingTypeDto);
        profile.setIsActive(true);
        profile.setTrainees(null);

        when(trainerService.getByUsername(eq("john.doe"), eq("secret")))
                .thenReturn(profile);

        String url = "/api/trainer/john.doe?password=secret";

        mockMvc.perform(get(url)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("john.doe"))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.specialization.trainingTypeName").value("Strength training"));
    }

    @Test
    void testUpdateTrainerProfile_Success() throws Exception {
        TrainerUpdateRequestDto updateDto = new TrainerUpdateRequestDto();
        updateDto.setFirstName("Jane");
        updateDto.setLastName("Doe");
        updateDto.setSpecializationId(1L);
        updateDto.setIsActive(true);

        TrainerProfileResponseDto profile = new TrainerProfileResponseDto();
        profile.setId(2L);
        profile.setUsername("jane.doe");
        profile.setFirstName("Jane");
        profile.setLastName("Doe");
        TrainingTypeDto cardioDto = new TrainingTypeDto();
        cardioDto.setId(1L);
        cardioDto.setTrainingTypeName("Cardio");
        profile.setSpecialization(cardioDto);
        profile.setIsActive(true);
        profile.setTrainees(null);

        when(trainerService.updateTrainer(any(TrainerUpdateRequestDto.class))).thenReturn(profile);

        String url = "/api/trainer/jane.doe/update/profile";

        mockMvc.perform(put(url)
                        .content(objectMapper.writeValueAsString(updateDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void testUpdateTrainer_Success() throws Exception {
        TrainerUpdateRequestDto updateDto = new TrainerUpdateRequestDto();
        updateDto.setUsername("john.doe");
        updateDto.setPassword("secret");
        updateDto.setFirstName("John");
        updateDto.setLastName("Doe");
        updateDto.setSpecializationId(1L);
        updateDto.setIsActive(true);

        TrainerProfileResponseDto profile = new TrainerProfileResponseDto();
        profile.setUsername("john.doe");
        profile.setFirstName("John");
        profile.setLastName("Doe");
        when(trainerService.updateTrainer(any(TrainerUpdateRequestDto.class))).thenReturn(profile);

        String url = "/api/trainer/john.doe/update";

        mockMvc.perform(put(url)
                        .content(objectMapper.writeValueAsString(updateDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("john.doe"));
    }

    @Test
    void testToggleTrainerActive_Success() throws Exception {
        doNothing().when(trainerService).toggleActive(eq("john.doe"), eq("secret"));

        String url = "/api/trainer/john.doe/activate?password=secret";

        mockMvc.perform(patch(url)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
