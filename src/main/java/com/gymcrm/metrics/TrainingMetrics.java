package com.gymcrm.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

@Component
public class TrainingMetrics {
    private final Counter trainingCreatedCounter;

    public TrainingMetrics(MeterRegistry registry) {
        this.trainingCreatedCounter = Counter.builder("gym.training.created")
                .description("Number of trainings created")
                .register(registry);
    }

    public void incrementTrainingCreated() {
        trainingCreatedCounter.increment();
    }
} 
