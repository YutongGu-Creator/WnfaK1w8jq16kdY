package org.example.weathersensor.service;

import org.example.weathersensor.data.SensorData;
import org.example.weathersensor.data.SensorDataRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
public class SensorDataService {
    private final SensorDataRepository repository;

    public SensorDataService(SensorDataRepository repository) {
        this.repository = repository;
    }

    public SensorData saveSensorData(SensorData data) {
        data.setTimestamp(new Date()); // Fill with current date
        return repository.save(data);
    }

    public Map<String, Double> getMetrics(List<String> sensorId, List<String> metrics, String statistic, Optional<Date> startDate, Optional<Date> endDate) {
        var end = endDate.orElse(new Date()); // Use current date as end date if none present
        var start = startDate.orElse(new Date(end.getTime() - 24 * 60 * 60 * 1000)); // 24hr before end date

        validateDateRange(start, end);

        List<SensorData> data = new ArrayList<>();
        for (String id : sensorId) {
            data.addAll(repository.findBySensorIdAndTimestampBetween(id, start, end));
        }

        Map<String, Double> result = new HashMap<>();

        for (String metric : metrics) {
            var value = calculateSensorDate(data, metric, statistic);
            result.put(metric, value);
        }

        return result;
    }

    void validateDateRange(Date start, Date end) {
        // Convert to LocalDateTime for ease of handling
        var startDateTime = start.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        var endDateTime = end.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();

        if (startDateTime.isAfter(LocalDateTime.now())) {
            throw new IllegalArgumentException(
                    "Start date cannot be in the future");
        }

        if (startDateTime.isAfter(endDateTime)) {
            throw new IllegalArgumentException(
                    "Start date cannot be after end date");
        }

        // Ensure date range is no more than 1 month
        if (ChronoUnit.DAYS.between(startDateTime, endDateTime) > 30) {
            throw new IllegalArgumentException(
                    "Date range cannot exceed 1 month");
        }
    }

    private double calculateSensorDate(List<SensorData> data, String metric, String statistic) {
        var values = data.stream()
                .map(d -> getMetricValue(d, metric))
                .toList();

        return switch (statistic.toLowerCase()) {
            case "min" -> Collections.min(values);
            case "max" -> Collections.max(values);
            case "sum" -> values.stream().mapToDouble(Double::doubleValue).sum();
            default -> Math.round(values.stream().mapToDouble(Double::doubleValue).average().orElse(0.0) * 10.0) / 10.0;
        };
    }

    private double getMetricValue(SensorData data, String metric) {
        return switch (metric.toLowerCase()) {
            case "temperature" -> data.getTemperature();
            case "humidity" -> data.getHumidity();
            case "windspeed" -> data.getWindSpeed();
            default -> throw new IllegalArgumentException("Invalid metric: " + metric);
        };
    }
}
