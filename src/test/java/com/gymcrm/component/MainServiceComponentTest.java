package com.gymcrm.component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gymcrm.dto.LoginRequestDto;
import com.gymcrm.dto.message.WorkloadMessage;
import com.gymcrm.dto.message.WorkloadResponseMessage;
import com.gymcrm.dto.trainee.AddTrainingRequestDto;
import com.gymcrm.dto.trainee.TraineeCreateRequestDto;
import com.gymcrm.dto.trainer.TrainerCreateRequestDto;
import com.gymcrm.dto.training_type.TrainingTypeDto;
import com.gymcrm.dto.workload.WorkloadRequestDto;
import com.gymcrm.model.Training;
import com.gymcrm.model.TrainingType;
import com.gymcrm.util.Constants;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDate;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@AutoConfigureMockMvc
@AutoConfigureWireMock(port = 0)
class MainServiceComponentTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine");
    @Container
    static RabbitMQContainer rabbitMQ = new RabbitMQContainer("rabbitmq:3.12-management");

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private EntityManager entityManager;

    @TestConfiguration
    static class TestConfig {
        @Bean
        public TestRabbitListener testRabbitListener() {
            return new TestRabbitListener();
        }
    }

    @Slf4j
    public static class TestRabbitListener {
        @RabbitListener(queues = Constants.QUEUE_UPDATE)
        public WorkloadResponseMessage handleWorkloadUpdate(WorkloadMessage<WorkloadRequestDto> message) {
            log.info("Test listener received workload update for: {}", message.getUsername());
            WorkloadResponseMessage response = new WorkloadResponseMessage();
            response.setUsername(message.getUsername());
            response.setStatus(WorkloadResponseMessage.WorkloadStatus.SUCCESS);
            return response;
        }
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "update");

        registry.add("spring.rabbitmq.host", rabbitMQ::getHost);
        registry.add("spring.rabbitmq.port", rabbitMQ::getAmqpPort);
        registry.add("spring.rabbitmq.username", rabbitMQ::getAdminUsername);
        registry.add("spring.rabbitmq.password", rabbitMQ::getAdminPassword);

        registry.add("feign.client.config.workload-read.url", () -> "http://localhost:${wiremock.server.port}");
        registry.add("feign.client.config.workload-update.url", () -> "http://localhost:${wiremock.server.port}");
    }

    @Test
    @Transactional
    void testUserAndTraining() throws Exception {
        TrainingType type = new TrainingType();
        type.setTrainingTypeName("CrossFit");
        entityManager.persist(type);
        Long trainingTypeId = type.getId();

        // register trainee
        TraineeCreateRequestDto traineeRequest = new TraineeCreateRequestDto();
        traineeRequest.setFirstName("John");
        traineeRequest.setLastName("Doe");
        MvcResult traineeResult = mockMvc.perform(post("/api/trainee/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(traineeRequest)))
                .andExpect(status().isOk())
                .andReturn();
        Map<String, String> traineeResponse = objectMapper.readValue(traineeResult.getResponse().getContentAsString(), new TypeReference<>() {
        });
        String traineeUsername = traineeResponse.get("username");
        String traineeToken = traineeResponse.get("token");

        // register trainer
        TrainerCreateRequestDto trainerRequest = new TrainerCreateRequestDto();
        trainerRequest.setFirstName("Jane");
        trainerRequest.setLastName("Smith");
        trainerRequest.setSpecializationId(trainingTypeId);
        MvcResult trainerResult = mockMvc.perform(post("/api/trainer/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(trainerRequest)))
                .andExpect(status().isOk())
                .andReturn();
        Map<String, String> trainerResponse = objectMapper.readValue(trainerResult.getResponse().getContentAsString(), new TypeReference<>() {
        });
        String trainerUsername = trainerResponse.get("username");

        AddTrainingRequestDto trainingRequest = new AddTrainingRequestDto();
        trainingRequest.setTraineeUsername(traineeUsername);
        trainingRequest.setTrainerUsername(trainerUsername);
        trainingRequest.setTrainingName("Functional Training");
        trainingRequest.setTrainingDate(LocalDate.now());
        trainingRequest.setTrainingDuration(60L);
        TrainingTypeDto trainingTypeDto = new TrainingTypeDto();
        trainingTypeDto.setId(trainingTypeId);
        trainingRequest.setTrainingType(trainingTypeDto);

        assertNotNull(traineeToken, "Trainee token should not be null");
        mockMvc.perform(post("/api/training/add/trainings")
                        .header("Authorization", "Bearer " + traineeToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(trainingRequest)))
                .andExpect(status().isOk());

        // check if training created
        Training savedTraining = entityManager.createQuery("SELECT t FROM Training t WHERE t.trainingName = 'Functional Training'", Training.class).getSingleResult();
        assertNotNull(savedTraining);
        assertEquals(trainerUsername, savedTraining.getTrainer().getUsername());
    }

    @Test
    void testLoginBruteForceProtection() throws Exception {
        TraineeCreateRequestDto request = new TraineeCreateRequestDto();
        request.setFirstName("Block");
        request.setLastName("Me");
        MvcResult result = mockMvc.perform(post("/api/trainee/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn();
        Map<String, String> responseMap = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
        });
        String username = responseMap.get("username");

        LoginRequestDto badLogin = new LoginRequestDto();
        badLogin.setUsername(username);
        badLogin.setPassword("wrong-password");
        for (int i = 0; i < 3; i++) {
            mockMvc.perform(post("/api/trainee/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(badLogin)))
                    .andExpect(status().isUnauthorized());
        }

        mockMvc.perform(post("/api/trainee/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(badLogin)))
                .andExpect(status().isTooManyRequests());
    }
}
