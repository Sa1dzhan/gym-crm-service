package com.gymcrm.integration.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gymcrm.config.JwtUtil;
import com.gymcrm.config.LoginAttemptService;
import com.gymcrm.config.TestConfig;
import com.gymcrm.controller.TraineeController;
import com.gymcrm.dto.UserCreatedResponseDto;
import com.gymcrm.dto.trainee.TraineeCreateRequestDto;
import com.gymcrm.dto.trainee.TraineeProfileResponseDto;
import com.gymcrm.dto.trainee.TraineeUpdateRequestDto;
import com.gymcrm.service.TraineeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = TraineeController.class)
@Import(TestConfig.class)
public class TraineeControllerIntegrTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TraineeService traineeService;
    @MockBean
    private JwtUtil jwtUtil;
    @MockBean
    private LoginAttemptService loginAttemptService;

    @Test
    void registerTrainee() throws Exception {
        TraineeCreateRequestDto createDto = new TraineeCreateRequestDto();
        createDto.setFirstName("John");
        createDto.setLastName("Doe");
        createDto.setDateOfBirth(LocalDate.of(1995, 5, 20));

        UserCreatedResponseDto serviceResponse = new UserCreatedResponseDto("John.Doe", "password123");

        when(traineeService.createTrainee(any(TraineeCreateRequestDto.class))).thenReturn(serviceResponse);
        when(jwtUtil.generateToken("John.Doe")).thenReturn("fake-jwt-token");

        mockMvc.perform(post("/api/trainee/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("John.Doe"))
                .andExpect(jsonPath("$.password").value("password123"))
                .andExpect(jsonPath("$.token").value("fake-jwt-token"));
    }

    @Test
    @WithMockUser(username = "test.user")
    void getTraineeProfile() throws Exception {
        TraineeProfileResponseDto profileDto = new TraineeProfileResponseDto();
        profileDto.setUsername("test.user");
        profileDto.setFirstName("Test");

        when(traineeService.getByUsername("test.user")).thenReturn(profileDto);

        mockMvc.perform(get("/api/trainee/profile"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.username").value("test.user"));
    }

    @Test
    @WithMockUser(username = "test.user")
    void updateTrainee() throws Exception {
        TraineeUpdateRequestDto updateDto = new TraineeUpdateRequestDto();
        updateDto.setFirstName("Updated");
        updateDto.setLastName("User");

        TraineeProfileResponseDto profileDto = new TraineeProfileResponseDto();
        profileDto.setUsername("test.user");
        profileDto.setFirstName("Updated");

        when(traineeService.updateTrainee(any(TraineeUpdateRequestDto.class))).thenReturn(profileDto);

        mockMvc.perform(put("/api/trainee/update/profile")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Updated"));
    }

    @Test
    @WithMockUser(username = "test.user")
    void updateTrainersList() throws Exception {
        when(traineeService.updateTrainersList(any(), any())).thenReturn(Collections.emptyList());

        mockMvc.perform(put("/api/trainee/trainers")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Collections.singletonList("trainer.one"))))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "test.user")
    void toggleTraineeActive() throws Exception {
        doNothing().when(traineeService).toggleActive("test.user");

        mockMvc.perform(patch("/api/trainee/activate").with(csrf()))
                .andExpect(status().isOk());
    }
}
