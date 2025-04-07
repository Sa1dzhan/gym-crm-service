package com.gymcrm.gymservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gymcrm.config.TestConfig;
import com.gymcrm.controller.GlobalExceptionHandler;
import com.gymcrm.controller.TraineeController;
import com.gymcrm.dto.UserCreatedResponseDto;
import com.gymcrm.dto.trainee.TraineeCreateRequestDto;
import com.gymcrm.dto.trainee.TraineeNotAssignedTrainersDto;
import com.gymcrm.dto.trainee.TraineeProfileResponseDto;
import com.gymcrm.dto.trainee.TraineeUpdateRequestDto;
import com.gymcrm.dto.trainer.TrainerShortProfileDto;
import com.gymcrm.service.TraineeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = TraineeController.class)
@Import({GlobalExceptionHandler.class, TestConfig.class, TraineeControllerTest.TestConfig.class})
public class TraineeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TraineeService traineeService;

    @Autowired
    private ObjectMapper objectMapper;

    @TestConfiguration
    static class TestConfig {
        @Bean
        public TraineeService traineeService() {
            return org.mockito.Mockito.mock(TraineeService.class);
        }
    }

    @Test
    void testCreateTrainee_Success() throws Exception {
        TraineeCreateRequestDto createDto = new TraineeCreateRequestDto();
        createDto.setFirstName("Alice");
        createDto.setLastName("Smith");
        createDto.setDateOfBirth(new Date());
        createDto.setAddress("123 Street");

        UserCreatedResponseDto authResponse = new UserCreatedResponseDto();
        authResponse.setUsername("alice.smith");

        when(traineeService.createTrainee(any(TraineeCreateRequestDto.class)))
                .thenReturn(authResponse);

        String url = "/api/trainee/register";
        mockMvc.perform(post(url)
                        .content(objectMapper.writeValueAsString(createDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("alice.smith"));
    }

    @Test
    void testLogin_Success() throws Exception {
        doNothing().when(traineeService).login(eq("alice"), eq("password"));

        String url = "/api/trainee/alice/login?password=password";
        mockMvc.perform(get(url)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void testChangePassword_Success() throws Exception {
        doNothing().when(traineeService).changePassword(eq("alice"), eq("oldPass"), eq("newPass"));

        String url = "/api/trainee/alice/update/password?oldPassword=oldPass&newPassword=newPass";
        mockMvc.perform(put(url)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void testGetTraineeProfile_Success() throws Exception {
        TraineeProfileResponseDto profile = new TraineeProfileResponseDto();
        profile.setId(1L);
        profile.setUsername("alice");
        profile.setFirstName("Alice");
        profile.setLastName("Smith");
        profile.setDateOfBirth(new Date());
        profile.setAddress("123 Street");
        profile.setIsActive(true);
        profile.setTrainers(null);

        when(traineeService.getByUsername(eq("alice"), eq("password")))
                .thenReturn(profile);

        String url = "/api/trainee/alice?password=password";
        mockMvc.perform(get(url)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("alice"))
                .andExpect(jsonPath("$.firstName").value("Alice"));
    }

    @Test
    void testUpdateTraineeProfile_Success() throws Exception {
        TraineeUpdateRequestDto updateDto = new TraineeUpdateRequestDto();
        updateDto.setFirstName("Alice");
        updateDto.setLastName("Johnson");
        updateDto.setDateOfBirth(new Date());
        updateDto.setAddress("456 Avenue");
        updateDto.setIsActive(true);

        TraineeProfileResponseDto profile = new TraineeProfileResponseDto();
        profile.setId(1L);
        profile.setUsername("alice");
        profile.setFirstName("Alice");
        profile.setLastName("Johnson");
        profile.setDateOfBirth(new Date());
        profile.setAddress("456 Avenue");
        profile.setIsActive(true);
        profile.setTrainers(null);

        when(traineeService.updateTrainee(any(TraineeUpdateRequestDto.class))).thenReturn(profile);

        String url = "/api/trainee/alice/update/profile";
        mockMvc.perform(put(url)
                        .content(objectMapper.writeValueAsString(updateDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void testDeleteTrainee_Success() throws Exception {
        doNothing().when(traineeService).deleteTraineeByUsername(eq("alice"), eq("password"));

        String url = "/api/trainee/alice?password=password";
        mockMvc.perform(delete(url)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void testGetNotAssignedTrainers_Success() throws Exception {
        TrainerShortProfileDto trainerDto = new TrainerShortProfileDto();
        trainerDto.setUsername("trainer1");
        trainerDto.setFirstName("Bob");
        trainerDto.setLastName("Brown");
        TraineeNotAssignedTrainersDto notAssignedDto =
                new TraineeNotAssignedTrainersDto(Collections.singletonList(trainerDto));

        when(traineeService.getTrainersNotAssigned(eq("alice"), eq("password")))
                .thenReturn(notAssignedDto);

        String url = "/api/trainee/alice/not-assigned-trainers?password=password";
        mockMvc.perform(get(url)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.trainers[0].username").value("trainer1"));
    }

    @Test
    void testUpdateTrainersList_Success() throws Exception {
        List<String> trainerUsernames = Arrays.asList("trainer1", "trainer2");

        TrainerShortProfileDto trainerDto1 = new TrainerShortProfileDto();
        trainerDto1.setUsername("trainer1");
        trainerDto1.setFirstName("Bob");
        trainerDto1.setLastName("Brown");

        TrainerShortProfileDto trainerDto2 = new TrainerShortProfileDto();
        trainerDto2.setUsername("trainer2");
        trainerDto2.setFirstName("Charlie");
        trainerDto2.setLastName("Clark");

        List<TrainerShortProfileDto> responseList = Arrays.asList(trainerDto1, trainerDto2);

        when(traineeService.updateTrainersList(eq("alice"), eq("password"), any(List.class)))
                .thenReturn(responseList);

        String url = "/api/trainee/alice/trainers?password=password";
        mockMvc.perform(put(url)
                        .content(objectMapper.writeValueAsString(trainerUsernames))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("trainer1"))
                .andExpect(jsonPath("$[1].username").value("trainer2"));
    }

    @Test
    void testToggleTraineeActive_Success() throws Exception {
        doNothing().when(traineeService).toggleActive(eq("alice"), eq("password"));

        String url = "/api/trainee/alice/activate?password=password";
        mockMvc.perform(patch(url)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}

