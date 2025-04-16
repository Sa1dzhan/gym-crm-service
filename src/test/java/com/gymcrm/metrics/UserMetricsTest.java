package com.gymcrm.metrics;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserMetricsTest {

    private MeterRegistry registry;
    private UserMetrics metrics;

    @BeforeEach
    void setUp() {
        registry = new SimpleMeterRegistry();
        metrics = new UserMetrics(registry);
    }

    @Test
    void testUserLoginCounter() {
        metrics.incrementUserLogin();
        assertEquals(1.0, registry.get("gym.user.login").counter().count());
    }

    @Test
    void testUserRegistrationCounter() {
        metrics.incrementUserRegistration();
        assertEquals(1.0, registry.get("gym.user.registration").counter().count());
    }

    @Test
    void testUserProfileUpdateCounter() {
        metrics.incrementUserProfileUpdate();
        assertEquals(1.0, registry.get("gym.user.profile.update").counter().count());
    }
} 