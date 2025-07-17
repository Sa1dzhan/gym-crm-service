package com.gymcrm.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
public class SecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserDetailsService userDetailsService;

    @Test
    @WithAnonymousUser
    void whenAnonymousUser_accessingProtectedEndpoint_thenIsForbidden() throws Exception {
        mockMvc.perform(get("/api/trainer/profile"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "TRAINEE")
    void whenUserWithWrongRole_thenIsForbidden() throws Exception {
        mockMvc.perform(get("/api/trainer/profile"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "TRAINER")
    void whenUserWithCorrectRole_thenSecurityPasses() throws Exception {
        mockMvc.perform(get("/api/trainer/profile"))
                .andExpect(status().isBadRequest());
    }
}
