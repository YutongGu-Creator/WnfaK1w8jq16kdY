# Weather Sensor Application

## Overview

The Weather Sensor Application is a Spring Boot application designed to collect, store, and analyze weather data from various sensors.

## Getting Started

### Prerequisites

- Java 17 or higher
- Maven 3.6.0 or higher

### Installation

1. Clone the repository:
   ```sh
   git clone https://github.com/YutongGu-Creator/WnfaK1w8jq16kdY
   cd weathersensor
   ```

2. Build the project using Maven:
   ```sh
   mvn clean install
   ```

3. Run the application:
   ```sh
   mvn spring-boot:run
   ```

## Usage

### API Endpoints

- **POST /data**: Record weather metrics for a specified sensor.

- **GET /metrics**: Retrieve weather metrics for a specified sensor and date range.
    - Parameters:
        - `sensorId`: ID of the sensor.
        - `metrics`: List of metrics to retrieve (e.g., temperature, humidity, windspeed).
        - `statistic`: Type of statistic to calculate (e.g., min, max, sum) (default to average).
        - `startDate`: (optional) Start date of the range.
        - `endDate`: (optional) End date of the range.

### Example Request

```sh
curl -X POST http://localhost:8080/api/sensors/data \
-H "Content-Type: application/json" \
-d '{"sensorId":"1","temperature":11.5,"humidity":53.2,"windSpeed":3.5}'
```

```sh
curl -X GET http://localhost:8080/api/sensors/metrics?sensorId=1&metrics=temperature,humidity,windspeed&statistic=max&startDate=2024-11-07&endDate=2024-11-08
```

## Running Tests

To run the tests, use the following command:

```sh
mvn test
```

## Database
H2 is used for persistent data storage, example database is under src/main/resources/demodb.mv.db, and configurable through spring.datasource.url under application.properties.