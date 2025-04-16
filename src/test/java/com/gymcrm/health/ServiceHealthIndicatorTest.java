package com.gymcrm.health;

import org.junit.jupiter.api.Test;
import org.springframework.boot.actuate.health.Health;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ServiceHealthIndicatorTest {

    @Test
    void testHealthUp() {
        ServiceHealthIndicator indicator = new ServiceHealthIndicator();
        Health health = indicator.health();

        assertEquals(Health.up().build().getStatus(), health.getStatus());
    }
} 