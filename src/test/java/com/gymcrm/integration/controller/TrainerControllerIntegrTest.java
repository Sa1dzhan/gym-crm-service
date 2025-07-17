package com.gymcrm.integration.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gymcrm.config.JwtUtil;
import com.gymcrm.config.LoginAttemptService;
import com.gymcrm.config.TestConfig;
import com.gymcrm.controller.TrainerController;
import com.gymcrm.dto.ChangePasswordRequestDto;
import com.gymcrm.dto.UserCreatedResponseDto;
import com.gymcrm.dto.trainer.TrainerCreateRequestDto;
import com.gymcrm.dto.trainer.TrainerProfileResponseDto;
import com.gymcrm.service.TrainerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = TrainerController.class)
@Import(TestConfig.class)
public class TrainerControllerIntegrTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TrainerService trainerService;
    @MockBean
    private JwtUtil jwtUtil;
    @MockBean
    private LoginAttemptService loginAttemptService;

    @Test
    void createTrainer() throws Exception {
        TrainerCreateRequestDto createDto = new TrainerCreateRequestDto();
        createDto.setFirstName("Test");
        createDto.setLastName("Trainer");
        createDto.setSpecializationId(1L);

        when(trainerService.createTrainer(any())).thenReturn(new UserCreatedResponseDto("Test.Trainer", "password"));
        when(jwtUtil.generateToken(any())).thenReturn("fake-token");

        mockMvc.perform(post("/api/trainer/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("Test.Trainer"));
    }

    @Test
    @WithMockUser(username = "test.trainer")
    void getTrainerProfile() throws Exception {
        TrainerProfileResponseDto profileDto = new TrainerProfileResponseDto();
        profileDto.setUsername("test.trainer");

        when(trainerService.getByUsername("test.trainer")).thenReturn(profileDto);

        mockMvc.perform(get("/api/trainer/profile"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("test.trainer"));
    }

    @Test
    @WithMockUser(username = "test.trainer")
    void changePassword() throws Exception {
        ChangePasswordRequestDto passwordDto = new ChangePasswordRequestDto();
        passwordDto.setOldPassword("oldPass");
        passwordDto.setNewPassword("newPass");

        doNothing().when(trainerService).changePassword(eq("test.trainer"), eq("oldPass"), eq("newPass"));

        mockMvc.perform(put("/api/trainer/update/password")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passwordDto)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "test.trainer")
    void toggleTrainerActive() throws Exception {
        doNothing().when(trainerService).toggleActive("test.trainer");

        mockMvc.perform(patch("/api/trainer/activate").with(csrf()))
                .andExpect(status().isOk());
    }
}
