package com.gymcrm.integration.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gymcrm.config.JwtUtil;
import com.gymcrm.config.LoginAttemptService;
import com.gymcrm.config.TestConfig;
import com.gymcrm.controller.TrainingController;
import com.gymcrm.dto.trainee.AddTrainingRequestDto;
import com.gymcrm.dto.training.TraineeTrainingsListRequestDto;
import com.gymcrm.service.TrainingService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = TrainingController.class)
@Import(TestConfig.class)
public class TrainingControllerIntegrTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TrainingService trainingService;
    @MockBean
    private JwtUtil jwtUtil;
    @MockBean
    private LoginAttemptService loginAttemptService;

    @Test
    @WithMockUser(username = "test.trainee")
    void getTraineeTrainings() throws Exception {
        TraineeTrainingsListRequestDto requestDto = new TraineeTrainingsListRequestDto();
        when(trainingService.getTraineeTrainings(eq("test.trainee"), any())).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/training/trainee/trainings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "test.user")
    void addTrainings() throws Exception {
        AddTrainingRequestDto requestDto = new AddTrainingRequestDto();
        doNothing().when(trainingService).addTraining(eq("test.user"), any());

        mockMvc.perform(post("/api/training/add/trainings")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk());
    }
}
