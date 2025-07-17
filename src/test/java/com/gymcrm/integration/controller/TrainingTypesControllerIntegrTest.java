package com.gymcrm.integration.controller;

import com.gymcrm.config.JwtUtil;
import com.gymcrm.config.LoginAttemptService;
import com.gymcrm.config.TestConfig;
import com.gymcrm.controller.TrainingTypesController;
import com.gymcrm.service.TrainingTypesService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = TrainingTypesController.class)
@Import(TestConfig.class)
public class TrainingTypesControllerIntegrTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TrainingTypesService trainingTypesService;
    @MockBean
    private JwtUtil jwtUtil;
    @MockBean
    private LoginAttemptService loginAttemptService;

    @Test
    @WithMockUser(username = "test.user")
    void getTrainingTypes() throws Exception {
        when(trainingTypesService.getTrainingTypesList(anyString())).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/training-types"))
                .andExpect(status().isOk());
    }
}
