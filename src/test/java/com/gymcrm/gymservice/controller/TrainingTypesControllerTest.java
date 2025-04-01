package com.gymcrm.gymservice.controller;

import com.gymcrm.controller.GlobalExceptionHandler;
import com.gymcrm.controller.TrainingTypesController;
import com.gymcrm.dto.training_type.TrainingTypeDto;
import com.gymcrm.service.TrainingTypesService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = TrainingTypesController.class)
@Import({GlobalExceptionHandler.class})
public class TrainingTypesControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TrainingTypesService trainingTypesService;

    @TestConfiguration
    static class TestConfig {
        @Bean
        @Primary
        public TrainingTypesService trainingService() {
            return Mockito.mock(TrainingTypesService.class);
        }
    }

    @Test
    void testGetTrainingTypes_Success() throws Exception {
        TrainingTypeDto dto1 = new TrainingTypeDto();
        dto1.setId(1L);
        dto1.setTrainingTypeName("Strength training");

        TrainingTypeDto dto2 = new TrainingTypeDto();
        dto2.setId(2L);
        dto2.setTrainingTypeName("Cardio");

        List<TrainingTypeDto> dtoList = Arrays.asList(dto1, dto2);
        when(trainingTypesService.getTrainingTypesList("user", "pass")).thenReturn(dtoList);

        mockMvc.perform(get("/api/training-types")
                        .param("username", "user")
                        .param("password", "pass")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].trainingTypeName").value("Strength training"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].trainingTypeName").value("Cardio"));
    }

    @Test
    void testGetTrainingTypes_Error() throws Exception {
        when(trainingTypesService.getTrainingTypesList("user", "wrongpass"))
                .thenThrow(new IllegalArgumentException("Invalid credentials"));

        mockMvc.perform(get("/api/training-types")
                        .param("username", "user")
                        .param("password", "wrongpass")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Invalid credentials"));
    }
}
