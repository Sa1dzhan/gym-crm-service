# Gym CRM Service

Service for managing a gym, including functionality for trainers and trainees.

## Requirements

- Java 17
- Maven 3.8+
- PostgreSQL 14+

## Configuration

The application supports the following profiles:

- `local` - for local development
- `dev` - for development
- `stg` - for testing
- `prod` - for production

### Running with different profiles

```bash
# Local development
mvn spring-boot:run -Dspring.profiles.active=local

# Development
mvn spring-boot:run -Dspring.profiles.active=dev

# Testing
mvn spring-boot:run -Dspring.profiles.active=stg

# Production
mvn spring-boot:run -Dspring.profiles.active=prod
```

## Monitoring

The application provides the following endpoints for monitoring:

- `/actuator/health` - application health status
- `/actuator/metrics` - application metrics
- `/actuator/prometheus` - metrics in Prometheus format

## Security

All endpoints except registration and monitoring endpoints require basic authentication.

## Testing

To run tests:

```bash
mvn test
```

## Logging

Logging is configured at DEBUG level for the `com.gymcrm` package and INFO for other components.
