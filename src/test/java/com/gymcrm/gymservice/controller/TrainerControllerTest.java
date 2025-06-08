package com.gymcrm.gymservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gymcrm.config.JwtUtil;
import com.gymcrm.config.LoginAttemptService;
import com.gymcrm.config.TestConfig;
import com.gymcrm.controller.GlobalExceptionHandler;
import com.gymcrm.controller.TrainerController;
import com.gymcrm.dto.ChangePasswordRequestDto;
import com.gymcrm.dto.LoginRequestDto;
import com.gymcrm.dto.UserCreatedResponseDto;
import com.gymcrm.dto.trainer.TrainerCreateRequestDto;
import com.gymcrm.dto.trainer.TrainerProfileResponseDto;
import com.gymcrm.dto.trainer.TrainerUpdateRequestDto;
import com.gymcrm.dto.training_type.TrainingTypeDto;
import com.gymcrm.service.TrainerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = TrainerController.class)
@Import({GlobalExceptionHandler.class, TestConfig.class})
public class TrainerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TrainerService trainerService;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private LoginAttemptService loginAttemptService;

    @Autowired
    private ObjectMapper objectMapper;

    private Authentication authentication;
    private SecurityContext securityContext;

    @BeforeEach
    void setUp() {
        authentication = mock(Authentication.class);
        securityContext = mock(SecurityContext.class);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getName()).thenReturn("john.doe");
    }

    @Test
    void testCreateTrainer_Success() throws Exception {
        TrainerCreateRequestDto createDto = new TrainerCreateRequestDto();
        createDto.setFirstName("John");
        createDto.setLastName("Doe");
        createDto.setSpecializationId(1L);

        UserCreatedResponseDto authResponse = new UserCreatedResponseDto();
        authResponse.setUsername("john.doe");
        authResponse.setPassword("generatedPass");

        when(trainerService.createTrainer(any(TrainerCreateRequestDto.class)))
                .thenReturn(authResponse);
        when(jwtUtil.generateToken(anyString())).thenReturn("jwt-token");

        String url = "/api/trainer/register";

        mockMvc.perform(post(url)
                        .content(objectMapper.writeValueAsString(createDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("john.doe"))
                .andExpect(jsonPath("$.token").value("jwt-token"));
    }

    @Test
    void testLogin_Success() throws Exception {
        LoginRequestDto loginDto = new LoginRequestDto();
        loginDto.setUsername("john.doe");
        loginDto.setPassword("password");

        doNothing().when(trainerService).login(eq("john.doe"), eq("password"));
        when(loginAttemptService.isBlocked(anyString())).thenReturn(false);
        when(jwtUtil.generateToken(anyString())).thenReturn("jwt-token");

        String url = "/api/trainer/login";

        mockMvc.perform(post(url)
                        .content(objectMapper.writeValueAsString(loginDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-token"));
    }

    @Test
    void testChangePassword_Success() throws Exception {
        ChangePasswordRequestDto changePasswordDto = new ChangePasswordRequestDto();
        changePasswordDto.setOldPassword("oldPassword");
        changePasswordDto.setNewPassword("newPassword");

        doNothing().when(trainerService).changePassword(eq("john.doe"), eq("oldPassword"), eq("newPassword"));

        String url = "/api/trainer/update/password";

        mockMvc.perform(put(url)
                        .with(user("john.doe"))
                        .content(objectMapper.writeValueAsString(changePasswordDto))
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

        when(trainerService.getByUsername(eq("john.doe")))
                .thenReturn(profile);

        String url = "/api/trainer/profile";

        mockMvc.perform(get(url)
                        .with(user("john.doe"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("john.doe"))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.specialization.trainingTypeName").value("Strength training"));
    }

    @Test
    void testUpdateTrainerProfile_Success() throws Exception {
        TrainerUpdateRequestDto updateDto = new TrainerUpdateRequestDto();
        updateDto.setFirstName("John");
        updateDto.setLastName("Doe");
        updateDto.setSpecializationId(1L);
        updateDto.setIsActive(true);

        TrainerProfileResponseDto profile = new TrainerProfileResponseDto();
        profile.setId(1L);
        profile.setUsername("john.doe");
        profile.setFirstName("John");
        profile.setLastName("Doe");
        TrainingTypeDto cardioDto = new TrainingTypeDto();
        cardioDto.setId(1L);
        cardioDto.setTrainingTypeName("Strength training");
        profile.setSpecialization(cardioDto);
        profile.setIsActive(true);
        profile.setTrainees(null);

        when(trainerService.updateTrainer(any(TrainerUpdateRequestDto.class))).thenReturn(profile);

        String url = "/api/trainer/update/profile";

        mockMvc.perform(put(url)
                        .with(user("john.doe"))
                        .content(objectMapper.writeValueAsString(updateDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("john.doe"));
    }

    @Test
    void testToggleTrainerActive_Success() throws Exception {
        doNothing().when(trainerService).toggleActive(eq("john.doe"));

        String url = "/api/trainer/activate";

        mockMvc.perform(patch(url)
                        .with(user("john.doe"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
