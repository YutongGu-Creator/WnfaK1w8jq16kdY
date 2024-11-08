package org.example.weathersensor.service;

import org.example.weathersensor.data.SensorData;
import org.example.weathersensor.data.SensorDataRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SensorDataServiceTest {

    @Mock
    private SensorDataRepository repository;

    @InjectMocks
    private SensorDataService service;

    AutoCloseable openMocks;

    @BeforeEach
    void setUp() {
        openMocks = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() throws Exception {
        openMocks.close();
    }

    @Test
    void shouldReturnDefaultMetricsWhenDatesAreNull() {
        var sensorId = List.of("sensor1");
        var metrics = List.of("temperature", "humidity", "windspeed");
        var statistic = "average";
        Optional<Date> startDate = Optional.empty();
        Optional<Date> endDate = Optional.empty();

        List<SensorData> data = new ArrayList<>();
        when(repository.findBySensorIdAndTimestampBetween(anyString(), any(Date.class), any(Date.class))).thenReturn(data);

        var result = service.getMetrics(sensorId, metrics, statistic, startDate, endDate);

        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals(0.0, result.get("temperature"));
        assertEquals(0.0, result.get("humidity"));
        assertEquals(0.0, result.get("windspeed"));
    }

    @Test
    void shouldReturnMetricsForGivenDateRange() {
        var sensorId = List.of("sensor1");
        var metrics = List.of("temperature", "humidity", "windspeed");
        var statistic = "average";
        var endDate = new Date();
        var startDate = new Date(endDate.getTime() - 48 * 60 * 60 * 1000);
        List<SensorData> data = populateSensorData(sensorId.get(0));

        when(repository.findBySensorIdAndTimestampBetween(anyString(), any(Date.class), any(Date.class))).thenReturn(data);

        // Test for avg value
        var result = service.getMetrics(sensorId, metrics, statistic, Optional.of(startDate), Optional.of(endDate));
        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals(25.0, result.get("temperature"));
        assertEquals(47.8, result.get("humidity"));
        assertEquals(5.0, result.get("windspeed"));

        // Test for min value
        statistic = "min";
        result = service.getMetrics(sensorId, metrics, statistic, Optional.of(startDate), Optional.of(endDate));
        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals(20.0, result.get("temperature"));
        assertEquals(45.5, result.get("humidity"));
        assertEquals(4.5, result.get("windspeed"));

        // Test for max value
        statistic = "max";
        result = service.getMetrics(sensorId, metrics, statistic, Optional.of(startDate), Optional.of(endDate));
        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals(30.0, result.get("temperature"));
        assertEquals(50.0, result.get("humidity"));
        assertEquals(5.5, result.get("windspeed"));

        // Test for sum value
        statistic = "sum";
        result = service.getMetrics(sensorId, metrics, statistic, Optional.of(startDate), Optional.of(endDate));
        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals(50.0, result.get("temperature"));
        assertEquals(95.5, result.get("humidity"));
        assertEquals(10, result.get("windspeed"));
    }

    @Test
    void shouldThrowExceptionWhenStartDateIsInFuture() {
        var sensorId = List.of("sensor1");
        var metrics = List.of("temperature", "humidity", "windspeed");
        var statistic = "average";
        var endDate = new Date();
        var startDate = new Date(endDate.getTime() + 24 * 60 * 60 * 1000);

        var exception = assertThrows(IllegalArgumentException.class, () -> service.getMetrics(sensorId, metrics, statistic, Optional.of(startDate), Optional.of(endDate)));

        assertEquals("Start date cannot be in the future", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenStartDateIsAfterEndDate() {
        var sensorId = List.of("sensor1");
        var metrics = List.of("temperature", "humidity", "windspeed");
        var statistic = "average";
        var startDate = new Date();
        var endDate = new Date(startDate.getTime() - 60 * 60 * 1000);

        var exception = assertThrows(IllegalArgumentException.class, () -> service.getMetrics(sensorId, metrics, statistic, Optional.of(startDate), Optional.of(endDate)));

        assertEquals("Start date cannot be after end date", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenDateRangeExceedsOneMonth() {
        var sensorId = List.of("sensor1");
        var metrics = List.of("temperature", "humidity", "windspeed");
        var statistic = "average";
        var endDate = new Date();
        var startDate = new Date(endDate.getTime() - 32L * 24 * 60 * 60 * 1000);

        var exception = assertThrows(IllegalArgumentException.class, () -> service.getMetrics(sensorId, metrics, statistic, Optional.of(startDate), Optional.of(endDate)));

        assertEquals("Date range cannot exceed 1 month", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenInvalidMetric() {
        var sensorId = List.of("sensor1");
        var metrics = List.of("invalidMetric");
        var statistic = "average";
        Optional<Date> startDate = Optional.empty();
        Optional<Date> endDate = Optional.empty();

        List<SensorData> data = populateSensorData(sensorId.get(0));
        when(repository.findBySensorIdAndTimestampBetween(anyString(), any(Date.class), any(Date.class))).thenReturn(data);

        var exception = assertThrows(IllegalArgumentException.class, () -> service.getMetrics(sensorId, metrics, statistic, startDate, endDate));

        assertEquals("Invalid metric: invalidMetric", exception.getMessage());
    }

    private static List<SensorData> populateSensorData(String sensorId) {
        List<SensorData> data = new ArrayList<>();

        var testSensorData1 = new SensorData();
        testSensorData1.setSensorId(sensorId);
        testSensorData1.setTemperature(20.0);
        testSensorData1.setHumidity(50.0);
        testSensorData1.setWindSpeed(5.5);
        data.add(testSensorData1);

        var testSensorData2 = new SensorData();
        testSensorData2.setSensorId(sensorId);
        testSensorData2.setTemperature(30.0);
        testSensorData2.setHumidity(45.5);
        testSensorData2.setWindSpeed(4.5);
        data.add(testSensorData2);
        return data;
    }
}