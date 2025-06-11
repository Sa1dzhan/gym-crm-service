package com.gymcrm.gymservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gymcrm.config.JwtUtil;
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
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = TrainingController.class)
@Import({GlobalExceptionHandler.class, TestConfig.class})
public class TrainingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TrainingService trainingService;

    @MockBean
    private JwtUtil jwtUtil;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testGetTraineeTrainings_Success() throws Exception {
        TraineeTrainingsListRequestDto reqDto = new TraineeTrainingsListRequestDto();
        reqDto.setPeriodFrom(LocalDate.of(2021, 3, 26));
        reqDto.setPeriodTo(LocalDate.of(2022, 3, 26));
        reqDto.setTrainerName("Trainer A");
        reqDto.setTrainingTypeName("Strength");

        TraineeTrainingsListResponseDto respDto = new TraineeTrainingsListResponseDto();
        respDto.setTrainingName("Morning Workout");
        respDto.setTrainingDuration(60L);
        respDto.setTrainingDate(LocalDate.of(2021, 3, 26));
        respDto.setTrainerName("Trainer A");
        TrainingTypeDto ttDto = new TrainingTypeDto();
        ttDto.setId(1L);
        ttDto.setTrainingTypeName("Strength training");
        respDto.setTrainingType(ttDto);

        List<TraineeTrainingsListResponseDto> responseList = Collections.singletonList(respDto);

        when(trainingService.getTraineeTrainings(anyString(), any(TraineeTrainingsListRequestDto.class)))
                .thenReturn(responseList);

        String url = "/api/training/trainee/trainings";

        mockMvc.perform(get(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reqDto))
                        .with(user("traineeUser").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].trainingName").value("Morning Workout"))
                .andExpect(jsonPath("$[0].trainingType.trainingTypeName").value("Strength training"))
                .andExpect(jsonPath("$[0].trainerName").value("Trainer A"));
    }

    @Test
    void testGetTrainerTrainings_Success() throws Exception {
        TrainerTrainingsListRequestDto reqDto = new TrainerTrainingsListRequestDto();
        reqDto.setPeriodFrom(LocalDate.of(2018, 3, 26));
        reqDto.setPeriodTo(LocalDate.of(2019, 3, 26));
        reqDto.setTraineeName("Trainee B");

        TrainerTrainingsListResponseDto respDto = new TrainerTrainingsListResponseDto();
        respDto.setTrainingName("Evening Session");
        respDto.setTrainingDuration(45L);
        respDto.setTrainingDate(LocalDate.of(2018, 3, 26));
        respDto.setTraineeName("Trainee B");
        TrainingTypeDto ttDto = new TrainingTypeDto();
        ttDto.setId(2L);
        ttDto.setTrainingTypeName("Cardio");
        respDto.setTrainingType(ttDto);

        List<TrainerTrainingsListResponseDto> responseList = Collections.singletonList(respDto);

        when(trainingService.getTrainerTrainings(anyString(), any(TrainerTrainingsListRequestDto.class)))
                .thenReturn(responseList);

        String url = "/api/training/trainer/trainings";

        mockMvc.perform(get(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reqDto))
                        .with(user("trainerUser").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].trainingName").value("Evening Session"))
                .andExpect(jsonPath("$[0].trainingType.trainingTypeName").value("Cardio"))
                .andExpect(jsonPath("$[0].traineeName").value("Trainee B"));
    }

    @Test
    void testAddTrainings_Success() throws Exception {
        AddTrainingRequestDto reqDto = new AddTrainingRequestDto();
        reqDto.setTrainingName("Afternoon Workout");
        doNothing().when(trainingService).addTraining(anyString(), any(AddTrainingRequestDto.class));

        String url = "/api/training/add/trainings";

        mockMvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reqDto))
                        .with(user("trainerUser").roles("USER")))
                .andExpect(status().isOk());
    }
}
