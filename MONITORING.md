# Monitoring Setup for Gym Service

This guide explains how to set up and use Prometheus and Grafana to visualize metrics from your Spring Boot application.

## Prerequisites

- Docker and Docker Compose installed on your machine
- Your Spring Boot application running on port 8079

## Getting Started

1. Start your Spring Boot application:

```bash
./mvnw spring-boot:run
```

2. Start Prometheus and Grafana using Docker Compose:

```bash
docker-compose up -d
```

3. Access the monitoring tools:
    - Prometheus: http://localhost:9090
    - Grafana: http://localhost:3000 (login with admin/admin)

## Configuring Grafana

1. Log in to Grafana at http://localhost:3000 with username `admin` and password `admin`
2. Go to "Configuration" > "Data sources"
3. Click "Add data source" and select "Prometheus"
4. Set the URL to `http://prometheus:9090` and click "Save & Test"
5. Import the dashboard:
    - Go to "+" > "Import"
    - Click "Upload JSON file" and select the `grafana-dashboard.json` file
    - Click "Import"

## Available Metrics

The dashboard includes:

1. **Training Creation Rate**: Shows the rate at which trainings are created
2. **Application Status**: Shows if the application is up or down
3. **Service Health**: Shows a 5-minute average of service uptime as a gauge
4. **JVM Heap Memory Usage**: Shows memory usage of your application
5. **CPU Usage**: Shows CPU usage of your application
6. **User Login Rate**: Shows the rate at which users log in
7. **User Registration Rate**: Shows the rate at which users register
8. **User Profile Update Rate**: Shows the rate at which users update their profiles
9. **User Activity Totals**: Shows total counts of user logins, registrations, and profile updates

## Health Indicators

Your application's health indicators are available at:

- http://localhost:8079/actuator/health

You can see detailed health information including:

- Your custom `DatabaseHealthIndicator` which checks database connectivity
- Your custom `ServiceHealthIndicator` which reports the overall service status

## Adding Custom Metrics

To add more custom metrics to your application:

1. Create a new metrics class similar to `TrainingMetrics.java`
2. Inject `MeterRegistry` and create counters, gauges, timers, etc.
3. Use these metrics in your services

Example:

```java

@Component
public class UserMetrics {
    private final Counter userRegisteredCounter;

    public UserMetrics(MeterRegistry registry) {
        this.userRegisteredCounter = Counter.builder("gym.user.registered")
                .description("Number of users registered")
                .register(registry);
    }

    public void incrementUserRegistered() {
        userRegisteredCounter.increment();
    }
}
```

## Troubleshooting

- If Prometheus can't connect to your application, check that:
    - Your application is running
    - The port in `prometheus.yml` matches your application's port
    - Your firewall isn't blocking connections
- If metrics aren't showing up in Grafana, verify that:
    - The Prometheus data source is configured correctly
    - Your application is exposing metrics at `/actuator/prometheus`
