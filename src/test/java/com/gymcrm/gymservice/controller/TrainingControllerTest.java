package com.gymcrm.gymservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gymcrm.config.TestConfig;
import com.gymcrm.controller.GlobalExceptionHandler;
import com.gymcrm.controller.TrainingController;
import com.gymcrm.dto.trainee.AddTrainingRequestDto;
import com.gymcrm.dto.training.TraineeTrainingsListRequestDto;
import com.gymcrm.dto.training.TraineeTrainingsListResponseDto;
import com.gymcrm.dto.training.TrainerTrainingsListRequestDto;
import com.gymcrm.dto.training.TrainerTrainingsListResponseDto;
import com.gymcrm.dto.training_type.TrainingTypeDto;
import com.gymcrm.service.TrainingService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = TrainingController.class)
@Import({GlobalExceptionHandler.class, TestConfig.class, TrainingControllerTest.TestConfig.class})
public class TrainingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TrainingService trainingService;

    @Autowired
    private ObjectMapper objectMapper;

    @TestConfiguration
    static class TestConfig {
        @Bean
        public TrainingService trainingService() {
            return org.mockito.Mockito.mock(TrainingService.class);
        }
    }

    @Test
    void testGetTraineeTrainings_Success() throws Exception {
        TraineeTrainingsListRequestDto reqDto = new TraineeTrainingsListRequestDto();
        reqDto.setUsername("traineeUser");
        reqDto.setPassword("pass");
        reqDto.setPeriodFrom(new Date());
        reqDto.setPeriodTo(new Date());
        reqDto.setTrainerName("Trainer A");
        reqDto.setTrainingTypeName("Strength");

        TraineeTrainingsListResponseDto respDto = new TraineeTrainingsListResponseDto();
        respDto.setTrainingName("Morning Workout");
        respDto.setTrainingDuration(60L);
        respDto.setTrainingDate(new Date());
        respDto.setTrainerName("Trainer A");
        TrainingTypeDto ttDto = new TrainingTypeDto();
        ttDto.setId(1L);
        ttDto.setTrainingTypeName("Strength training");
        respDto.setTrainingType(ttDto);

        List<TraineeTrainingsListResponseDto> responseList = Collections.singletonList(respDto);

        when(trainingService.getTraineeTrainings(any(TraineeTrainingsListRequestDto.class)))
                .thenReturn(responseList);

        String url = "/api/training/traineeUser/trainee/trainings";

        mockMvc.perform(get(url)
                        .content(objectMapper.writeValueAsString(reqDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].trainingName").value("Morning Workout"))
                .andExpect(jsonPath("$[0].trainingType.trainingTypeName").value("Strength training"))
                .andExpect(jsonPath("$[0].trainerName").value("Trainer A"));
    }

    @Test
    void testGetTrainerTrainings_Success() throws Exception {
        TrainerTrainingsListRequestDto reqDto = new TrainerTrainingsListRequestDto();
        reqDto.setUsername("trainerUser");
        reqDto.setPassword("pass");
        reqDto.setPeriodFrom(new Date());
        reqDto.setPeriodTo(new Date());
        reqDto.setTraineeName("Trainee B");

        TrainerTrainingsListResponseDto respDto = new TrainerTrainingsListResponseDto();
        respDto.setTrainingName("Evening Session");
        respDto.setTrainingDuration(45L);
        respDto.setTrainingDate(new Date());
        respDto.setTraineeName("Trainee B");
        TrainingTypeDto ttDto = new TrainingTypeDto();
        ttDto.setId(2L);
        ttDto.setTrainingTypeName("Cardio");
        respDto.setTrainingType(ttDto);

        List<TrainerTrainingsListResponseDto> responseList = Collections.singletonList(respDto);

        when(trainingService.getTrainerTrainings(any(TrainerTrainingsListRequestDto.class)))
                .thenReturn(responseList);

        String url = "/api/training/trainerUser/trainer/trainings";

        mockMvc.perform(get(url)
                        .content(objectMapper.writeValueAsString(reqDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].trainingName").value("Evening Session"))
                .andExpect(jsonPath("$[0].trainingType.trainingTypeName").value("Cardio"))
                .andExpect(jsonPath("$[0].traineeName").value("Trainee B"));
    }

    @Test
    void testAddTrainings_Success() throws Exception {
        AddTrainingRequestDto reqDto = new AddTrainingRequestDto();
        reqDto.setUsername("trainerUser");
        reqDto.setPassword("pass");
        reqDto.setTrainingName("Afternoon Workout");
        doNothing().when(trainingService).addTraining(any(AddTrainingRequestDto.class));

        String url = "/api/training/add/trainings";

        mockMvc.perform(post(url)
                        .content(objectMapper.writeValueAsString(reqDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}

