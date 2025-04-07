package com.gymcrm.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

@Component
public class TrainingMetrics {
    private final Counter trainingCreatedCounter;
    private final Counter trainingUpdatedCounter;
    private final Counter trainingDeletedCounter;

    public TrainingMetrics(MeterRegistry registry) {
        this.trainingCreatedCounter = Counter.builder("gym.training.created")
                .description("Number of trainings created")
                .register(registry);

        this.trainingUpdatedCounter = Counter.builder("gym.training.updated")
                .description("Number of trainings updated")
                .register(registry);

        this.trainingDeletedCounter = Counter.builder("gym.training.deleted")
                .description("Number of trainings deleted")
                .register(registry);
    }

    public void incrementTrainingCreated() {
        trainingCreatedCounter.increment();
    }

    public void incrementTrainingUpdated() {
        trainingUpdatedCounter.increment();
    }

    public void incrementTrainingDeleted() {
        trainingDeletedCounter.increment();
    }
} 