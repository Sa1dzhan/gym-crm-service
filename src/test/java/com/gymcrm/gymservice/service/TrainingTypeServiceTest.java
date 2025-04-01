package com.gymcrm.gymservice.service;

import com.gymcrm.converter.TrainingTypeMapper;
import com.gymcrm.dao.GeneralUserRepository;
import com.gymcrm.dao.TrainingTypeRepository;
import com.gymcrm.dto.training_type.TrainingTypeDto;
import com.gymcrm.model.TrainingType;
import com.gymcrm.model.User;
import com.gymcrm.service.impl.TrainingTypesServiceImpl;
import com.gymcrm.util.Authentication;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TrainingTypeServiceTest {

    @Mock
    private TrainingTypeRepository trainingTypeRepository;

    @Mock
    private TrainingTypeMapper trainingTypeMapper;

    @Mock
    private GeneralUserRepository userRepository;

    @InjectMocks
    private TrainingTypesServiceImpl trainingTypesService;

    private MockedStatic<Authentication> authenticationMock;

    @BeforeEach
    void setUp() {
        authenticationMock = mockStatic(Authentication.class);
    }

    @AfterEach
    void tearDown() {
        authenticationMock.close();
    }

    @Test
    void getTrainingTypesList_Success() {
        String username = "user";
        String password = "pass";

        User dummyUser = new User();
        authenticationMock.when(() ->
                Authentication.authenticateUser(eq(username), eq(password), any())
        ).thenReturn(dummyUser);

        TrainingType tt1 = new TrainingType();
        tt1.setId(1L);
        tt1.setTrainingTypeName("Cardio");

        TrainingType tt2 = new TrainingType();
        tt2.setId(2L);
        tt2.setTrainingTypeName("Strength");

        List<TrainingType> trainingTypes = Arrays.asList(tt1, tt2);
        when(trainingTypeRepository.findAll()).thenReturn(trainingTypes);

        TrainingTypeDto dto1 = new TrainingTypeDto();
        dto1.setTrainingTypeName("Cardio");
        TrainingTypeDto dto2 = new TrainingTypeDto();
        dto2.setTrainingTypeName("Strength");

        when(trainingTypeMapper.toResponseDTO(tt1)).thenReturn(dto1);
        when(trainingTypeMapper.toResponseDTO(tt2)).thenReturn(dto2);

        List<TrainingTypeDto> result = trainingTypesService.getTrainingTypesList(username, password);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Cardio", result.get(0).getTrainingTypeName());
        assertEquals("Strength", result.get(1).getTrainingTypeName());

        authenticationMock.verify(() ->
                Authentication.authenticateUser(eq(username), eq(password), any())
        );
    }

    @Test
    void getTrainingTypesList_Failed() {
        String username = "user";
        String password = "wrongPass";

        authenticationMock.when(() ->
                Authentication.authenticateUser(eq(username), eq(password), any())
        ).thenThrow(new RuntimeException("Authentication failed"));

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                trainingTypesService.getTrainingTypesList(username, password)
        );

        assertEquals("Authentication failed", ex.getMessage());
    }
}
