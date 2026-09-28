# Fraud Rule Engine Service

This is a production-grade microservice built with Spring Boot and Kafka to process customer financial transactions and flag potential fraud based on predefined rules.

## Features

- **Kafka Integration**: Consumes transaction events from a Kafka topic.
- **Fraud Detection**: Applies multiple rules to each transaction:
- **High Value**: Transactions above R10,000.
- **Suspicious Category**: Transactions in categories like "Gambling" or "Crypto".
- **Off-Hours**: Transactions occurring between 00:00 and 04:00.
- **High Velocity**: Multiple transactions (more than 3) from the same customer within 5 minutes.
- **Data storage**: Stores fraud alerts in an H2 in-memory database (for demonstration purposes, can be swapped for PostgreSQL/MySQL).
- **REST API**: Provides endpoints to send transactions and retrieve aggregated fraud alert data.

## Prerequisites

- Java 17 or higher (Java 21 recommended)
- Docker and Docker Compose
- Maven Wrapper (included)

## How to Build

### Using Maven Wrapper
```bash
./mvnw clean install
```

### Using Docker
```bash
docker build -t fraud-rule-engine .
```

## How to Run

### Using Docker Compose 
This will start both the Fraud Rule Engine and a Kafka/Zookeeper environment.

```bash
docker-compose up -d
```
*(Note: if your local kafka running , ignore this step)*

### Running Locally
1. Start a Kafka broker on `localhost:9092`.
2. Run the application:
```bash
./mvnw spring-boot:run
```

## API Endpoints

### Transactions (created this controller to push message to transactionconsumer)
- **Send transaction for processing**: `POST /api/transactions/post-transaction`
  - Payload: See `test-payloads.sh` for examples.

### Fraud Alerts
- **Get all fraud alerts**: `GET /api/fraud-alerts`
  - Optional query parameter: `severity` (e.g., `?severity=HIGH`)
- **Get alerts by customer ID**: `GET /api/fraud-alerts/customer/{customerId}`

## Testing

Run unit and integration tests:
```bash
./mvnw test
```

## Production Considerations
- **Security**: Basic Authentication enabled for all API endpoints.
- **Resilience**: Kafka consumer includes error handling and is configured with a Dead Letter Queue (DLQ) for failed messages.
- **Monitoring**: Integration with Spring Boot Actuator for health checks, metrics, and Prometheus support.
- **Scalability**: Stateless architecture ready for horizontal scaling (Note: Distributed velocity check recommended for multi-instance deployments).
- **Extensibility**: Rules are implemented using a Strategy pattern, making it easy to add new fraud detection logic.
