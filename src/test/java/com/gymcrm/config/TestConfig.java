package com.gymcrm.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;

@TestConfiguration
@Import(TestSecurityConfig.class)
public class TestConfig {
    @Bean
    @Primary
    public TransactionIdFilter transactionIdFilter() {
        return new TransactionIdFilter();
    }

    @Bean
    public LoginAttemptService loginAttemptService() {
        return new LoginAttemptService();
    }
}
