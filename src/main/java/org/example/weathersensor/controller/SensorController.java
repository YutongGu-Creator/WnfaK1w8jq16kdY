package org.example.weathersensor.controller;

import org.example.weathersensor.data.SensorData;
import org.example.weathersensor.service.SensorDataService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/sensors")
public class SensorController {
    private final SensorDataService service;

    public SensorController(SensorDataService service) {
        this.service = service;
    }

    @PostMapping("/data")
    public ResponseEntity<SensorData> addSensorData(@RequestBody SensorData data) {
        return ResponseEntity.ok(service.saveSensorData(data));
    }

    @GetMapping("/metrics")
    public ResponseEntity<Map<String, Double>> getMetrics(
            @RequestParam List<String> sensorId,
            @RequestParam List<String> metrics,
            @RequestParam(defaultValue = "average") String statistic,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate) {

        // If both startDate and endDate are not provided, use the last 24 hours
        if (startDate == null && endDate == null) {
            endDate = new Date();
            startDate = new Date(endDate.getTime() - 24 * 60 * 60 * 1000);
        } else if (endDate == null) { // If only endDate is not provided, use the current date
            endDate = new Date();
        } else if (startDate == null) { // If only startDate is not provided, throw IllegalArgumentException
            throw new IllegalArgumentException("Missing start date, it has to be defined");
        }

        try {
            Map<String, Double> result = service.getMetrics(
                    sensorId,
                    metrics,
                    statistic,
                    Optional.of(startDate),
                    Optional.of(endDate)
            );
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of(e.getMessage(), Double.NaN));
        }
    }
}
