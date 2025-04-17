package com.gymcrm.gymservice.controller;

import com.gymcrm.config.JwtUtil;
import com.gymcrm.config.TestConfig;
import com.gymcrm.controller.GlobalExceptionHandler;
import com.gymcrm.controller.TrainingTypesController;
import com.gymcrm.dto.training_type.TrainingTypeDto;
import com.gymcrm.service.TrainingTypesService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = TrainingTypesController.class)
@Import({GlobalExceptionHandler.class, TestConfig.class})
public class TrainingTypesControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TrainingTypesService trainingTypesService;

    @MockBean
    private JwtUtil jwtUtil;

    @Test
    void testGetTrainingTypes_Success() throws Exception {
        TrainingTypeDto dto1 = new TrainingTypeDto();
        dto1.setId(1L);
        dto1.setTrainingTypeName("Strength training");

        TrainingTypeDto dto2 = new TrainingTypeDto();
        dto2.setId(2L);
        dto2.setTrainingTypeName("Cardio");

        List<TrainingTypeDto> dtoList = Arrays.asList(dto1, dto2);
        when(trainingTypesService.getTrainingTypesList(ArgumentMatchers.anyString())).thenReturn(dtoList);

        mockMvc.perform(get("/api/training-types")
                        .with(user("user").roles("USER"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].trainingTypeName").value("Strength training"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].trainingTypeName").value("Cardio"));
    }

    @Test
    void testGetTrainingTypes_Error() throws Exception {
        when(trainingTypesService.getTrainingTypesList(ArgumentMatchers.anyString()))
                .thenThrow(new IllegalArgumentException("Invalid credentials"));

        mockMvc.perform(get("/api/training-types")
                        .with(user("user").roles("USER"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Invalid credentials"));
    }
}
