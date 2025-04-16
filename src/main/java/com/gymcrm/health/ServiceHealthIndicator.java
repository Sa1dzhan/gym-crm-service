package com.gymcrm.health;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
public class ServiceHealthIndicator implements HealthIndicator {

    @Override
    public Health health() {
        try {
            // Здесь можно добавить дополнительную логику проверки состояния сервиса
            return Health.up()
                    .withDetail("service", "gym-service")
                    .withDetail("status", "operational")
                    .build();
        } catch (Exception e) {
            return Health.down()
                    .withDetail("service", "gym-service")
                    .withDetail("status", "degraded")
                    .withException(e)
                    .build();
        }
    }
} 