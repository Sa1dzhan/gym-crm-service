package com.gymcrm.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

@Component
public class UserMetrics {
    private final Counter userLoginCounter;
    private final Counter userRegistrationCounter;
    private final Counter userProfileUpdateCounter;

    public UserMetrics(MeterRegistry registry) {
        this.userLoginCounter = Counter.builder("gym.user.login")
                .description("Number of user logins")
                .register(registry);

        this.userRegistrationCounter = Counter.builder("gym.user.registration")
                .description("Number of user registrations")
                .register(registry);

        this.userProfileUpdateCounter = Counter.builder("gym.user.profile.update")
                .description("Number of user profile updates")
                .register(registry);
    }

    public void incrementUserLogin() {
        userLoginCounter.increment();
    }

    public void incrementUserRegistration() {
        userRegistrationCounter.increment();
    }

    public void incrementUserProfileUpdate() {
        userProfileUpdateCounter.increment();
    }
} 