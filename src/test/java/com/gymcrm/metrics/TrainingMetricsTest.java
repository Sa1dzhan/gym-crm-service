package com.gymcrm.metrics;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TrainingMetricsTest {

    private MeterRegistry registry;
    private TrainingMetrics metrics;

    @BeforeEach
    void setUp() {
        registry = new SimpleMeterRegistry();
        metrics = new TrainingMetrics(registry);
    }

    @Test
    void testTrainingCreatedCounter() {
        metrics.incrementTrainingCreated();
        assertEquals(1.0, registry.get("gym.training.created").counter().count());
    }

    @Test
    void testTrainingUpdatedCounter() {
        metrics.incrementTrainingUpdated();
        assertEquals(1.0, registry.get("gym.training.updated").counter().count());
    }

    @Test
    void testTrainingDeletedCounter() {
        metrics.incrementTrainingDeleted();
        assertEquals(1.0, registry.get("gym.training.deleted").counter().count());
    }
} 