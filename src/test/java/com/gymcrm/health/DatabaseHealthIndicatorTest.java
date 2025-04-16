package com.gymcrm.health;

import org.junit.jupiter.api.Test;
import org.springframework.boot.actuate.health.Health;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class DatabaseHealthIndicatorTest {

    @Test
    void testHealthUp() {
        JdbcTemplate jdbcTemplate = mock(JdbcTemplate.class);
        when(jdbcTemplate.queryForObject(anyString(), any(Class.class))).thenReturn(1);

        DatabaseHealthIndicator indicator = new DatabaseHealthIndicator(jdbcTemplate);
        Health health = indicator.health();

        assertEquals(Health.up().build().getStatus(), health.getStatus());
    }

    @Test
    void testHealthDown() {
        JdbcTemplate jdbcTemplate = mock(JdbcTemplate.class);
        when(jdbcTemplate.queryForObject(anyString(), any(Class.class)))
                .thenThrow(new RuntimeException("Database connection failed"));

        DatabaseHealthIndicator indicator = new DatabaseHealthIndicator(jdbcTemplate);
        Health health = indicator.health();

        assertEquals(Health.down().build().getStatus(), health.getStatus());
    }
} 